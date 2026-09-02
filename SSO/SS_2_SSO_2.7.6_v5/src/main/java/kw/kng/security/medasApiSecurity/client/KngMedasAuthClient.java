package kw.kng.security.medasApiSecurity.client;

import java.util.List;

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

@Component
public class KngMedasAuthClient {
	private final RestTemplate restTemplate;
	private final KngMedasApiProperties properties;
	private final KngMedasEndpointResolver endpointResolver;

	public KngMedasAuthClient(@Qualifier("kngMedasRestTemplate") RestTemplate restTemplate,
							  KngMedasApiProperties properties,
							  KngMedasEndpointResolver endpointResolver) 
	{
		this.restTemplate = restTemplate;
		this.properties = properties;
		this.endpointResolver = endpointResolver;
	}

	public MedasAuthResponseDto authenticate() {
		MedasAuthRequestDto request = new MedasAuthRequestDto(properties.getAppName(), properties.getUsername(),
				properties.getPassword());

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<MedasAuthRequestDto> entity = new HttpEntity<>(request, headers);

		Exception lastException = null;

		List<String> candidateUrls = endpointResolver.getCandidateBaseUrls();

		for (String baseUrl : candidateUrls) {
			String tokenUrl = properties.buildTokenUrl(baseUrl);

			try {
				ResponseEntity<MedasAuthResponseDto> response = restTemplate.postForEntity(tokenUrl, entity,
						MedasAuthResponseDto.class);

				MedasAuthResponseDto body = response.getBody();

				if (response.getStatusCode().is2xxSuccessful() && body != null && body.getToken() != null
						&& !body.getToken().trim().isEmpty()) {
					/*
					 * This MEDAS server is working.
					 */
					endpointResolver.markActive(baseUrl);

					return body;
				}
			} catch (HttpClientErrorException ex) {
				/*
				 * 401/403 means the MEDAS server itself responded correctly.
				 *
				 * This is probably a credential/configuration problem, NOT a server
				 * availability issue.
				 */
				if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN) {
					throw ex;
				}

				/*
				 * 404 can mean kng_medas WAR is not deployed on this Tomcat.
				 */
				lastException = ex;
			} catch (HttpServerErrorException ex) {
				/*
				 * 5xx: try another MEDAS server.
				 */
				lastException = ex;
			} catch (ResourceAccessException ex) {
				/*
				 * Connection refused, timeout, host unreachable, etc.
				 */
				lastException = ex;
			}
		}

		throw new IllegalStateException("No available KNG MEDAS REST API server was found.", lastException);
	}

}
