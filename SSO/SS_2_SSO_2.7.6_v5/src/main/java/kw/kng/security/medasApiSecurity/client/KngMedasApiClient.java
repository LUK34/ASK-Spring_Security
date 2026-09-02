package kw.kng.security.medasApiSecurity.client;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolver;
import kw.kng.security.medasApiSecurity.token.KngMedasTokenService;

/**
 * Central HTTP client used by PATMRD to communicate with the secured
 * KNG MEDAS REST API.
 *
 * <p>
 * This class hides HTTP communication, JWT handling and runtime failover
 * details from the application business/service layer.
 * </p>
 *
 * <p>
 * High-level flow:
 * </p>
 *
 * <pre>
 * PATMRD Service
 *      |
 *      v
 * KngMedasApiClient
 *      |
 *      +-- KngMedasTokenService
 *      |       -> obtains / caches / invalidates JWT
 *      |
 *      +-- KngMedasEndpointResolver
 *      |       -> tracks the active MEDAS server
 *      |
 *      +-- kngMedasRestTemplate
 *              -> executes the HTTP request
 *      |
 *      v
 * KNG MEDAS REST API
 * </pre>
 *
 * <p>
 * IMPORTANT FAILOVER RULE:
 * GET requests may be retried after an availability failure because they are
 * read operations. POST, PUT and DELETE requests are NOT automatically
 * replayed after an uncertain network/server failure because the first server
 * may already have committed the database change.
 * </p>
 */
@Component
public class KngMedasApiClient 
{
	/*
	 * Dedicated RestTemplate configured for KNG MEDAS REST API communication.
	 * Connect/read timeout values are configured in KngMedasApiConfig.
	 */
	private final RestTemplate restTemplate;
	/*
	 * Handles the JWT lifecycle used for server-to-server authentication.
	 */
	private final KngMedasTokenService tokenService;
	/*
	 * Maintains the currently active KNG MEDAS server and supports
	 * prime -> fallback server resolution.
	 */
	private final KngMedasEndpointResolver endpointResolver;

	/**
	 * Constructor injection for the MEDAS HTTP client dependencies.
	 *
	 * @param restTemplate dedicated KNG MEDAS RestTemplate
	 * @param tokenService JWT token service
	 * @param endpointResolver active/fallback MEDAS endpoint resolver
	 */
	public KngMedasApiClient(@Qualifier("kngMedasRestTemplate") RestTemplate restTemplate,
			KngMedasTokenService tokenService, KngMedasEndpointResolver endpointResolver) {
		this.restTemplate = restTemplate;
		this.tokenService = tokenService;
		this.endpointResolver = endpointResolver;
	}

	// ############################################################################################################
	// GENERIC REST API METHODS
	// ############################################################################################################

	/**
	 * Executes a GET request against the currently active KNG MEDAS REST API
	 * server.
	 *
	 * Runtime failover behaviour:
	 *
	 * 1. Use currently active MEDAS server.
	 * 2. If JWT is rejected with 401, refresh JWT and retry once.
	 * 3. If the active server is unavailable, invalidate that server and JWT.
	 * 4. Authentication searches PRIME -> fallbacks again.
	 * 5. Retry the GET once against the newly selected server.
	 */
	public <T> T get(String endpoint, Class<T> responseType)
	{
	    try
	    {
	        return executeGet(endpoint, responseType);
	    }
	    catch (HttpClientErrorException.Unauthorized ex)
	    {
	        /*
	         * Server responded, but cached JWT is no longer accepted.
	         * Refresh JWT and retry once.
	         */
	        tokenService.invalidateToken();

	        return executeGet(endpoint, responseType);
	    }
	    catch (HttpClientErrorException ex)
	    {
	        /*
	         * Other 4xx responses prove that MEDAS responded.
	         *
	         * These normally indicate request/security/business problems.
	         * Do NOT switch servers.
	         */
	        throw ex;
	    }
	    catch (HttpServerErrorException ex)
	    {
	        /*
	         * Fail over only for infrastructure/service availability errors.
	         */
	        if (isAvailabilityServerError(ex))
	        {
	            return retryGetAfterServerFailure(
	                    endpoint,
	                    responseType);
	        }

	        /*
	         * A normal HTTP 500 can represent application,
	         * database or business logic failure.
	         */
	        throw ex;
	    }
	    catch (ResourceAccessException ex)
	    {
	        /*
	         * Connection refused,
	         * connection timeout,
	         * read timeout,
	         * host unreachable, etc.
	         */
	        return retryGetAfterServerFailure(
	                endpoint,
	                responseType);
	    }
	}


