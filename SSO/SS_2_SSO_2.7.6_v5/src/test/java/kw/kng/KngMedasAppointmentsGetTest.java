package kw.kng;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

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


/**
 * Manual integration test for retrieving appointment data
 * from the secured KNG MEDAS REST API.
 *
 * Expected architecture:
 *
 * PRIMARY:
 * PATMRD
 *   -> API Gateway
 *   -> Eureka service discovery
 *   -> KNG MEDAS REST
 *
 * SECONDARY:
 * PATMRD
 *   -> Direct MEDAS PRIME / fallback servers
 *
 * IMPORTANT:
 *
 * KngMedasEndpointResolver tracks only the secondary
 * direct MEDAS route.
 *
 * Therefore:
 *
 * endpointResolver.getActiveBaseUrl() == null
 *
 * is completely valid when the request is successfully
 * handled through the API Gateway.
 */
@Disabled("Manual KNG MEDAS integration test - do not run during normal WAR build")
@SpringBootTest(classes = {
        KngMedasApiConfig.class,
        KngMedasEndpointResolverImpl.class,
        KngMedasAuthClient.class,
        KngMedasApiClient.class,
        KngMedasTokenServiceImpl.class,
        KngMedasAppointmentsGetTest.TestConfig.class
})
@ActiveProfiles("prod")
public class KngMedasAppointmentsGetTest
{

    // ############################################################################################################
    // TEST ENDPOINT
    // ############################################################################################################

    private static final String TEST_GET_ENDPOINT =
            "/appointments_rest/all?page=0";


    // ############################################################################################################
    // SPRING COMPONENTS
    // ############################################################################################################

    @Autowired
    private KngMedasApiClient medasApiClient;

    @Autowired
    private KngMedasTokenService tokenService;

    @Autowired
    private KngMedasEndpointResolver endpointResolver;

    @Autowired
    private KngMedasApiProperties properties;


    // ############################################################################################################
    // TEST
    // ############################################################################################################

    @Test
    public void testGetAppointmentsFromKngMedas()
    {

        System.out.println(
                "=======================================================");

        System.out.println(
                "KNG MEDAS APPOINTMENTS GET TEST -> START");

        System.out.println(
                "=======================================================");


        // --------------------------------------------------------------------------------------------------------
        // STEP 1
        // VERIFY MEDAS CONFIGURATION
        // --------------------------------------------------------------------------------------------------------

        assertFalse(
                properties.getCandidateBaseUrls().isEmpty(),
                "At least one direct KNG MEDAS REST API server must be configured.");

        System.out.println();
        System.out.println(
                "STEP 1 - KNG MEDAS CONFIGURATION");


        // --------------------------------------------------------------------------------------------------------
        // DISPLAY GATEWAY CONFIGURATION
        // --------------------------------------------------------------------------------------------------------

        String gatewayBaseUrl =
                properties.getGatewayBaseUrl();


        if (gatewayBaseUrl != null)
        {
            System.out.println(
                    "Gateway enabled/configured = true");

            System.out.println(
                    "Gateway Base URL = "
                            + gatewayBaseUrl);
        }
        else
        {
            System.out.println(
                    "Gateway enabled/configured = false");
        }


        // --------------------------------------------------------------------------------------------------------
        // DISPLAY DIRECT FALLBACK SERVERS
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "Configured Direct MEDAS Servers:");

        for (String baseUrl :
                properties.getCandidateBaseUrls())
        {
            System.out.println(
                    "Candidate = "
                            + baseUrl);
        }


        // --------------------------------------------------------------------------------------------------------
        // STEP 2
        // OBTAIN JWT
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 2 - Authenticating against KNG MEDAS...");


        String token =
                tokenService.getValidToken();


        assertNotNull(
                token,
                "JWT token should not be null.");

        assertFalse(
                token.trim().isEmpty(),
                "JWT token should not be empty.");


        /*
         * SECURITY:
         *
         * Never print:
         *
         * - JWT token
         * - password
         * - Authorization header
         */
        System.out.println();
        System.out.println(
                "Authentication successful.");

