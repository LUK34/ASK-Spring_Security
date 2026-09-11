package kw.kng;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import kw.kng.security.medasApiSecurity.token.KngMedasTokenServiceImpl;


/**
 * Manual integration test for runtime failover from:
 *
 * PRIMARY:
 * PATMRD -> API Gateway -> KNG MEDAS REST
 *
 * to:
 *
 * SECONDARY:
 * PATMRD -> Direct MEDAS server
 *
 *
 * TEST PROCEDURE:
 *
 * 1. Start Eureka.
 * 2. Start KNG MEDAS REST.
 * 3. Start API Gateway.
 * 4. Run this test.
 * 5. The first GET must succeed through Gateway.
 * 6. During the countdown, STOP ONLY THE API GATEWAY.
 * 7. Do NOT stop MEDAS REST.
 * 8. The second GET should automatically fall back
 *    to a direct MEDAS server.
 *
 *
 * IMPORTANT:
 *
 * This test uses GET because GET can safely be replayed
 * after an availability failure.
 *
 * Do NOT use POST / PUT / DELETE for this runtime
 * failover test.
 */
@Disabled("Manual KNG MEDAS integration test - do not run during normal WAR build")
@SpringBootTest(classes = {
        KngMedasApiConfig.class,
        KngMedasEndpointResolverImpl.class,
        KngMedasAuthClient.class,
        KngMedasApiClient.class,
        KngMedasTokenServiceImpl.class,
        KngMedasRuntimeFailoverTest.TestConfig.class
})
@ActiveProfiles("prod")
public class KngMedasRuntimeFailoverTest
{

    // ############################################################################################################
    // TEST CONFIGURATION
    // ############################################################################################################

    /*
     * Safe GET endpoint.
     *
     * This request does not insert/update/delete MEDAS data.
     */
    private static final String TEST_GET_ENDPOINT =
            "/actuator/health";


    /*
     * Time given to manually stop the API Gateway.
     */
    private static final int FAILOVER_COUNTDOWN_SECONDS =
            80;


    // ############################################################################################################
    // SPRING COMPONENTS
    // ############################################################################################################

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
    public void testGatewayToDirectRuntimeFailover()
            throws Exception
    {

        System.out.println(
                "=======================================================");

        System.out.println(
                "KNG MEDAS GATEWAY -> DIRECT RUNTIME FAILOVER TEST");

        System.out.println(
                "=======================================================");


        // --------------------------------------------------------------------------------------------------------
        // STEP 1
        // VERIFY CONFIGURATION
        // --------------------------------------------------------------------------------------------------------

        String gatewayBaseUrl =
                properties.getGatewayBaseUrl();


        assertNotNull(
                gatewayBaseUrl,
                "API Gateway must be enabled/configured for this test.");


        assertFalse(
                properties.getCandidateBaseUrls().isEmpty(),
                "At least one direct KNG MEDAS REST API server must be configured.");


        System.out.println();
        System.out.println(
                "STEP 1 - Runtime Failover Configuration");


        System.out.println(
                "Gateway Base URL = "
                        + gatewayBaseUrl);


        System.out.println();
        System.out.println(
                "Direct MEDAS fallback servers:");


        for (String baseUrl :
                properties.getCandidateBaseUrls())
        {
            System.out.println(
                    "Candidate = "
                            + baseUrl);
        }


        // --------------------------------------------------------------------------------------------------------
        // STEP 2
        // ENSURE DIRECT ROUTE HAS NOT ALREADY BEEN SELECTED
        // --------------------------------------------------------------------------------------------------------

        endpointResolver.invalidate();


        assertNull(
                endpointResolver.getActiveBaseUrl(),
                "No direct MEDAS server should be active before the Gateway test begins.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 3
        // FIRST GET - GATEWAY MUST BE AVAILABLE
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 3 - Performing initial GET through API Gateway...");


        System.out.println(
                "Endpoint = "
                        + TEST_GET_ENDPOINT);


        Object initialResponse =
                medasApiClient.get(
                        TEST_GET_ENDPOINT,
                        Object.class);


        assertNotNull(
                initialResponse,
                "Initial Gateway GET response should not be null.");


        /*
         * If Gateway handled the GET successfully, the direct
         * endpoint resolver should still have no active server.
         */
        String directServerAfterInitialGet =
                endpointResolver.getActiveBaseUrl();


        assertNull(
                directServerAfterInitialGet,
                "A direct MEDAS server became active during the initial GET. "
                        + "The initial request may not have used the API Gateway.");


        System.out.println();
        System.out.println(
                "Initial GET completed successfully.");

        System.out.println(
                "Initial Request Route = API GATEWAY");

        System.out.println(
                "Gateway Base URL = "
                        + gatewayBaseUrl);

        System.out.println(
                "Active Direct MEDAS Server = NONE");


        // --------------------------------------------------------------------------------------------------------
        // STEP 4
        // MANUAL GATEWAY SHUTDOWN
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "NOW STOP THE KNG MSD API GATEWAY.");

        System.out.println();

        System.out.println(
                "For your current local DEV environment:");

        System.out.println(
                "Stop the application running on port 8888.");

        System.out.println();

        System.out.println(
                "DO NOT STOP EUREKA.");

        System.out.println(
                "DO NOT STOP KNG MEDAS REST.");

        System.out.println();

        System.out.println(
                "You have "
                        + FAILOVER_COUNTDOWN_SECONDS
                        + " seconds.");

        System.out.println(
                "=======================================================");


        // --------------------------------------------------------------------------------------------------------
        // STEP 5
        // COUNTDOWN
        // --------------------------------------------------------------------------------------------------------

        runFailoverCountdown();


        // --------------------------------------------------------------------------------------------------------
        // STEP 6
        // SECOND GET - GATEWAY SHOULD FAIL AND DIRECT ROUTE SHOULD TAKE OVER
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "COUNTDOWN COMPLETE.");

