package kw.kng.security.medasApiSecurity.token;

import org.springframework.stereotype.Service;

import kw.kng.security.medasApiSecurity.client.KngMedasAuthClient;
import kw.kng.security.medasApiSecurity.dto.MedasAuthResponseDto;

/**
 * Manages the JWT used by PATMRD when calling protected KNG MEDAS REST API endpoints.
 *
 * <p>The JWT is cached so PATMRD does not request a new token for every API call.
 * A new token is requested when no token exists, the current token is close to
 * expiry, or the cached token has been explicitly invalidated.</p>
 */
@Service
public class KngMedasTokenServiceImpl implements KngMedasTokenService 
{
	
	// #########################################################################################
	
	/*
	 * Refresh the JWT 60 seconds before its actual expiry time.
	 * This avoids using a token that may expire while a REST request is in progress.
	 */
	private static final long REFRESH_BUFFER_MS = 60_000L;

	/*
	 * Cached JWT information.
	 *
	 * volatile ensures refreshed/invalidated values are visible to other
	 * application threads using this singleton Spring service.
	 */
	private volatile String cachedToken;
	private volatile String tokenType;
	private volatile long tokenExpiryTimeMs;
	// #########################################################################################

	/*
	 * Authentication client responsible for requesting a new JWT from
	 * the KNG MEDAS authentication endpoint.
	 */
	private final KngMedasAuthClient authClient;

	public KngMedasTokenServiceImpl(KngMedasAuthClient authClient) {
		this.authClient = authClient;
	}

	// #########################################################################################

	// #########################################################################################

	/**
	 * Returns a valid JWT.
	 *
	 * If no token exists, or the current token is close to expiry, a new JWT is
	 * requested from KNG_MEDAS_REST_v1.
	 */
	@Override
	public String getValidToken() {
		if (isTokenUsable()) {
			return cachedToken;
		}

		synchronized (this) {
			/*
			 * Double check after acquiring the lock.
			 *
			 * Another request may already have refreshed the token while this thread was
			 * waiting.
			 */
			if (isTokenUsable()) {
				return cachedToken;
			}

			refreshToken();

			return cachedToken;
		}
	}

	// #########################################################################################

	// #########################################################################################

	/**
	 * Returns the complete value required for the HTTP Authorization header.
	 *
	 * Example:
	 * Bearer eyJ...
	 *
	 * getValidToken() ensures that the token is valid before the header value
	 * is returned.
	 */
	@Override
	public String getAuthorizationHeaderValue() {
		String token = getValidToken();

		String type = tokenType == null || tokenType.trim().isEmpty() ? "Bearer" : tokenType;

		return type + " " + token;
	}
	// #########################################################################################

	// #########################################################################################

	/**
	 * Explicitly invalidates the cached token.
	 *
	 * Useful when KNG MEDAS responds with 401 because the currently cached JWT is
	 * no longer accepted.
	 */
	@Override
	public synchronized void invalidateToken() {
		cachedToken = null;
		tokenType = null;
		tokenExpiryTimeMs = 0L;
	}
	// #########################################################################################

	// #########################################################################################

	/**
	 * Checks whether the currently cached JWT can still safely be used.
	 *
	 * A token is considered unusable when it is missing/blank or when the
	 * current time has entered the refresh buffer before its expiry time.
	 */
	private boolean isTokenUsable() {
		if (cachedToken == null || cachedToken.trim().isEmpty()) {
			return false;
		}

		long currentTime = System.currentTimeMillis();

		return currentTime < tokenExpiryTimeMs - REFRESH_BUFFER_MS;
	}

	/**
	 * Requests a new JWT from KNG MEDAS and updates the local token cache.
	 *
	 * The expiry timestamp is calculated using the current system time plus
	 * the expiration duration returned by the KNG MEDAS authentication endpoint.
	 */
	private void refreshToken() {
		MedasAuthResponseDto response = authClient.authenticate();

		cachedToken = response.getToken();
		tokenType = response.getTokenType() == null 
				 	|| response.getTokenType().trim().isEmpty()
					? "Bearer" 
					: response.getTokenType();
		
		long expiresIn = response.getExpiresIn();

		if (expiresIn <= 0) 
		{
			throw new IllegalStateException("KNG MEDAS returned an invalid JWT expiration.");
		}

		tokenExpiryTimeMs = System.currentTimeMillis() + expiresIn;
	}

	// #########################################################################################

}