        System.out.println(
                "JWT received successfully.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 3
        // DISPLAY AUTHENTICATION ROUTE
        // --------------------------------------------------------------------------------------------------------

        String activeDirectServer =
                endpointResolver.getActiveBaseUrl();


        System.out.println();
        System.out.println(
                "STEP 3 - Authentication Route");


        if (activeDirectServer == null)
        {
            /*
             * Normal result when authentication succeeds
             * through the API Gateway.
             *
             * The endpoint resolver stores only direct
             * MEDAS server state.
             */
            System.out.println(
                    "Authentication Route = API GATEWAY");

            System.out.println(
                    "Gateway Base URL = "
                            + gatewayBaseUrl);

            System.out.println(
                    "Active Direct MEDAS Server = NONE");
        }
        else
        {
            /*
             * A non-null direct server means authentication
             * used the secondary direct MEDAS route.
             */
            System.out.println(
                    "Authentication Route = DIRECT MEDAS");

            System.out.println(
                    "Active Direct MEDAS Server = "
                            + activeDirectServer);
        }


        // --------------------------------------------------------------------------------------------------------
        // STEP 4
        // CALL APPOINTMENTS REST API
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 4 - Calling Appointments REST API...");

        System.out.println(
                "Endpoint = "
                        + TEST_GET_ENDPOINT);


        @SuppressWarnings("rawtypes")
        Map response =
                medasApiClient.get(
                        TEST_GET_ENDPOINT,
                        Map.class);


        // --------------------------------------------------------------------------------------------------------
        // STEP 5
        // VERIFY RESPONSE
        // --------------------------------------------------------------------------------------------------------

        assertNotNull(
                response,
                "Appointments REST API response should not be null.");


        System.out.println();
        System.out.println(
                "Appointments REST API response received successfully.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 6
        // DISPLAY SPRING PAGE INFORMATION
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 6 - Appointment Page Information");


        System.out.println(
                "Total Elements = "
                        + response.get("totalElements"));

        System.out.println(
                "Total Pages = "
                        + response.get("totalPages"));

        System.out.println(
                "Current Page = "
                        + response.get("number"));

        System.out.println(
                "Page Size = "
                        + response.get("size"));

        System.out.println(
                "Number Of Elements = "
                        + response.get("numberOfElements"));

        System.out.println(
                "First Page = "
                        + response.get("first"));

        System.out.println(
                "Last Page = "
                        + response.get("last"));


        // --------------------------------------------------------------------------------------------------------
        // STEP 7
        // VERIFY CONTENT FIELD
        // --------------------------------------------------------------------------------------------------------

        Object content =
                response.get("content");


        assertNotNull(
                content,
                "Appointments page content should not be null.");


        System.out.println();
        System.out.println(
                "Appointment content received.");

        System.out.println(
                "Content = "
                        + content);


        // --------------------------------------------------------------------------------------------------------
        // STEP 8
        // IDENTIFY FINAL REQUEST ROUTE
        // --------------------------------------------------------------------------------------------------------

        String finalActiveDirectServer =
                endpointResolver.getActiveBaseUrl();


        System.out.println();
        System.out.println(
                "STEP 8 - Request Route Result");


        if (finalActiveDirectServer == null)
        {
            /*
             * No direct server became active.
             *
             * Therefore the request remained on the
             * Gateway-primary route.
             */
            System.out.println(
                    "Request Route = API GATEWAY");

            System.out.println(
                    "Gateway Base URL = "
                            + gatewayBaseUrl);

            System.out.println(
                    "Active Direct MEDAS Server = NONE");
        }
        else
        {
            /*
             * A direct server became active.
             *
             * This can happen when:
             *
             * - Gateway is disabled
             * - Gateway is unreachable
             * - Gateway returns 502 / 503 / 504
             *
             * GET is safe to replay through the
             * direct failover chain.
             */
            System.out.println(
                    "Request Route = DIRECT MEDAS");

            System.out.println(
                    "Active Direct MEDAS Server = "
                            + finalActiveDirectServer);
        }


        // --------------------------------------------------------------------------------------------------------
        // SUCCESS
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "KNG MEDAS APPOINTMENTS GET -> SUCCESS");

        System.out.println(
                "=======================================================");


        System.out.println();

        System.out.println(
                "Endpoint Used = "
                        + TEST_GET_ENDPOINT);


        System.out.println();

        System.out.println(
                "PATMRD successfully retrieved appointment data "
                        + "from the secured KNG MEDAS REST API.");


        System.out.println();

        System.out.println(
                "=======================================================");

        System.out.println(
                "KNG MEDAS APPOINTMENTS GET TEST -> END");

        System.out.println(
                "=======================================================");
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