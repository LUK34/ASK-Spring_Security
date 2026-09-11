package kw.kng.security.medasApiSecurity.client;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import kw.kng.security.medasApiSecurity.config.KngMedasApiProperties;
import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolver;
import kw.kng.security.medasApiSecurity.token.KngMedasTokenService;

/**
 * Central HTTP client used by PATMRD to communicate with the secured
 * KNG MEDAS REST API.
 *
 * <p>
 * PRIMARY ROUTE:
 * PATMRD -> API Gateway -> Eureka -> KNG MEDAS REST
 * </p>
 *
 * <p>
 * SECONDARY ROUTE:
 * PATMRD -> Direct MEDAS PRIME / fallback servers
 * </p>
 *
 * <p>
 * IMPORTANT WRITE-SAFETY RULE:
 * GET requests may be replayed through the direct route after a Gateway
 * availability failure because GET is a read operation.
 * POST, PUT and DELETE are NOT automatically replayed after an uncertain
 * Gateway/network/server failure because the original request may already
 * have reached MEDAS and modified data.
 * </p>
 */
@Component
public class KngMedasApiClient {

    private static final Logger log = LoggerFactory.getLogger(KngMedasApiClient.class);

    private final RestTemplate restTemplate;
    private final KngMedasTokenService tokenService;
    private final KngMedasEndpointResolver endpointResolver;
    private final KngMedasApiProperties properties;

    public KngMedasApiClient(
            @Qualifier("kngMedasRestTemplate") RestTemplate restTemplate,
            KngMedasTokenService tokenService,
            KngMedasEndpointResolver endpointResolver,
            KngMedasApiProperties properties) {

        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
        this.endpointResolver = endpointResolver;
        this.properties = properties;
    }

    // ############################################################################################################
    // PUBLIC REST API METHODS
    // ############################################################################################################

    /**
     * Executes GET using Gateway first. If Gateway is unavailable through
     * network failure or 502/503/504, the request is safely retried through
     * the existing direct MEDAS failover chain.
     */
    public <T> T get(String endpoint, Class<T> responseType) {

        String gatewayBaseUrl = properties.getGatewayBaseUrl();

        if (gatewayBaseUrl != null) {
            try {
                log.info("Executing KNG MEDAS GET through API Gateway. Endpoint: {}", endpoint);

                return executeGetWithUnauthorizedRetry(
                        buildUrl(gatewayBaseUrl, endpoint),
                        responseType);

            } catch (HttpClientErrorException ex) {
                // 400 / 401 after refresh / 403 / 404 etc. are not availability failures.
                throw ex;

            } catch (HttpServerErrorException ex) {
                if (!isAvailabilityServerError(ex)) {
                    throw ex;
                }

                log.warn(
                        "KNG MEDAS API Gateway returned infrastructure error {} for GET endpoint {}. "
                                + "Switching to direct MEDAS failover.",
                        ex.getStatusCode(),
                        endpoint);

            } catch (ResourceAccessException ex) {
                log.warn(
                        "KNG MEDAS API Gateway is unavailable for GET endpoint {}. "
                                + "Switching to direct MEDAS failover. Cause: {}",
                        endpoint,
                        ex.getMessage());
            }
        } else {
            log.warn(
                    "KNG MEDAS API Gateway is disabled or not configured. "
                            + "Executing GET through direct MEDAS failover. Endpoint: {}",
                    endpoint);
        }

        return executeDirectGetWithFailover(endpoint, responseType);
    }

    /**
     * Executes POST through Gateway when configured.
     *
     * POST is deliberately NOT replayed through the direct route after an
     * uncertain Gateway/network/server failure because duplicate data may result.
     */
    public <T, R> R post(String endpoint, T requestBody, Class<R> responseType) {

        String gatewayBaseUrl = properties.getGatewayBaseUrl();

        if (gatewayBaseUrl != null) {
            try {
                log.info("Executing KNG MEDAS POST through API Gateway. Endpoint: {}", endpoint);

                return executePostWithUnauthorizedRetry(
                        buildUrl(gatewayBaseUrl, endpoint),
                        requestBody,
                        responseType);

            } catch (HttpClientErrorException ex) {
                throw ex;

            } catch (HttpServerErrorException ex) {
                if (isAvailabilityServerError(ex)) {
                    throw handleUnsafeGatewayRequestFailure("POST", endpoint, ex);
                }
                throw ex;

            } catch (ResourceAccessException ex) {
                throw handleUnsafeGatewayRequestFailure("POST", endpoint, ex);
            }
        }

        log.warn(
                "KNG MEDAS API Gateway is disabled or not configured. "
                        + "Executing POST directly against MEDAS. Endpoint: {}",
                endpoint);

        return executeDirectPostOnce(endpoint, requestBody, responseType);
    }

