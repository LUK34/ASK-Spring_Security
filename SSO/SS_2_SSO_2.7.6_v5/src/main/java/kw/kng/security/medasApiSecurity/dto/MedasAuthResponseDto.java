package kw.kng.security.medasApiSecurity.dto;

import lombok.Data;
import java.io.Serializable;
/**
 * Response DTO used to receive authentication details from the
 * KNG MEDAS REST API token endpoint.
 *
 * <p>
 * After PATMRD successfully authenticates with KNG MEDAS, the authentication
 * endpoint returns a JWT token together with its associated metadata.
 * This DTO represents that JSON response.
 * </p>
 *
 * <pre>
 * KNG MEDAS /api/auth/token
 *          |
 *          |  MedasAuthResponseDto
 *          v
 * PATMRD KngMedasTokenService
 *          |
 *          v
 * JWT cached and used for protected REST API calls
 * </pre>
 */
@Data
public class MedasAuthResponseDto implements Serializable
{
	/**
     * Serialization version identifier.
     */
	private static final long serialVersionUID = 1L;

	/**
     * JWT access token returned by the KNG MEDAS authentication endpoint.
     *
     * This token is later included in the Authorization header when PATMRD
     * calls protected KNG MEDAS REST endpoints.
     */
    private String token;
    /**
     * Authentication scheme associated with the token.
     *
     * Normally this value is "Bearer".
     */
    private String tokenType;
    /**
     * Token validity period returned by KNG MEDAS.
     *
     * This value is used by the token-management logic to determine when the
     * cached JWT should be considered expired or refreshed.
     */
    private long expiresIn;
    /**
     * Application/client name associated with the issued JWT.
     */
    private String appName;

}
