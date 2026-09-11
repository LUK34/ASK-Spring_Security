package kw.kng;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import kw.kng.security.medasApiSecurity.client.KngMedasApiClient;
import kw.kng.security.medasApiSecurity.client.KngMedasAuthClient;
import kw.kng.security.medasApiSecurity.config.KngMedasApiConfig;
import kw.kng.security.medasApiSecurity.config.KngMedasApiProperties;
import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolver;
import kw.kng.security.medasApiSecurity.endpoint.KngMedasEndpointResolverImpl;
import kw.kng.security.medasApiSecurity.token.KngMedasTokenService;
import kw.kng.security.medasApiSecurity.token.KngMedasTokenServiceImpl;


/**
 * Manual controlled CRUD integration test for REASON_SETUP.
 *
 * Architecture:
 *
 * PRIMARY:
 * PATMRD
 *   -> API Gateway
 *   -> KNG MEDAS REST
 *
 * SECONDARY:
 * PATMRD
 *   -> Direct MEDAS PRIME / fallback servers
 *
 *
 * IMPORTANT WRITE SAFETY:
 *
 * POST / PUT / DELETE operations are NOT intended to be
 * automatically replayed after an uncertain Gateway/network
 * failure.
 *
 * Therefore:
 *
 * - Keep API Gateway running during this test.
 * - Keep KNG MEDAS REST running during this test.
 * - Do NOT intentionally stop Gateway during POST / PUT / DELETE.
 *
 *
 * CRUD lifecycle:
 *
 * 1. CREATE test row
 * 2. GET and verify created row
 * 3. UPDATE same row
 * 4. GET and verify updated row
 * 5. DELETE same row
 * 6. GET and verify HTTP 404
 *
 *
 * Only the record created by this test is modified/deleted.
 */
@Disabled("Manual KNG MEDAS integration test - do not run during normal WAR build")
@SpringBootTest(classes = {
        KngMedasApiConfig.class,
        KngMedasEndpointResolverImpl.class,
        KngMedasAuthClient.class,
        KngMedasApiClient.class,
        KngMedasTokenServiceImpl.class,
        KngMedasReasonSetupCrudTest.TestConfig.class
})
@ActiveProfiles("prod")
@TestMethodOrder(OrderAnnotation.class)
public class KngMedasReasonSetupCrudTest
{

    // ############################################################################################################
    // REST ENDPOINTS
    // ############################################################################################################

    private static final String CREATE_ENDPOINT =
            "/reasonsetup_rest/create";

    private static final String UPDATE_ENDPOINT =
            "/reasonsetup_rest/update/";

    private static final String DELETE_ENDPOINT =
            "/reasonsetup_rest/delete/";

    private static final String DETAILS_ENDPOINT =
            "/reasonsetup_rest/details/";


    // ############################################################################################################
    // TEST DATA
    // ############################################################################################################

    /*
     * The reasonCode returned from the POST operation
     * is stored here.
     *
     * UPDATE and DELETE operate ONLY on the newly-created
     * test row.
     */
    private static Long createdReasonCode;


    /*
     * Unique test value so the row is easy to identify
     * in Oracle if manual inspection is required.
     */
    private static final String CREATE_REASON_NAME =
            "PATMRD REST API TEST - CREATE - "
                    + System.currentTimeMillis();


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
    // TEST 1
    // POST - CREATE
    // ############################################################################################################

    @Test
    @Order(1)
    public void test01_CreateReasonSetup()
    {

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "REASON_SETUP POST TEST -> START");

        System.out.println(
                "=======================================================");


        // --------------------------------------------------------------------------------------------------------
        // STEP 1
        // VERIFY CONFIGURATION
        // --------------------------------------------------------------------------------------------------------

        assertFalse(
                properties.getCandidateBaseUrls().isEmpty(),
                "At least one direct KNG MEDAS REST API server must be configured.");


        String gatewayBaseUrl =
                properties.getGatewayBaseUrl();


        System.out.println();
        System.out.println(
                "STEP 1 - KNG MEDAS CONFIGURATION");


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
        // AUTHENTICATE
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
             * Normal when authentication succeeds
             * through the API Gateway.
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
             * A non-null direct server indicates that
             * authentication used the secondary direct route.
             */
            System.out.println(
                    "Authentication Route = DIRECT MEDAS");

