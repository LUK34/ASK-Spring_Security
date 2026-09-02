package kw.kng.security.medasApiSecurity.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import kw.kng.security.medasApiSecurity.config.KngMedasApiProperties;

/**
 * Resolves and tracks the active KNG MEDAS REST API server.
 *
 * <p>
 * The configured PRIME server and fallback servers are obtained from
 * {@link KngMedasApiProperties}.
 * </p>
 *
 * <p>
 * Once a working MEDAS server is identified, it is stored as the active
 * server and is tried first for subsequent REST API calls.
 * </p>
 *
 * <p>
 * If the active server later becomes unavailable, it can be invalidated.
 * The next MEDAS authentication/API resolution cycle can then search the
 * configured PRIME -> fallback server list again.
 * </p>
 */
@Service
public class KngMedasEndpointResolverImpl implements KngMedasEndpointResolver 
{
	/*
	 * Central KNG MEDAS configuration containing the PRIME server,
	 * fallback servers and related API configuration.
	 */
	private final KngMedasApiProperties properties;

	/*
	 * Server currently known to be working.
	 *
	 * volatile ensures that changes to the active server are visible to
	 * application threads that may be making MEDAS REST API calls concurrently.
	 */
	private volatile String activeBaseUrl;

	
	/**
	 * Creates the endpoint resolver using the configured KNG MEDAS properties.
	 */
	public KngMedasEndpointResolverImpl(KngMedasApiProperties properties) 
	{
		this.properties = properties;
	}

	/**
	 * Returns the MEDAS base URLs in the order they should be attempted.
	 *
	 * <p>
	 * If no active server is currently known, the configured order is returned:
	 * PRIME server first, followed by the configured fallback servers.
	 * </p>
	 *
	 * <p>
	 * If a working server is already known, that active server is placed first
	 * so subsequent MEDAS calls continue using the known working server.
	 * Remaining configured servers are then added without duplicating the
	 * active URL.
	 * </p>
	 */
	@Override
	public List<String> getCandidateBaseUrls() 
	{
		List<String> configured = properties.getCandidateBaseUrls();

		/*
		 * When we already know a working server, try it first.
		 */
		if (activeBaseUrl == null) 
		{
			return configured;
		}

		List<String> ordered = new ArrayList<>();

		// Known working server receives the highest priority.
		ordered.add(activeBaseUrl);

		/*
		 * Add the remaining configured servers while avoiding duplication
		 * of the active server already added above.
		 */
		for (String url : configured) 
		{
			if (!activeBaseUrl.equals(url)) 
			{
				ordered.add(url);
			}
		}

		return ordered;
	}

	/**
	 * Returns the MEDAS server currently considered active.
	 *
	 * @return active MEDAS base URL, or null if no server is currently selected
	 */
	@Override
	public String getActiveBaseUrl() {
		return activeBaseUrl;
	}

	/**
	 * Marks the supplied MEDAS base URL as the currently active/working server.
	 *
	 * synchronized protects the update when multiple application threads are
	 * performing MEDAS operations at the same time.
	 */
	@Override
	public synchronized void markActive(String baseUrl) {
		this.activeBaseUrl = baseUrl;
	}

	/**
	 * Invalidates the active server only when the supplied URL matches the
	 * server that is currently marked as active.
	 *
	 * This prevents a failure relating to another URL from accidentally
	 * clearing the currently selected working server.
	 */
	@Override
	public synchronized void invalidate(String baseUrl) {
		if (baseUrl != null && baseUrl.equals(activeBaseUrl)) {
			activeBaseUrl = null;
		}
	}

	/**
	 * Unconditionally clears the currently active MEDAS server.
	 *
	 * The next MEDAS resolution/authentication cycle will therefore need to
	 * identify a working server again.
	 */
	@Override
	public synchronized void invalidate() {
		activeBaseUrl = null;
	}
}
