package kw.kng.security.medasApiSecurity.config;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/*
 	// ################################################################################################################################
 	 													
 	 												// CODE 1:
 	
 	// ################################################################################################################################
 */


/**
 * Central configuration-properties class for the KNG MEDAS REST API integration.
 *
 * <p>
 * Spring binds all properties beginning with {@code kng.medas.api} from the
 * active application properties file into this class.
 * </p>
 *
 * <p>
 * This keeps MEDAS connection settings in one place instead of hard-coding
 * server addresses, WAR context names, authentication paths, credentials,
 * and timeout values throughout the application.
 * </p>
 *
 * <p>
 * This class also builds the ordered list of MEDAS application base URLs used
 * by the API client's prime-server/fallback-server failover mechanism.
 * </p>
 *
 * <p>
 * Lombok's {@link Data} annotation generates the getters and setters required
 * by Spring configuration-property binding.
 * </p>
 */
@Component
@Data
@ConfigurationProperties(prefix = "kng.medas.api")
public class KngMedasApiProperties
{
	 /**
     * Contains the prime MEDAS server and ordered fallback servers.
     */
	private Server server = new Server();
	
	/**
	 * API Gateway configuration.
	 *
	 * API Gateway is the preferred/primary route for communication
	 * with KNG MEDAS REST API.
	 */
	private Gateway gateway = new Gateway();
	
	

	/**
     * Deployed KNG MEDAS WAR/context name.
     *
     * Example: kng_medas
     */
	private String war;

	 /*
     * Older design used one fixed base URL. The current design instead uses
     * server.prime + server.fallbacks to support failover.
     */
	// private String baseUrl;
	
	  /**
     * Relative endpoint used to obtain the JWT access token.
     *
     * Example: /api/auth/token
     */
	private String authUrl;

	 /**
     * Logical name of the application consuming the KNG MEDAS REST API.
     */
	private String appName;
	/**
     * Username sent to the KNG MEDAS authentication endpoint.
     */
	private String username;
	 /**
     * Raw server-side password used when requesting a JWT token.
     *
     * This value belongs only to the server-to-server integration and must
     * never be exposed to browser-side code.
     */
	private String password;

	 /**
     * Maximum time, in milliseconds, allowed to establish an HTTP connection
     * to a KNG MEDAS server.
     *
     * Default: 5000 ms.
     */
	private int connectTimeout = 5000;
	/**
     * Maximum time, in milliseconds, allowed to wait for the HTTP response
     * after the connection has been established.
     *
     * Default: 15000 ms.
     */
	private int readTimeout = 15000;

	// ###################################################################################################################################
	// ============================================================================================================
    // MEDAS API GATEWAY CONFIGURATION -> PRIMARY WORKFLOW -> START
    // ============================================================================================================
	// ###################################################################################################################################
	
	@Data
	public static class Gateway {

	    /**
	     * Controls whether API Gateway should be attempted as the
	     * primary MEDAS communication route.
	     */
	    private boolean enabled = true;

	    /**
	     * API Gateway server URL.
	     *
	     * DEV example:
	     * http://localhost:8888
	     *
	     * PROD example:
	     * http://10.201.49.120:8888
	     */
	    private String url;
	}
	
	
	/**
	 * Builds the KNG MEDAS application URL through API Gateway.
	 *
	 * Example:
	 *
	 * gateway.url = http://localhost:8888 (DEV) / http://10.201.49.120:8888 (PROD)
	 * war         = kng_medas
	 *
	 * result:
	 * http://localhost:8888/kng_medas
	 *
	 * @return Gateway MEDAS base URL, or null when Gateway is disabled
	 *         or no Gateway URL is configured
	 */
	public String getGatewayBaseUrl() {

	    if (gateway == null || !gateway.isEnabled()) {
	        return null;
	    }

	    if (gateway.getUrl() == null
	            || gateway.getUrl().trim().isEmpty()) {
	        return null;
	    }

	    StringBuilder baseUrl =
	            new StringBuilder(normalizeBaseUrl(gateway.getUrl()));

	    if (war != null && !war.trim().isEmpty()) {
	        baseUrl.append("/");
	        baseUrl.append(trimSlashes(war));
	    }

	    return baseUrl.toString();
	}
	
	// ###################################################################################################################################
	// ============================================================================================================
	// MEDAS API GATEWAY CONFIGURATION -> PRIMARY WORKFLOW -> END
	// ============================================================================================================
	// ###################################################################################################################################
		

	// ###################################################################################################################################
	// ============================================================================================================
    // MEDAS SERVER CONFIGURATION -> FAILOVER WORKFLOW -> START
    // ============================================================================================================
	// ###################################################################################################################################
	  /**
     * Groups the KNG MEDAS server addresses used by the failover mechanism.
     *
     * The prime server has the highest priority. Fallback servers are evaluated
     * in their configured order.
     */
	@Data
	public static class Server {
		  /**
         * Highest-priority KNG MEDAS Tomcat server.
         */
		private String prime;