            System.out.println(
                    "Active Direct MEDAS Server = "
                            + activeDirectServer);
        }


        // --------------------------------------------------------------------------------------------------------
        // STEP 4
        // BUILD CREATE REQUEST
        // --------------------------------------------------------------------------------------------------------

        Map<String, Object> request =
                new HashMap<String, Object>();

        request.put(
                "reasonName",
                CREATE_REASON_NAME);


        System.out.println();
        System.out.println(
                "STEP 4 - Creating REASON_SETUP record...");

        System.out.println(
                "Reason Name = "
                        + CREATE_REASON_NAME);


        // --------------------------------------------------------------------------------------------------------
        // STEP 5
        // POST
        // --------------------------------------------------------------------------------------------------------

        @SuppressWarnings("rawtypes")
        Map response =
                medasApiClient.post(
                        CREATE_ENDPOINT,
                        request,
                        Map.class);


        assertNotNull(
                response,
                "Create response should not be null.");

        assertNotNull(
                response.get("reasonCode"),
                "Created reasonCode should not be null.");

        assertNotNull(
                response.get("reasonName"),
                "Created reasonName should not be null.");


        Number generatedId =
                (Number) response.get("reasonCode");


        createdReasonCode =
                generatedId.longValue();


        assertTrue(
                createdReasonCode > 0,
                "Generated reasonCode should be greater than zero.");

        assertEquals(
                CREATE_REASON_NAME,
                response.get("reasonName"),
                "Created reasonName should match the request.");


        System.out.println();
        System.out.println(
                "REASON_SETUP INSERT SUCCESSFUL.");

        System.out.println(
                "Generated Reason Code = "
                        + createdReasonCode);

        System.out.println(
                "Reason Name = "
                        + response.get("reasonName"));


        // --------------------------------------------------------------------------------------------------------
        // STEP 6
        // VERIFY CREATED ROW USING GET
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 6 - Verifying inserted row using GET...");


        @SuppressWarnings("rawtypes")
        Map verifyResponse =
                medasApiClient.get(
                        DETAILS_ENDPOINT + createdReasonCode,
                        Map.class);


        assertNotNull(
                verifyResponse,
                "Inserted ReasonSetup record should be retrievable.");

        assertNotNull(
                verifyResponse.get("reasonCode"),
                "Retrieved reasonCode should not be null.");

        assertEquals(
                createdReasonCode.longValue(),
                ((Number) verifyResponse.get("reasonCode")).longValue(),
                "Retrieved reasonCode should match.");

        assertEquals(
                CREATE_REASON_NAME,
                verifyResponse.get("reasonName"),
                "Retrieved reasonName should match inserted value.");


        System.out.println(
                "Inserted row verified successfully through GET.");


        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "REASON_SETUP POST TEST -> SUCCESS");

        System.out.println(
                "=======================================================");
    }


    // ############################################################################################################
    // TEST 2
    // PUT - UPDATE
    // ############################################################################################################

    @Test
    @Order(2)
    public void test02_UpdateReasonSetup()
    {

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "REASON_SETUP PUT TEST -> START");

        System.out.println(
                "=======================================================");


        // --------------------------------------------------------------------------------------------------------
        // STEP 1
        // VERIFY POST TEST CREATED A ROW
        // --------------------------------------------------------------------------------------------------------

        assertNotNull(
                createdReasonCode,
                "POST test must create a ReasonSetup row before UPDATE.");


        String updatedReasonName =
                "PATMRD REST API TEST - UPDATED - "
                        + createdReasonCode;


        // --------------------------------------------------------------------------------------------------------
        // STEP 2
        // BUILD UPDATE REQUEST
        // --------------------------------------------------------------------------------------------------------

        Map<String, Object> request =
                new HashMap<String, Object>();

        request.put(
                "reasonName",
                updatedReasonName);


        System.out.println();
        System.out.println(
                "Updating Reason Code = "
                        + createdReasonCode);

        System.out.println(
                "New Reason Name = "
                        + updatedReasonName);


        // --------------------------------------------------------------------------------------------------------
        // STEP 3
        // PUT
        // --------------------------------------------------------------------------------------------------------

        @SuppressWarnings("rawtypes")
        Map response =
                medasApiClient.put(
                        UPDATE_ENDPOINT + createdReasonCode,
                        request,
                        Map.class);


        assertNotNull(
                response,
                "Update response should not be null.");

        assertNotNull(
                response.get("reasonCode"),
                "Updated reasonCode should not be null.");

        assertEquals(
                createdReasonCode.longValue(),
                ((Number) response.get("reasonCode")).longValue(),
                "Updated reasonCode should remain unchanged.");

        assertEquals(
                updatedReasonName,
                response.get("reasonName"),
                "Updated reasonName should match the request.");


        System.out.println();
        System.out.println(
                "REASON_SETUP UPDATE SUCCESSFUL.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 4
        // VERIFY UPDATE USING GET
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "Verifying updated row using GET...");


        @SuppressWarnings("rawtypes")
        Map verifyResponse =
                medasApiClient.get(
                        DETAILS_ENDPOINT + createdReasonCode,
                        Map.class);


        assertNotNull(
                verifyResponse,
                "Updated ReasonSetup record should be retrievable.");

        assertNotNull(
                verifyResponse.get("reasonCode"),
                "Retrieved reasonCode should not be null.");

        assertEquals(
                createdReasonCode.longValue(),
                ((Number) verifyResponse.get("reasonCode")).longValue(),
                "Retrieved reasonCode should remain unchanged.");

        assertEquals(
                updatedReasonName,
                verifyResponse.get("reasonName"),
                "Database should contain the updated reasonName.");


        System.out.println(
                "Updated row verified successfully through GET.");


        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "REASON_SETUP PUT TEST -> SUCCESS");

        System.out.println(
                "=======================================================");
    }


    // ############################################################################################################
    // TEST 3
    // DELETE
    // ############################################################################################################

    @Test
    @Order(3)
    public void test03_DeleteReasonSetup()
    {

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "REASON_SETUP DELETE TEST -> START");

        System.out.println(
                "=======================================================");


        // --------------------------------------------------------------------------------------------------------
        // STEP 1
        // VERIFY TEST ROW EXISTS
        // --------------------------------------------------------------------------------------------------------

        assertNotNull(
                createdReasonCode,
                "POST test must create a ReasonSetup row before DELETE.");


        System.out.println();
        System.out.println(
                "Deleting Reason Code = "
                        + createdReasonCode);


        // --------------------------------------------------------------------------------------------------------
        // STEP 2
        // VERIFY RECORD EXISTS BEFORE DELETE
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "Verifying record exists before DELETE...");


        @SuppressWarnings("rawtypes")
        Map beforeDeleteResponse =
                medasApiClient.get(
                        DETAILS_ENDPOINT + createdReasonCode,
                        Map.class);


        assertNotNull(
                beforeDeleteResponse,
                "ReasonSetup record should exist before DELETE.");

        assertNotNull(
                beforeDeleteResponse.get("reasonCode"),
                "Reason code should not be null before DELETE.");

        assertEquals(
                createdReasonCode.longValue(),
                ((Number) beforeDeleteResponse.get("reasonCode")).longValue(),
                "Reason code before DELETE should match the created record.");


        System.out.println(
                "Record confirmed before DELETE.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 3
        // DELETE
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "Sending DELETE request...");


        medasApiClient.delete(
                DELETE_ENDPOINT + createdReasonCode);


        System.out.println();
        System.out.println(
                "REASON_SETUP DELETE REQUEST SUCCESSFUL.");

        System.out.println(
                "Deleted Reason Code = "
                        + createdReasonCode);


        // --------------------------------------------------------------------------------------------------------
        // STEP 4
        // VERIFY RECORD NO LONGER EXISTS
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "Verifying deleted row using GET...");


        boolean recordNotFound =
                false;


        try
        {

            medasApiClient.get(
                    DETAILS_ENDPOINT + createdReasonCode,
                    Map.class);

        }
        catch (HttpClientErrorException.NotFound ex)
        {

            recordNotFound =
                    true;


            System.out.println(
                    "HTTP 404 received as expected.");

            System.out.println(
                    "Deleted Reason Code "
                            + createdReasonCode
                            + " is no longer available.");
        }


        // --------------------------------------------------------------------------------------------------------
        // STEP 5
        // ASSERT DELETE WAS ACTUALLY COMPLETED
        // --------------------------------------------------------------------------------------------------------

        assertTrue(
                recordNotFound,
                "Expected HTTP 404 after DELETE, but the "
                        + "ReasonSetup record is still retrievable.");


        System.out.println();
        System.out.println(
                "DELETE verification successful.");

        System.out.println(
                "The ReasonSetup record no longer exists.");


        // --------------------------------------------------------------------------------------------------------
        // SUCCESS
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "REASON_SETUP DELETE TEST -> SUCCESS");

        System.out.println(
                "=======================================================");


        System.out.println();

        System.out.println(
                "Controlled CRUD lifecycle completed.");

        System.out.println(
                "Test ReasonSetup row was:");

        System.out.println(
                "1. Created successfully.");

        System.out.println(
                "2. Verified after CREATE.");

        System.out.println(
                "3. Updated successfully.");

        System.out.println(
                "4. Verified after UPDATE.");

        System.out.println(
                "5. Deleted successfully.");

        System.out.println(
                "6. Verified as NOT FOUND after DELETE.");


        System.out.println();

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