        System.out.println(
                "Testing Gateway -> Direct MEDAS failover now...");

        System.out.println(
                "=======================================================");


        Object failoverResponse =
                medasApiClient.get(
                        TEST_GET_ENDPOINT,
                        Object.class);


        assertNotNull(
                failoverResponse,
                "MEDAS GET response after Gateway failure should not be null.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 7
        // VERIFY DIRECT SERVER WAS SELECTED
        // --------------------------------------------------------------------------------------------------------

        String activeDirectServer =
                endpointResolver.getActiveBaseUrl();


        assertNotNull(
                activeDirectServer,
                "A direct MEDAS server should have been selected "
                        + "after the API Gateway became unavailable.");

        assertFalse(
                activeDirectServer.trim().isEmpty(),
                "The selected direct MEDAS server should not be empty.");


        System.out.println();
        System.out.println(
                "Gateway failure detected successfully.");

        System.out.println(
                "Failover Request Route = DIRECT MEDAS");

        System.out.println(
                "Active Direct MEDAS Server = "
                        + activeDirectServer);


        // --------------------------------------------------------------------------------------------------------
        // SUCCESS
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "KNG MEDAS GATEWAY -> DIRECT FAILOVER SUCCESS");

        System.out.println(
                "=======================================================");


        System.out.println();

        System.out.println(
                "Initial Route  : API GATEWAY");

        System.out.println(
                "Gateway       : "
                        + gatewayBaseUrl);

        System.out.println(
                "Failover Route : DIRECT MEDAS");

        System.out.println(
                "Direct Server  : "
                        + activeDirectServer);

        System.out.println();

        System.out.println(
                "GET request completed successfully after "
                        + "API Gateway failure.");

        System.out.println();

        System.out.println(
                "=======================================================");

        System.out.println(
                "KNG MEDAS RUNTIME FAILOVER TEST -> END");

        System.out.println(
                "=======================================================");
    }


    // ############################################################################################################
    // COUNTDOWN UTILITY
    // ############################################################################################################

    /**
     * Gives the tester enough time to manually stop
     * the API Gateway.
     */
    private void runFailoverCountdown()
            throws InterruptedException
    {

        for (int seconds =
                FAILOVER_COUNTDOWN_SECONDS;
                seconds >= 1;
                seconds--)
        {

            System.out.println(
                    "Runtime failover test starts in "
                            + seconds
                            + " second"
                            + (seconds == 1 ? "" : "s")
                            + "...");

            Thread.sleep(1000);
        }
    }


    // ############################################################################################################
    // TEST-ONLY SPRING CONFIGURATION
    // ############################################################################################################

    @Configuration
    @EnableConfigurationProperties(
            KngMedasApiProperties.class)
    static class TestConfig
    {

        @Bean
        RestTemplateBuilder restTemplateBuilder()
        {
            return new RestTemplateBuilder();
        }
    }
}