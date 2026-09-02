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
        // VERIFY MEDAS SERVERS ARE CONFIGURED
        // --------------------------------------------------------------------------------------------------------

        assertFalse(
                properties.getCandidateBaseUrls().isEmpty(),
                "At least one KNG MEDAS REST API server must be configured.");

        System.out.println();

        System.out.println(
                "STEP 1 - Configured MEDAS Servers");

        for (String baseUrl : properties.getCandidateBaseUrls())
        {
            System.out.println(
                    "Candidate = " + baseUrl);
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


        String activeServer =
                endpointResolver.getActiveBaseUrl();

        assertNotNull(
                activeServer,
                "Active MEDAS server should not be null.");


        System.out.println();

        System.out.println(
                "Authentication successful.");

        System.out.println(
                "Active MEDAS Server = "
                        + activeServer);


        // --------------------------------------------------------------------------------------------------------
        // STEP 3
        // CALL APPOINTMENTS REST API
        // --------------------------------------------------------------------------------------------------------

        System.out.println();

        System.out.println(
                "STEP 3 - Calling Appointments REST API...");

        System.out.println(
                "Endpoint = "
                        + TEST_GET_ENDPOINT);


        @SuppressWarnings("rawtypes")
        Map response =
                medasApiClient.get(
                        TEST_GET_ENDPOINT,
                        Map.class);


        // --------------------------------------------------------------------------------------------------------
        // STEP 4
        // VERIFY RESPONSE
        // --------------------------------------------------------------------------------------------------------

        assertNotNull(
                response,
                "Appointments REST API response should not be null.");


        System.out.println();

        System.out.println(
                "Appointments REST API response received successfully.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 5
        // DISPLAY SPRING PAGE INFORMATION
        // --------------------------------------------------------------------------------------------------------

        System.out.println();

        System.out.println(
                "STEP 5 - Appointment Page Information");

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
        // STEP 6
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
                "MEDAS Server Used = "
                        + endpointResolver.getActiveBaseUrl());

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