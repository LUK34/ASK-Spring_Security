package kw.kng;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import kw.kng.security.medasApiSecurity.client.KngMedasApiClient;
import kw.kng.security.medasApiSecurity.client.KngMedasAuthClient;
import kw.kng.security.medasApiSecurity.config.KngMedasApiConfig;
import kw.kng.security.medasApiSecurity.config.KngMedasApiProperties;
import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolver;
import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolverImpl;
import kw.kng.security.medasApiSecurity.token.KngMedasTokenService;
import kw.kng.security.medasApiSecurity.token.KngMedasTokenServiceImpl;

@Disabled("Manual KNG MEDAS integration test - do not run during normal WAR build")
@SpringBootTest(classes = { KngMedasApiConfig.class, KngMedasEndpointResolverImpl.class, KngMedasAuthClient.class,
		KngMedasApiClient.class, KngMedasTokenServiceImpl.class, KngMedasRuntimeFailoverTest.TestConfig.class })
@ActiveProfiles("prod")
public class KngMedasRuntimeFailoverTest {

	// ############################################################################################################
	// TEST CONFIGURATION
	// ############################################################################################################

	private static final String EXPECTED_INITIAL_SERVER = "http://10.201.49.120:8080/kng_medas";

	private static final String EXPECTED_FAILOVER_SERVER = "http://10.201.53.180:8080/kng_medas";

	/*
	 * Safe GET endpoint.
	 *
	 * This request does not insert/update/delete MEDAS data.
	 */
	private static final String TEST_GET_ENDPOINT = "/actuator/health";

	/*
	 * Time given to manually stop Tomcat on the PRIME server.
	 */
	private static final int FAILOVER_COUNTDOWN_SECONDS = 80;

	// ############################################################################################################
	// SPRING COMPONENTS
	// ############################################################################################################

	@Autowired
	private KngMedasTokenService tokenService;

	@Autowired
	private KngMedasEndpointResolver endpointResolver;

	@Autowired
	private KngMedasApiClient medasApiClient;

	@Autowired
	private KngMedasApiProperties properties;

	// ############################################################################################################
	// RUNTIME FAILOVER TEST
	// ############################################################################################################

