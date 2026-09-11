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

/**
 * Manual integration test for KNG MEDAS JWT authentication.
 *
 * Expected architecture:
 *
 * PRIMARY:
 * PATMRD -> API Gateway -> KNG MEDAS REST
 *
 * SECONDARY:
 * PATMRD -> Direct MEDAS PRIME / fallback servers
 *
 * IMPORTANT:
 * When authentication succeeds through the API Gateway,
 * KngMedasEndpointResolver does NOT contain an active server.
 *
 * The endpoint resolver tracks only the secondary direct
 * MEDAS route.
 */
//@Disabled("Manual KNG MEDAS integration test - do not run during normal WAR build")
@SpringBootTest(classes = {
        KngMedasApiConfig.class,
        KngMedasEndpointResolverImpl.class,
        KngMedasAuthClient.class,
        KngMedasTokenServiceImpl.class,
        KngMedasAuthenticationTest.TestConfig.class
})
@ActiveProfiles("prod")
public class KngMedasAuthenticationTest
{

    @Autowired
    private KngMedasTokenService tokenService;

    @Autowired
    private KngMedasApiProperties properties;

    @Autowired
    private KngMedasEndpointResolver endpointResolver;


    @Test
    public void testKngMedasAuthentication()
    {
        System.out.println(
                "=======================================================");

        System.out.println(
                "KNG MEDAS JWT AUTHENTICATION TEST -> START");

        System.out.println(
                "=======================================================");


        // --------------------------------------------------------------------------------------------------------
        // STEP 1
        // VERIFY CONFIGURATION
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 1 - KNG MEDAS CONFIGURATION");

        System.out.println(
                "Auth URL = "
                        + properties.getAuthUrl());

        System.out.println(
                "WAR = "
                        + properties.getWar());

        System.out.println(
                "App Name = "
                        + properties.getAppName());


        boolean usernameConfigured =
                properties.getUsername() != null
                        && !properties.getUsername()
                                .trim()
                                .isEmpty();

        boolean passwordConfigured =
                properties.getPassword() != null
                        && !properties.getPassword()
                                .trim()
                                .isEmpty();


        System.out.println(
                "Username configured = "
                        + usernameConfigured);

        System.out.println(
                "Password configured = "
                        + passwordConfigured);


        // --------------------------------------------------------------------------------------------------------
        // STEP 2
        // DISPLAY GATEWAY CONFIGURATION
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 2 - API GATEWAY CONFIGURATION");


        String gatewayBaseUrl =
                properties.getGatewayBaseUrl();


        if (gatewayBaseUrl != null)
        {
            System.out.println(
                    "Gateway enabled/configured = true");

            System.out.println(
                    "Gateway Base URL = "
                            + gatewayBaseUrl);

            System.out.println(
                    "Gateway Token URL = "
                            + properties.buildTokenUrl(
                                    gatewayBaseUrl));
        }
        else
        {
            System.out.println(
                    "Gateway enabled/configured = false");
        }


        // --------------------------------------------------------------------------------------------------------
        // STEP 3
        // DISPLAY SECONDARY DIRECT MEDAS CONFIGURATION
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 3 - DIRECT MEDAS FALLBACK SERVERS");


        for (String baseUrl :
                properties.getCandidateBaseUrls())
        {
            System.out.println(
                    "Direct Base URL = "
                            + baseUrl);

            System.out.println(
                    "Direct Token URL = "
                            + properties.buildTokenUrl(
                                    baseUrl));
        }


        // --------------------------------------------------------------------------------------------------------
        // STEP 4
        // AUTHENTICATE
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "STEP 4 - Requesting JWT...");


        String token =
                tokenService.getValidToken();


        // --------------------------------------------------------------------------------------------------------
        // STEP 5
        // VERIFY JWT
        // --------------------------------------------------------------------------------------------------------

        assertNotNull(
                token,
                "KNG MEDAS JWT token should not be null.");

        assertFalse(
                token.trim().isEmpty(),
                "KNG MEDAS JWT token should not be empty.");


        /*
         * SECURITY:
         *
         * Never print:
         *
         * - JWT
         * - password
         * - Authorization header
         */
        System.out.println();
        System.out.println(
                "JWT received successfully.");


        // --------------------------------------------------------------------------------------------------------
        // STEP 6
        // IDENTIFY ROUTE
        // --------------------------------------------------------------------------------------------------------

        String activeDirectBaseUrl =
                endpointResolver.getActiveBaseUrl();


        System.out.println();
        System.out.println(
                "STEP 5 - AUTHENTICATION ROUTE RESULT");


        if (activeDirectBaseUrl == null)
        {
            /*
             * This is the normal result when Gateway authentication
             * succeeds.
             *
             * KngMedasEndpointResolver tracks ONLY direct MEDAS
             * servers, therefore null here is valid.
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
             * A direct server being active means authentication
             * was completed through the secondary direct route.
             */
            System.out.println(
                    "Authentication Route = DIRECT MEDAS");

            System.out.println(
                    "Active Direct MEDAS Server = "
                            + activeDirectBaseUrl);
        }


        // --------------------------------------------------------------------------------------------------------
        // SUCCESS
        // --------------------------------------------------------------------------------------------------------

        System.out.println();
        System.out.println(
                "=======================================================");

        System.out.println(
                "KNG MEDAS JWT AUTHENTICATION -> SUCCESS");

        System.out.println(
                "=======================================================");

        System.out.println(
                "JWT was obtained successfully.");

        System.out.println(
                "Actual JWT value was NOT printed.");

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