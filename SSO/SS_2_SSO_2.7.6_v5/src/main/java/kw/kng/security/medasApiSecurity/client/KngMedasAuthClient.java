package kw.kng.security.medasApiSecurity.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import kw.kng.security.medasApiSecurity.config.KngMedasApiProperties;
import kw.kng.security.medasApiSecurity.dto.MedasAuthRequestDto;
import kw.kng.security.medasApiSecurity.dto.MedasAuthResponseDto;
import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolver;

/**
 * Handles authentication against KNG MEDAS REST API.
 *
 * PRIMARY ROUTE: PATMRD -> API Gateway -> Eureka -> MEDAS REST
 *
 * SECONDARY ROUTE: PATMRD -> Direct MEDAS prime/fallback servers
 */
@Component
public class KngMedasAuthClient {
	private static final Logger log = LoggerFactory.getLogger(KngMedasAuthClient.class);

	private final RestTemplate restTemplate;
	private final KngMedasApiProperties properties;
	private final KngMedasEndpointResolver endpointResolver;

	public KngMedasAuthClient(@Qualifier("kngMedasRestTemplate") RestTemplate restTemplate,
			KngMedasApiProperties properties, KngMedasEndpointResolver endpointResolver) {
		this.restTemplate = restTemplate;
		this.properties = properties;
		this.endpointResolver = endpointResolver;
	}

	// ############################################################################################################
	// PRIMARY AUTHENTICATION WORKFLOW
	// ############################################################################################################

	public MedasAuthResponseDto authenticate() {
		MedasAuthRequestDto request = new MedasAuthRequestDto(properties.getAppName(), properties.getUsername(),
				properties.getPassword());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<MedasAuthRequestDto> entity = new HttpEntity<>(request, headers);

		// ========================================================================================================
		// PRIMARY ROUTE -> API GATEWAY
		// ========================================================================================================

		String gatewayBaseUrl = properties.getGatewayBaseUrl();

		if (gatewayBaseUrl != null) {
			String gatewayTokenUrl = properties.buildTokenUrl(gatewayBaseUrl);

			try {
				log.info("Attempting KNG MEDAS authentication through API Gateway: {}", gatewayBaseUrl);

				ResponseEntity<MedasAuthResponseDto> response = restTemplate.postForEntity(gatewayTokenUrl, entity,
						MedasAuthResponseDto.class);

				MedasAuthResponseDto body = response.getBody();

				if (isValidAuthenticationResponse(response, body)) {
					log.info("KNG MEDAS authentication through API Gateway succeeded.");

					return body;
				}

				/*
				 * Technically a 2xx response without a token should not happen. Treat it as an
				 * invalid authentication response.
				 */
				log.error("API Gateway returned a successful HTTP response, "
						+ "but no valid KNG MEDAS token was present.");

				throw new IllegalStateException("KNG MEDAS authentication returned an invalid response.");
			} catch (HttpClientErrorException ex) {
				/*
				 * 401 / 403:
				 *
				 * Gateway successfully reached MEDAS. Authentication was rejected.
				 *
				 * DO NOT bypass the Gateway.
				 */
				if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN) {
					log.warn("KNG MEDAS authentication rejected through API Gateway. " + "HTTP status: {}",
							ex.getStatusCode());

					throw ex;
				}

				/*
				 * 400 / 404 and other client-side errors are normally configuration/request
				 * problems.
				 *
				 * Do not silently bypass Gateway.
				 */
				log.error("KNG MEDAS API Gateway authentication request failed. " + "HTTP status: {}",
						ex.getStatusCode());

				throw ex;
			} catch (HttpServerErrorException ex) {
				/*
				 * 503 is treated as Gateway/backend unavailability. Therefore use direct MEDAS
				 * fallback.
				 */
				if (ex.getStatusCode() == HttpStatus.BAD_GATEWAY || ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE
						|| ex.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
					log.warn("KNG MEDAS API Gateway returned infrastructure error {}. "
							+ "Switching to direct MEDAS failover.", ex.getStatusCode());
				} else {
					/*
					 * Other 5xx errors may indicate an application/server error, not necessarily
					 * that Gateway itself is unavailable.
					 */
					log.error("KNG MEDAS API Gateway returned server error: {}", ex.getStatusCode());

					throw ex;
				}
			} catch (ResourceAccessException ex) {
				/*
				 * Typical cases: - Gateway port unavailable - Connection refused - Connect
				 * timeout - Read timeout - Host unreachable
				 */
				log.warn("KNG MEDAS API Gateway is unavailable. " + "Switching to direct MEDAS failover. Cause: {}",
						ex.getMessage());
			}
		} else {
			log.warn("KNG MEDAS API Gateway is disabled or not configured. " + "Using direct MEDAS failover.");
		}