	/**
	 * Executes POST.
	 *
	 * POST is NOT automatically replayed after server/network failure
	 * because the original request may already have modified data.
	 */
	public <T, R> R post(
	        String endpoint,
	        T requestBody,
	        Class<R> responseType)
	{
	    try
	    {
	        return executePost(
	                endpoint,
	                requestBody,
	                responseType);
	    }
	    catch (HttpClientErrorException.Unauthorized ex)
	    {
	        /*
	         * Server responded successfully at network level.
	         * Only JWT authentication failed.
	         */
	        tokenService.invalidateToken();

	        return executePost(
	                endpoint,
	                requestBody,
	                responseType);
	    }
	    catch (HttpClientErrorException ex)
	    {
	        /*
	         * All other 4xx responses prove that MEDAS responded.
	         *
	         * Examples:
	         * 400 - bad request
	         * 403 - forbidden
	         * 404 - resource/endpoint not found
	         *
	         * Do NOT switch servers.
	         */
	        throw ex;
	    }
	    catch (HttpServerErrorException ex)
	    {
	        /*
	         * Only treat infrastructure-related 5xx statuses
	         * as server availability failures.
	         */
	        if (isAvailabilityServerError(ex))
	        {
	            throw handleUnsafeRequestServerFailure(
	                    "POST",
	                    endpoint,
	                    ex);
	        }

	        /*
	         * HTTP 500 may be application/database/business failure.
	         */
	        throw ex;
	    }
	    catch (ResourceAccessException ex)
	    {
	        /*
	         * The request outcome may be unknown.
	         *
	         * Invalidate the server/JWT but DO NOT replay POST,
	         * because that could create duplicate data.
	         */
	        throw handleUnsafeRequestServerFailure(
	                "POST",
	                endpoint,
	                ex);
	    }
	}


	/**
	 * Executes PUT.
	 *
	 * PUT is NOT automatically replayed after an uncertain
	 * network/server failure.
	 */
	public <T, R> R put(
	        String endpoint,
	        T requestBody,
	        Class<R> responseType)
	{
	    try
	    {
	        return executePut(
	                endpoint,
	                requestBody,
	                responseType);
	    }
	    catch (HttpClientErrorException.Unauthorized ex)
	    {
	        tokenService.invalidateToken();

	        return executePut(
	                endpoint,
	                requestBody,
	                responseType);
	    }
	    catch (HttpClientErrorException ex)
	    {
	        /*
	         * MEDAS responded with a 4xx.
	         *
	         * Do NOT fail over.
	         */
	        throw ex;
	    }
	    catch (HttpServerErrorException ex)
	    {
	        if (isAvailabilityServerError(ex))
	        {
	            throw handleUnsafeRequestServerFailure(
	                    "PUT",
	                    endpoint,
	                    ex);
	        }

	        /*
	         * Do not interpret every HTTP 500 as server failure.
	         */
	        throw ex;
	    }
	    catch (ResourceAccessException ex)
	    {
	        /*
	         * PUT may already have been processed by the original server.
	         * Do not automatically replay it.
	         */
	        throw handleUnsafeRequestServerFailure(
	                "PUT",
	                endpoint,
	                ex);
	    }
	}


	/**
	 * Executes DELETE.
	 *
	 * DELETE is NOT automatically replayed after an uncertain
	 * network/server failure.
	 */
	public void delete(String endpoint)
	{
	    try
	    {
	        executeDelete(endpoint);
	    }
	    catch (HttpClientErrorException.Unauthorized ex)
	    {
	        tokenService.invalidateToken();

	        executeDelete(endpoint);
	    }
	    catch (HttpClientErrorException ex)
	    {
	        /*
	         * MEDAS responded with a 4xx.
	         *
	         * Do NOT fail over.
	         */
	        throw ex;
	    }
	    catch (HttpServerErrorException ex)
	    {
	        if (isAvailabilityServerError(ex))
	        {
	            throw handleUnsafeRequestServerFailure(
	                    "DELETE",
	                    endpoint,
	                    ex);
	        }

	        throw ex;
	    }
	    catch (ResourceAccessException ex)
	    {
	        /*
	         * DELETE may already have been completed on the original server.
	         * Do not automatically execute it against another server.
	         */
	        throw handleUnsafeRequestServerFailure(
	                "DELETE",
	                endpoint,
	                ex);
	    }
	}

	// ############################################################################################################
	// INTERNAL EXECUTION METHODS
	// ############################################################################################################

	/**
	 * Performs the actual authenticated GET request.
	 *
	 * Retry/failover decisions are intentionally handled by the public get()
	 * method.
	 */
	private <T> T executeGet(String endpoint, Class<T> responseType) {
		HttpHeaders headers = createAuthenticatedHeaders();

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<T> response = restTemplate.exchange(buildUrl(endpoint), HttpMethod.GET, entity, responseType);

		return response.getBody();
	}

	/**
	 * Performs the actual authenticated JSON POST request.
	 */
	private <T, R> R executePost(String endpoint, T requestBody, Class<R> responseType) {
		HttpHeaders headers = createAuthenticatedHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<R> response = restTemplate.exchange(buildUrl(endpoint), HttpMethod.POST, entity, responseType);

		return response.getBody();
	}

