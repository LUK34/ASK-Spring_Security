package kw.kng.security.medasApiSecurity.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO used when PATMRD authenticates with the KNG MEDAS REST API.
 *
 * <p>
 * This object represents the JSON request body sent to the KNG MEDAS
 * authentication/token endpoint.
 * </p>
 *
 * <pre>
 * PATMRD
 *   |
 *   |  MedasAuthRequestDto
 *   |  { appName, username, password }
 *   v
 * KNG MEDAS /api/auth/token
 *   |
 *   v
 * JWT authentication response
 * </pre>
 *
 * <p>
 * This DTO is used only for server-to-server authentication. The credentials
 * contained in this object must not be exposed to browser-side code.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedasAuthRequestDto implements Serializable
{
	/**
     * Serialization version identifier.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logical name of the application requesting access to the KNG MEDAS API.
     */
    private String appName;
    
    /**
     * Username configured for this application/client in KNG MEDAS.
     */
    private String username;
 
    /**
     * Raw client password sent to the KNG MEDAS authentication endpoint.
     *
     * This value is used only during server-to-server authentication and
     * must not be exposed to browser/client-side code.
     */
    private String password;
	

}
