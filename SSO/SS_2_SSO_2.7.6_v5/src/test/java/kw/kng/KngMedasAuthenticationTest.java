package kw.kng;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import kw.kng.security.medasApiSecurity.client.KngMedasAuthClient;
import kw.kng.security.medasApiSecurity.config.KngMedasApiConfig;
import kw.kng.security.medasApiSecurity.config.KngMedasApiProperties;
import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolver;
import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolverImpl;
import kw.kng.security.medasApiSecurity.token.KngMedasTokenService;
import kw.kng.security.medasApiSecurity.token.KngMedasTokenServiceImpl;

//@Disabled("Manual KNG MEDAS integration test - do not run during normal WAR build")
@SpringBootTest(classes = { KngMedasApiConfig.class, 
							KngMedasEndpointResolverImpl.class,
							KngMedasAuthClient.class,
							KngMedasTokenServiceImpl.class, 
							KngMedasAuthenticationTest.TestConfig.class 
							}
				)
@ActiveProfiles("prod")
public class KngMedasAuthenticationTest {
	@Autowired
	private KngMedasTokenService tokenService;

	@Autowired
	private KngMedasApiProperties properties;

	@Autowired
	private KngMedasEndpointResolver endpointResolver;

	@Test
	public void testKngMedasAuthentication() {
		System.out.println("=======================================================");
		System.out.println("KNG MEDAS JWT AUTHENTICATION TEST -> START");
		System.out.println("=======================================================");

		// -------------------------------------------------------------
		// CONFIGURATION CHECK
		// -------------------------------------------------------------

		System.out.println("-------------------------------------------------------");
		System.out.println("MEDAS CONFIGURATION CHECK");
		System.out.println("-------------------------------------------------------");

		System.out.println("Auth URL  = " + properties.getAuthUrl());
		System.out.println("WAR       = " + properties.getWar());
		System.out.println("App Name  = " + properties.getAppName());

		System.out.println("Username configured = "
				+ (properties.getUsername() != null && !properties.getUsername().trim().isEmpty()));

		System.out.println("Password configured = "
				+ (properties.getPassword() != null && !properties.getPassword().trim().isEmpty()));

		System.out.println("-------------------------------------------------------");
		System.out.println("MEDAS CANDIDATE SERVERS");
		System.out.println("-------------------------------------------------------");

		for (String baseUrl : properties.getCandidateBaseUrls()) {
			System.out.println("Base URL  = " + baseUrl);
			System.out.println("Token URL = " + properties.buildTokenUrl(baseUrl));
		}

		System.out.println("-------------------------------------------------------");

		// -------------------------------------------------------------
		// ACT
		// -------------------------------------------------------------

		String token = tokenService.getValidToken();
		String activeBaseUrl = endpointResolver.getActiveBaseUrl();

		// -------------------------------------------------------------
		// ASSERT
		// -------------------------------------------------------------

		assertNotNull(token, "KNG MEDAS JWT token should not be null.");
		assertFalse(token.trim().isEmpty(), "KNG MEDAS JWT token should not be empty.");
		assertNotNull(activeBaseUrl, "An active KNG MEDAS REST API server should have been selected.");
		assertFalse(activeBaseUrl.trim().isEmpty(), "The active KNG MEDAS REST API server should not be empty.");

		/*
		 * SECURITY: Never print the actual JWT token in the console/log.
		 */
		System.out.println("KNG MEDAS JWT AUTHENTICATION -> SUCCESS");
		System.out.println("JWT received successfully from KNG MEDAS.");
		System.out.println("Active MEDAS Server = " + activeBaseUrl);

		System.out.println("=======================================================");
		System.out.println("KNG MEDAS JWT AUTHENTICATION TEST -> END");
		System.out.println("=======================================================");
	}

	/*
	 * Test-only Spring configuration.
	 */
	@Configuration
	@EnableConfigurationProperties(KngMedasApiProperties.class)
	static class TestConfig {
		@Bean
		RestTemplateBuilder restTemplateBuilder() {
			return new RestTemplateBuilder();
		}
	}
}