	/**
	 * Performs the actual authenticated JSON PUT request.
	 */
	private <T, R> R executePut(String endpoint, T requestBody, Class<R> responseType) {
		HttpHeaders headers = createAuthenticatedHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<R> response = restTemplate.exchange(buildUrl(endpoint), HttpMethod.PUT, entity, responseType);

		return response.getBody();
	}

	/**
	 * Performs the actual authenticated DELETE request.
	 */
	private void executeDelete(String endpoint) {
		HttpHeaders headers = createAuthenticatedHeaders();

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		restTemplate.exchange(buildUrl(endpoint), HttpMethod.DELETE, entity, Void.class);
	}

	// ############################################################################################################
	// RUNTIME FAILOVER METHODS
	// ############################################################################################################

	/**
	 * Safe retry path used for GET requests.
	 */
	private <T> T retryGetAfterServerFailure(
	        String endpoint,
	        Class<T> responseType)
	{
	    invalidateCurrentServer();

	    /*
	     * createAuthenticatedHeaders() inside executeGet()
	     * will request another JWT.
	     *
	     * KngMedasAuthClient then performs:
	     *
	     * PRIME -> fallback[0] -> fallback[1] -> ...
	     */
	    return executeGet(
	            endpoint,
	            responseType);
	}


	/**
	 * Determines whether a server-side HTTP status represents
	 * an infrastructure/service availability problem.
	 *
	 * Only 502, 503 and 504 are considered availability failures here.
	 * A generic 500 is left to the caller because it may represent an
	 * application/database/business error rather than an unavailable server.
	 */
	private boolean isAvailabilityServerError(
	        HttpServerErrorException ex)
	{
	    return ex.getStatusCode() == HttpStatus.BAD_GATEWAY
	            || ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE
	            || ex.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT;
	}


	/**
	 * Used for POST / PUT / DELETE failures.
	 *
	 * The active server and JWT are invalidated so that the NEXT
	 * MEDAS call can resolve another server.
	 *
	 * The current write request is deliberately NOT replayed.
	 */
	private IllegalStateException handleUnsafeRequestServerFailure(
	        String httpMethod,
	        String endpoint,
	        Exception cause)
	{
	    String failedBaseUrl =
	            endpointResolver.getActiveBaseUrl();

	    invalidateCurrentServer();

	    return new IllegalStateException(
	            "KNG MEDAS REST API server became unavailable during "
	                    + httpMethod
	                    + " request to endpoint: "
	                    + endpoint
	                    + ". Failed server: "
	                    + failedBaseUrl
	                    + ". The request was not automatically retried "
	                    + "to avoid duplicate or inconsistent data.",
	            cause);
	}


	/**
	 * Invalidates:
	 *
	 * 1. Currently selected MEDAS server.
	 * 2. JWT associated with that server.
	 *
	 * The next MEDAS call will therefore trigger endpoint resolution and
	 * authentication again.
	 */
	private void invalidateCurrentServer()
	{
	    String failedBaseUrl =
	            endpointResolver.getActiveBaseUrl();

	    if (failedBaseUrl != null)
	    {
	        endpointResolver.invalidate(
	                failedBaseUrl);
	    }
	    else
	    {
	        endpointResolver.invalidate();
	    }

	    tokenService.invalidateToken();
	}

	// ############################################################################################################
	// HEADER / URL METHODS
	// ############################################################################################################

	/**
	 * Creates the common HTTP headers required by protected KNG MEDAS endpoints.
	 *
	 * The Authorization header value is supplied by KngMedasTokenService so
	 * business/service code never handles the JWT directly.
	 */
	private HttpHeaders createAuthenticatedHeaders()
	{
		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		headers.set(HttpHeaders.AUTHORIZATION, tokenService.getAuthorizationHeaderValue());

		return headers;
	}

	/**
	 * Combines the currently active MEDAS application base URL with a relative
	 * REST endpoint.
	 *
	 * Example:
	 *
	 * active base URL = http://server:8080/kng_medas
	 * endpoint        = /appointments_rest/create
	 *
	 * final URL       = http://server:8080/kng_medas/appointments_rest/create
	 */
	private String buildUrl(String endpoint) 
	{
		String baseUrl = endpointResolver.getActiveBaseUrl();

		if (baseUrl == null || baseUrl.trim().isEmpty()) 
		{
			throw new IllegalStateException("No active KNG MEDAS REST API server is available.");
		}
		
		/*
		 * Remove trailing slash characters before appending the endpoint.
		 */
		while (baseUrl.endsWith("/")) 
		{
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}

		/*
		 * If no endpoint was supplied, return only the active base URL.
		 */
		if (endpoint == null || endpoint.trim().isEmpty()) {
			return baseUrl;
		}

		/*
		 * Ensure exactly one slash exists between base URL and endpoint.
		 */
		return baseUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
	}
}