	@Test
	public void testRuntimeFailover() throws Exception {

		System.out.println("=======================================================");

		System.out.println("KNG MEDAS RUNTIME FAILOVER TEST -> START");

		System.out.println("=======================================================");

		// --------------------------------------------------------------------------------------------------------
		// STEP 1
		// VERIFY CONFIGURATION
		// --------------------------------------------------------------------------------------------------------

		assertFalse(properties.getCandidateBaseUrls().isEmpty(),
				"At least one KNG MEDAS REST API server must be configured.");

		System.out.println();

		System.out.println("STEP 1 - Configured MEDAS Servers");

		for (String baseUrl : properties.getCandidateBaseUrls()) {
			System.out.println("Candidate = " + baseUrl);
		}

		// --------------------------------------------------------------------------------------------------------
		// STEP 2
		// AUTHENTICATE WHILE PRIME SERVER IS RUNNING
		// --------------------------------------------------------------------------------------------------------

		System.out.println();

		System.out.println("STEP 2 - Authenticating against KNG MEDAS...");

		String token = tokenService.getValidToken();

		assertNotNull(token, "JWT token should not be null.");

		assertFalse(token.trim().isEmpty(), "JWT token should not be empty.");

		String initialServer = endpointResolver.getActiveBaseUrl();

		assertNotNull(initialServer, "Initial active MEDAS server should not be null.");

		System.out.println();

		System.out.println("Initial Active Server =");

		System.out.println(initialServer);

		// --------------------------------------------------------------------------------------------------------
		// STEP 3
		// VERIFY PRIME WAS SELECTED
		// --------------------------------------------------------------------------------------------------------

		assertEquals(EXPECTED_INITIAL_SERVER, initialServer, "PRIME MEDAS server should initially be selected.");

		System.out.println();

		System.out.println("PRIME SERVER SUCCESSFULLY SELECTED.");

		System.out.println();

		System.out.println("=======================================================");

		System.out.println("NOW STOP TOMCAT ON SERVER 10.201.49.120");

		System.out.println();

		System.out.println("DO NOT STOP THIS JUNIT TEST.");

		System.out.println();

		System.out.println("You have " + FAILOVER_COUNTDOWN_SECONDS + " seconds.");

		System.out.println("=======================================================");

		System.out.println();

		// --------------------------------------------------------------------------------------------------------
		// STEP 4
		// COUNTDOWN WHILE USER STOPS TOMCAT ON PRIME
		// --------------------------------------------------------------------------------------------------------

		runFailoverCountdown();

		// --------------------------------------------------------------------------------------------------------
		// STEP 5
		// CALL SAFE GET
		// --------------------------------------------------------------------------------------------------------

		System.out.println();

		System.out.println("=======================================================");

		System.out.println("COUNTDOWN COMPLETE.");

		System.out.println("Testing runtime failover now...");

		System.out.println("=======================================================");

		System.out.println();

		System.out.println("STEP 5 - Calling MEDAS GET endpoint...");

		System.out.println("Endpoint = " + TEST_GET_ENDPOINT);

		Object response = medasApiClient.get(TEST_GET_ENDPOINT, Object.class);

		assertNotNull(response, "MEDAS GET response should not be null.");

		// --------------------------------------------------------------------------------------------------------
		// STEP 6
		// CHECK WHICH SERVER IS NOW ACTIVE
		// --------------------------------------------------------------------------------------------------------

		String finalServer = endpointResolver.getActiveBaseUrl();

		assertNotNull(finalServer, "Final active MEDAS server should not be null.");

		System.out.println();

		System.out.println("Final Active Server =");

		System.out.println(finalServer);

		// --------------------------------------------------------------------------------------------------------
		// STEP 7
		// VERIFY FAILOVER SERVER
		// --------------------------------------------------------------------------------------------------------

		assertEquals(EXPECTED_FAILOVER_SERVER, finalServer,
				"MEDAS runtime failover should switch from PRIME " + "server to the first available fallback server.");

		// --------------------------------------------------------------------------------------------------------
		// SUCCESS
		// --------------------------------------------------------------------------------------------------------

		System.out.println();

		System.out.println("=======================================================");

		System.out.println("KNG MEDAS RUNTIME FAILOVER -> SUCCESS");

		System.out.println("=======================================================");

		System.out.println();

		System.out.println("Initial Server : " + initialServer);

		System.out.println("Failover Server: " + finalServer);

		System.out.println();

		System.out.println("GET request completed successfully after failover.");

		System.out.println();

		System.out.println("=======================================================");

		System.out.println("KNG MEDAS RUNTIME FAILOVER TEST -> END");

		System.out.println("=======================================================");
	}

	// ############################################################################################################
	// COUNTDOWN UTILITY
	// ############################################################################################################

	/**
	 * Gives the tester enough time to manually stop Tomcat on the currently active
	 * PRIME MEDAS server.
	 *
	 * Countdown is displayed in the STS console.
	 */
	private void runFailoverCountdown() throws InterruptedException {

		for (int seconds = FAILOVER_COUNTDOWN_SECONDS; seconds >= 1; seconds--) {

			System.out.println(
					"Runtime failover test starts in " + seconds + " second" + (seconds == 1 ? "" : "s") + "...");

			Thread.sleep(1000);
		}
	}

	// ############################################################################################################
	// TEST-ONLY SPRING CONFIGURATION
	// ############################################################################################################

	@Configuration
	@EnableConfigurationProperties(KngMedasApiProperties.class)
	static class TestConfig {

		@Bean
		RestTemplateBuilder restTemplateBuilder() {
			return new RestTemplateBuilder();
		}
	}
}