    /**
     * Executes PUT through Gateway when configured.
     *
     * PUT is deliberately NOT replayed after an uncertain availability failure.
     */
    public <T, R> R put(String endpoint, T requestBody, Class<R> responseType) {

        String gatewayBaseUrl = properties.getGatewayBaseUrl();

        if (gatewayBaseUrl != null) {
            try {
                log.info("Executing KNG MEDAS PUT through API Gateway. Endpoint: {}", endpoint);

                return executePutWithUnauthorizedRetry(
                        buildUrl(gatewayBaseUrl, endpoint),
                        requestBody,
                        responseType);

            } catch (HttpClientErrorException ex) {
                throw ex;

            } catch (HttpServerErrorException ex) {
                if (isAvailabilityServerError(ex)) {
                    throw handleUnsafeGatewayRequestFailure("PUT", endpoint, ex);
                }
                throw ex;

            } catch (ResourceAccessException ex) {
                throw handleUnsafeGatewayRequestFailure("PUT", endpoint, ex);
            }
        }

        log.warn(
                "KNG MEDAS API Gateway is disabled or not configured. "
                        + "Executing PUT directly against MEDAS. Endpoint: {}",
                endpoint);

        return executeDirectPutOnce(endpoint, requestBody, responseType);
    }

    /**
     * Executes DELETE through Gateway when configured.
     *
     * DELETE is deliberately NOT replayed after an uncertain availability failure.
     */
    public void delete(String endpoint) {

        String gatewayBaseUrl = properties.getGatewayBaseUrl();

        if (gatewayBaseUrl != null) {
            try {
                log.info("Executing KNG MEDAS DELETE through API Gateway. Endpoint: {}", endpoint);

                executeDeleteWithUnauthorizedRetry(
                        buildUrl(gatewayBaseUrl, endpoint));

                return;

            } catch (HttpClientErrorException ex) {
                throw ex;

            } catch (HttpServerErrorException ex) {
                if (isAvailabilityServerError(ex)) {
                    throw handleUnsafeGatewayRequestFailure("DELETE", endpoint, ex);
                }
                throw ex;

            } catch (ResourceAccessException ex) {
                throw handleUnsafeGatewayRequestFailure("DELETE", endpoint, ex);
            }
        }

        log.warn(
                "KNG MEDAS API Gateway is disabled or not configured. "
                        + "Executing DELETE directly against MEDAS. Endpoint: {}",
                endpoint);

        executeDirectDeleteOnce(endpoint);
    }

    // ############################################################################################################
    // GATEWAY / COMMON EXECUTION WITH ONE JWT REFRESH
    // ############################################################################################################

    private <T> T executeGetWithUnauthorizedRetry(String url, Class<T> responseType) {
        try {
            return executeGet(url, responseType);
        } catch (HttpClientErrorException.Unauthorized ex) {
            tokenService.invalidateToken();
            return executeGet(url, responseType);
        }
    }

    private <T, R> R executePostWithUnauthorizedRetry(
            String url,
            T requestBody,
            Class<R> responseType) {
        try {
            return executePost(url, requestBody, responseType);
        } catch (HttpClientErrorException.Unauthorized ex) {
            tokenService.invalidateToken();
            return executePost(url, requestBody, responseType);
        }
    }

    private <T, R> R executePutWithUnauthorizedRetry(
            String url,
            T requestBody,
            Class<R> responseType) {
        try {
            return executePut(url, requestBody, responseType);
        } catch (HttpClientErrorException.Unauthorized ex) {
            tokenService.invalidateToken();
            return executePut(url, requestBody, responseType);
        }
    }

    private void executeDeleteWithUnauthorizedRetry(String url) {
        try {
            executeDelete(url);
        } catch (HttpClientErrorException.Unauthorized ex) {
            tokenService.invalidateToken();
            executeDelete(url);
        }
    }

    // ############################################################################################################
    // DIRECT GET FAILOVER
    // ############################################################################################################

