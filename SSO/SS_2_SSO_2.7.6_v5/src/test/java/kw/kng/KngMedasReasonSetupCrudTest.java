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
     * The reasonCode returned from the POST operation is stored here.
     *
     * UPDATE and DELETE will operate ONLY on this newly-created test row.
     */
    private static Long createdReasonCode;

    /*
     * Unique value so that the test record is easy to identify in Oracle.
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
    // POST - INSERT
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
        // VERIFY SERVER CONFIGURATION
        // --------------------------------------------------------------------------------------------------------

        assertFalse(
                properties.getCandidateBaseUrls().isEmpty(),
                "At least one KNG MEDAS server must be configured.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 2
        // AUTHENTICATE
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 1 - Authenticating against KNG MEDAS...");


        String token =
                tokenService.getValidToken();


        assertNotNull(
                token,
                "JWT token should not be null.");

        assertFalse(
                token.trim().isEmpty(),
                "JWT token should not be empty.");


        System.out.println(
                "Authentication successful.");

        System.out.println(
                "Active MEDAS Server = "
                        + endpointResolver.getActiveBaseUrl());


        // --------------------------------------------------------------------------------------------------------
        // STEP 3
        // BUILD CREATE REQUEST
        // --------------------------------------------------------------------------------------------------------

        Map<String, Object> request =
                new HashMap<String, Object>();

        request.put(
                "reasonName",
                CREATE_REASON_NAME);


        System.out.println();
        System.out.println(
                "STEP 2 - Creating REASON_SETUP record...");

        System.out.println(
                "Reason Name = "
                        + CREATE_REASON_NAME);


        // --------------------------------------------------------------------------------------------------------
        // STEP 4
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
        // STEP 5
        // VERIFY USING GET
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 3 - Verifying inserted row using GET...");


        @SuppressWarnings("rawtypes")
        Map verifyResponse =
                medasApiClient.get(
                        DETAILS_ENDPOINT + createdReasonCode,
                        Map.class);


        assertNotNull(
                verifyResponse,
                "Inserted ReasonSetup record should be retrievable.");

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


        boolean recordNotFound = false;


        try
        {

            medasApiClient.get(
                    DETAILS_ENDPOINT + createdReasonCode,
                    Map.class);

        }
        catch (HttpClientErrorException.NotFound ex)
        {

            recordNotFound = true;

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