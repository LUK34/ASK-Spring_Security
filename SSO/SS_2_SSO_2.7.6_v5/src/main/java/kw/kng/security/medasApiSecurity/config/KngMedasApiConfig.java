package kw.kng.security.medasApiSecurity.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
/**
 * Configuration class for the KNG MEDAS REST API client.
 *
 * <p>
 * This class is part of the PATMRD -> KNG MEDAS server-to-server integration.
 * Its responsibility is to create and configure the {@link RestTemplate}
 * instance used by the MEDAS API client classes when communicating with the
 * secured KNG MEDAS REST API.
 * </p>
 *
 * <p>
 * The connection and read timeout values are not hard-coded here.
 * They are read from {@link KngMedasApiProperties}, which obtains them from
 * the application's configured KNG MEDAS API properties.
 * </p>
 *
 * <p>
 * Typical request flow:
 * </p>
 *
 * <pre>
 * PATMRD Service
 *      |
 *      v
 * KngMedasApiClient
 *      |
 *      v
 * kngMedasRestTemplate
 *      |
 *      v
 * KNG MEDAS REST API
 * </pre>
 *
 * <p>
 * Keeping this RestTemplate as a dedicated named bean prevents it from being
 * confused with any other RestTemplate instances that may exist in PATMRD.
 * </p>
 */
@Configuration
public class KngMedasApiConfig 
{
    /**
     * Creates the dedicated RestTemplate used for KNG MEDAS API communication.
     *
     * <p>
     * The bean is explicitly named "kngMedasRestTemplate" so that other
     * components can inject this exact RestTemplate using the same bean name
     * (for example through @Qualifier).
     * </p>
     *
     * <p>
     * Two timeout values are configured:
     * </p>
     *
     * <ul>
     *     <li>
     *         Connect timeout - maximum time PATMRD will wait while trying
     *         to establish a connection to a KNG MEDAS REST API server.
     *     </li>
     *     <li>
     *         Read timeout - maximum time PATMRD will wait for the REST API
     *         response after the connection has already been established.
     *     </li>
     * </ul>
     *
     * @param builder
     *        Spring Boot's RestTemplateBuilder used to construct the client.
     *
     * @param properties
     *        KNG MEDAS API configuration properties containing the timeout
     *        values used by this RestTemplate.
     *
     * @return configured RestTemplate dedicated to KNG MEDAS REST API calls
     */
	 	@Bean(name = "kngMedasRestTemplate")
	    public RestTemplate kngMedasRestTemplate(RestTemplateBuilder builder,KngMedasApiProperties properties)
	    {
	        return builder
	        		// Maximum time allowed to establish the HTTP connection.
	                .setConnectTimeout(Duration.ofMillis(properties.getConnectTimeout()))
	                // Maximum time allowed to wait for the HTTP response.
	                .setReadTimeout(Duration.ofMillis(properties.getReadTimeout()))
	                // Build and return the configured RestTemplate instance.
	                .build();
	    }

}