    /**
     * Executes a GET through the existing direct MEDAS PRIME/fallback chain.
     */
    private <T> T executeDirectGetWithFailover(String endpoint, Class<T> responseType) {

        Exception lastException = null;
        List<String> candidateUrls = endpointResolver.getCandidateBaseUrls();

        if (candidateUrls == null || candidateUrls.isEmpty()) {
            throw new IllegalStateException("No direct KNG MEDAS REST API server is configured.");
        }

        for (String baseUrl : candidateUrls) {
            String url = buildUrl(baseUrl, endpoint);

            try {
                log.info("Attempting direct KNG MEDAS GET through server: {}", baseUrl);

                T result = executeGetWithUnauthorizedRetry(url, responseType);

                endpointResolver.markActive(baseUrl);

                log.info("Direct KNG MEDAS GET succeeded through server: {}", baseUrl);

                return result;

            } catch (HttpClientErrorException ex) {
                // A direct server responded. Do not hide request/auth/application errors by switching servers.
                throw ex;

            } catch (HttpServerErrorException ex) {
                if (!isAvailabilityServerError(ex)) {
                    throw ex;
                }

                log.warn(
                        "Direct KNG MEDAS server {} returned infrastructure error {} for GET endpoint {}. "
                                + "Trying next configured server.",
                        baseUrl,
                        ex.getStatusCode(),
                        endpoint);

                invalidateDirectServer(baseUrl);
                lastException = ex;

            } catch (ResourceAccessException ex) {
                log.warn(
                        "Unable to reach direct KNG MEDAS server {} for GET endpoint {}. "
                                + "Trying next configured server. Cause: {}",
                        baseUrl,
                        endpoint,
                        ex.getMessage());

                invalidateDirectServer(baseUrl);
                lastException = ex;
            }
        }

        log.error("No direct KNG MEDAS REST API server was available for GET endpoint: {}", endpoint);

        throw new IllegalStateException(
                "No direct KNG MEDAS REST API server was available for GET endpoint: " + endpoint,
                lastException);
    }

    // ############################################################################################################
    // DIRECT WRITE EXECUTION - NO AUTOMATIC REPLAY
    // ############################################################################################################

    private <T, R> R executeDirectPostOnce(
            String endpoint,
            T requestBody,
            Class<R> responseType) {

        String baseUrl = resolveDirectBaseUrl();
        String url = buildUrl(baseUrl, endpoint);

        try {
            R result = executePostWithUnauthorizedRetry(url, requestBody, responseType);
            endpointResolver.markActive(baseUrl);
            return result;
        } catch (HttpServerErrorException ex) {
            if (isAvailabilityServerError(ex)) {
                throw handleUnsafeDirectRequestFailure("POST", endpoint, baseUrl, ex);
            }
            throw ex;
        } catch (ResourceAccessException ex) {
            throw handleUnsafeDirectRequestFailure("POST", endpoint, baseUrl, ex);
        }
    }

    private <T, R> R executeDirectPutOnce(
            String endpoint,
            T requestBody,
            Class<R> responseType) {

        String baseUrl = resolveDirectBaseUrl();
        String url = buildUrl(baseUrl, endpoint);

        try {
            R result = executePutWithUnauthorizedRetry(url, requestBody, responseType);
            endpointResolver.markActive(baseUrl);
            return result;
        } catch (HttpServerErrorException ex) {
            if (isAvailabilityServerError(ex)) {
                throw handleUnsafeDirectRequestFailure("PUT", endpoint, baseUrl, ex);
            }
            throw ex;
        } catch (ResourceAccessException ex) {
            throw handleUnsafeDirectRequestFailure("PUT", endpoint, baseUrl, ex);
        }
    }

    private void executeDirectDeleteOnce(String endpoint) {

        String baseUrl = resolveDirectBaseUrl();
        String url = buildUrl(baseUrl, endpoint);

        try {
            executeDeleteWithUnauthorizedRetry(url);
            endpointResolver.markActive(baseUrl);
        } catch (HttpServerErrorException ex) {
            if (isAvailabilityServerError(ex)) {
                throw handleUnsafeDirectRequestFailure("DELETE", endpoint, baseUrl, ex);
            }
            throw ex;
        } catch (ResourceAccessException ex) {
            throw handleUnsafeDirectRequestFailure("DELETE", endpoint, baseUrl, ex);
        }
    }

    // ############################################################################################################
    // LOW-LEVEL HTTP EXECUTION
    // ############################################################################################################

    private <T> T executeGet(String url, Class<T> responseType) {
        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                responseType);