		 /**
         * Ordered list of fallback KNG MEDAS Tomcat servers.
         */
		private List<String> fallbacks = new ArrayList<>();
	}

	 // ============================================================================================================
    // CANDIDATE BASE URL CONSTRUCTION
    // ============================================================================================================

	
	 /**
     * Returns all configured KNG MEDAS application base URLs in priority order.
     *
     * <p>
     * Order:
     * </p>
     *
     * <pre>
     * 1. Prime server
     * 2. Fallback server 1
     * 3. Fallback server 2
     * 4. ...
     * </pre>
     *
     * <p>
     * The configured WAR/context name is appended to every Tomcat server URL.
     * A {@link LinkedHashSet} is deliberately used because it preserves the
     * insertion/priority order while also removing duplicate URLs.
     * </p>
     *
     * @return ordered, de-duplicated KNG MEDAS application base URLs
     */
	public List<String> getCandidateBaseUrls() 
	{
		Set<String> urls = new LinkedHashSet<>();

		if (server != null) 
		{
			// Always add the prime server first because it has highest priority.
			addServerUrl(urls, server.getPrime());

			// Add fallback servers in exactly the order configured.
			if (server.getFallbacks() != null) 
			{
				for (String fallback : server.getFallbacks()) 
				{
					addServerUrl(urls, fallback);
				}
			}
		}

		return new ArrayList<>(urls);
	}

	// ----------------------------------------------------------------------------------

	// ============================================================================================================
    // AUTHENTICATION URL CONSTRUCTION
    // ============================================================================================================


    /**
     * Builds the complete JWT authentication URL for a supplied KNG MEDAS
     * application base URL.
     *
     * <pre>
     * baseUrl = http://server:8080/kng_medas
     * authUrl = /api/auth/token
     *
     * result  = http://server:8080/kng_medas/api/auth/token
     * </pre>
     *
     * @param baseUrl KNG MEDAS application base URL
     * @return complete token endpoint URL
     */
	public String buildTokenUrl(String baseUrl) {
		return normalizeBaseUrl(baseUrl) + normalizePath(authUrl);
	}

	// ============================================================================================================
    // INTERNAL URL NORMALIZATION HELPERS
    // ============================================================================================================

    /**
     * Adds one configured Tomcat server to the candidate URL collection.
     *
     * Null/blank server values are ignored. If a WAR/context is configured,
     * it is appended to the normalized Tomcat server URL.
     *
     * @param urls ordered set collecting candidate MEDAS URLs
     * @param serverUrl configured Tomcat server URL
     */
	private void addServerUrl(Set<String> urls, String serverUrl) 
	{
		
		if (serverUrl == null || serverUrl.trim().isEmpty()) 
		{
			return;
		}

		 /*
         * Normalize the server URL first so appending the WAR name does not
         * accidentally produce duplicate slash characters.
         */
		StringBuilder baseUrl = new StringBuilder(normalizeBaseUrl(serverUrl));

		if (war != null && !war.trim().isEmpty()) 
		{
			baseUrl.append("/");
			baseUrl.append(trimSlashes(war));
		}

		/*
         * LinkedHashSet automatically removes duplicates while preserving
         * the configured server priority order.
         */
		urls.add(baseUrl.toString());
	}

	 /**
     * Trims whitespace and removes trailing slashes from a base URL.
     *
     * @param value base URL to normalize
     * @return normalized URL, or an empty string when the value is null
     */
	private String normalizeBaseUrl(String value) 
	{
		if (value == null) 
		{
			return "";
		}

		String result = value.trim();

		while (result.endsWith("/")) 
		{
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	  /**
     * Normalizes an endpoint path so a non-empty path always starts with "/".
     *
     * Example:
     * api/auth/token -> /api/auth/token
     *
     * @param value endpoint path
     * @return normalized endpoint path, or an empty string for null/blank input
     */
	private String normalizePath(String value) 
	{
		if (value == null || value.trim().isEmpty()) 
		{
			return "";
		}

		String path = value.trim();

		if (!path.startsWith("/")) 
		{
			path = "/" + path;
		}

		return path;
	}

	 /**
     * Removes leading and trailing slash characters from a value.
     *
     * This is mainly used for the WAR/context name so values such as
     * "kng_medas", "/kng_medas", and "/kng_medas/" produce the same URL.
     *
     * @param value value to clean
     * @return value without surrounding slash characters
     */
	private String trimSlashes(String value) 
	{
		String result = value.trim();

		while (result.startsWith("/")) 
		{
			result = result.substring(1);
		}

		while (result.endsWith("/")) 
		{
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}
	// ----------------------------------------------------------------------------------

	// ###################################################################################################################################
	// ============================================================================================================
    // MEDAS SERVER CONFIGURATION -> FAILOVER WORKFLOW -> END
    // ============================================================================================================
	// ###################################################################################################################################

}