		// ========================================================================================================
		// SECONDARY ROUTE -> EXISTING DIRECT MEDAS FAILOVER
		// ========================================================================================================

		return authenticateUsingDirectFailover(entity);
	}

	// ############################################################################################################
	// SECONDARY DIRECT MEDAS FAILOVER
	// ############################################################################################################

	private MedasAuthResponseDto authenticateUsingDirectFailover(HttpEntity<MedasAuthRequestDto> entity) {
		Exception lastException = null;

		List<String> candidateUrls = endpointResolver.getCandidateBaseUrls();

		for (String baseUrl : candidateUrls) {
			String tokenUrl = properties.buildTokenUrl(baseUrl);

			try {
				log.info("Attempting direct KNG MEDAS authentication through server: {}", baseUrl);

				ResponseEntity<MedasAuthResponseDto> response = restTemplate.postForEntity(tokenUrl, entity,
						MedasAuthResponseDto.class);

				MedasAuthResponseDto body = response.getBody();

				if (isValidAuthenticationResponse(response, body)) {
					/*
					 * Direct MEDAS server is working. Remember it so future direct-fallback calls
					 * can try this server first.
					 */
					endpointResolver.markActive(baseUrl);

					log.info("Direct KNG MEDAS authentication succeeded through server: {}", baseUrl);

					return body;
				}

				log.warn("Direct KNG MEDAS server {} returned an invalid authentication response.", baseUrl);
			} catch (HttpClientErrorException ex) {
				/*
				 * 401 / 403 means MEDAS responded correctly. Credentials/configuration are the
				 * problem.
				 *
				 * Do not continue trying other servers.
				 */
				if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN) {
					log.warn("Direct KNG MEDAS authentication rejected by server {}. " + "HTTP status: {}", baseUrl,
							ex.getStatusCode());

					throw ex;
				}

				/*
				 * Example: 404 may indicate kng_medas WAR is not available on this Tomcat
				 * server.
				 */
				log.warn("Direct KNG MEDAS server {} returned HTTP {}. " + "Trying next configured server.", baseUrl,
						ex.getStatusCode());

				lastException = ex;
			} catch (HttpServerErrorException ex) {
				/*
				 * The current direct MEDAS server returned a server-side error.
				 *
				 * This is the SECONDARY direct failover workflow, so try the next configured
				 * MEDAS server.
				 */
				log.warn("Direct KNG MEDAS server {} returned HTTP {}. " + "Trying next configured server.", baseUrl,
						ex.getStatusCode());

				lastException = ex;

			} catch (ResourceAccessException ex) {
				log.warn("Unable to reach direct KNG MEDAS server {}. " + "Trying next configured server. Cause: {}",
						baseUrl, ex.getMessage());

				lastException = ex;
			}
		}

		/*
		 * We reach this point only when:
		 *
		 * Gateway failed/unavailable AND all direct MEDAS servers also failed.
		 */
		log.error(
				"No available KNG MEDAS REST API server was found " + "through API Gateway or direct MEDAS failover.");

		throw new IllegalStateException("No available KNG MEDAS REST API server was found.", lastException);
	}

	// ############################################################################################################
	// HELPER METHODS
	// ############################################################################################################

	private boolean isValidAuthenticationResponse(ResponseEntity<MedasAuthResponseDto> response,
			MedasAuthResponseDto body) {
		return response != null && response.getStatusCode().is2xxSuccessful() && body != null && body.getToken() != null
				&& !body.getToken().trim().isEmpty();
	}
}