        return response.getBody();
    }

    private <T, R> R executePost(String url, T requestBody, Class<R> responseType) {
        HttpHeaders headers = createAuthenticatedHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<R> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                responseType);

        return response.getBody();
    }

    private <T, R> R executePut(String url, T requestBody, Class<R> responseType) {
        HttpHeaders headers = createAuthenticatedHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<R> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                responseType);

        return response.getBody();
    }

    private void executeDelete(String url) {
        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                entity,
                Void.class);
    }

    // ############################################################################################################
    // FAILURE / ROUTE HELPERS
    // ############################################################################################################

    private boolean isAvailabilityServerError(HttpServerErrorException ex) {
        return ex.getStatusCode() == HttpStatus.BAD_GATEWAY
                || ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE
                || ex.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT;
    }

    /**
     * Gateway write failure. The write is NOT replayed directly because the
     * request outcome may be unknown.
     */
    private IllegalStateException handleUnsafeGatewayRequestFailure(
            String httpMethod,
            String endpoint,
            Exception cause) {

        tokenService.invalidateToken();

        log.error(
                "KNG MEDAS {} through API Gateway failed for endpoint {}. "
                        + "The request was NOT automatically replayed directly to MEDAS.",
                httpMethod,
                endpoint);

        return new IllegalStateException(
                "KNG MEDAS API Gateway became unavailable during "
                        + httpMethod
                        + " request to endpoint: "
                        + endpoint
                        + ". The request was not automatically retried through direct MEDAS "
                        + "to avoid duplicate or inconsistent data.",
                cause);
    }

    /**
     * Direct write failure. The current direct server is invalidated, but the
     * write is NOT replayed against another direct server.
     */
    private IllegalStateException handleUnsafeDirectRequestFailure(
            String httpMethod,
            String endpoint,
            String failedBaseUrl,
            Exception cause) {

        invalidateDirectServer(failedBaseUrl);
        tokenService.invalidateToken();

        log.error(
                "Direct KNG MEDAS {} failed through server {} for endpoint {}. "
                        + "The request was NOT replayed against another MEDAS server.",
                httpMethod,
                failedBaseUrl,
                endpoint);

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

    private void invalidateDirectServer(String baseUrl) {
        if (baseUrl != null) {
            endpointResolver.invalidate(baseUrl);
        }
    }

    /**
     * Resolves one direct server for a non-replayable write.
     *
     * Prefer the remembered active direct server. If none exists, use the first
     * configured direct server. Token acquisition may itself select/mark a direct
     * server when Gateway is disabled.
     */
    private String resolveDirectBaseUrl()
    {
        /*
         * If a working direct MEDAS server is already known,
         * continue using it.
         */
        String activeBaseUrl =
                endpointResolver.getActiveBaseUrl();

        if (activeBaseUrl != null
                && !activeBaseUrl.trim().isEmpty())
        {
            return activeBaseUrl;
        }

        /*
         * No direct server is currently selected.
         *
         * Force a fresh authentication cycle.
         *
         * When this method is used, Gateway is disabled/not configured.
         * Therefore KngMedasAuthClient will execute the existing
         * PRIME -> fallback authentication sequence and mark the
         * first working direct MEDAS server as active.
         */
        tokenService.invalidateToken();

        tokenService.getValidToken();

        activeBaseUrl =   endpointResolver.getActiveBaseUrl();

        if (activeBaseUrl == null
                || activeBaseUrl.trim().isEmpty())
        {
            throw new IllegalStateException(
                    "No working direct KNG MEDAS REST API server could be selected.");
        }

        return activeBaseUrl;
    }

    // ############################################################################################################
    // HEADER / URL HELPERS
    // ############################################################################################################

    private HttpHeaders createAuthenticatedHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(HttpHeaders.AUTHORIZATION, tokenService.getAuthorizationHeaderValue());

        return headers;
    }

    /**
     * Combines a supplied application base URL with a relative REST endpoint.
     *
     * Gateway example:
     * base URL = http://localhost:8888/kng_medas
     * endpoint = /appointments_rest/create
     *
     * Direct example:
     * base URL = http://10.201.49.120:8080/kng_medas
     * endpoint = /appointments_rest/create
     */
    private String buildUrl(String baseUrl, String endpoint) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalStateException("KNG MEDAS base URL is not available.");
        }

        String normalizedBaseUrl = baseUrl.trim();

        while (normalizedBaseUrl.endsWith("/")) {
            normalizedBaseUrl = normalizedBaseUrl.substring(0, normalizedBaseUrl.length() - 1);
        }

        if (endpoint == null || endpoint.trim().isEmpty()) {
            return normalizedBaseUrl;
        }

        String normalizedEndpoint = endpoint.trim();

        return normalizedBaseUrl
                + (normalizedEndpoint.startsWith("/")
                        ? normalizedEndpoint
                        : "/" + normalizedEndpoint);
    }
}
