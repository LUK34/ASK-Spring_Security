package kw.kng.notes.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import kw.kng.notes.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * ============================================================================================
 * CUSTOM USER DETAILS IMPLEMENTATION
 * ============================================================================================
 *
 * PURPOSE:
 *
 * Spring Security works with the UserDetails interface when it needs information
 * about an authenticated user.
 *
 * Our application already has its own custom entity:
 *
 *     kw.kng.notes.entities.User
 *
 * However, Spring Security does not directly know how our custom User entity
 * should be represented during authentication and authorization.
 *
 * This class acts as the BRIDGE / ADAPTER between:
 *
 *     Application User Entity
 *              |
 *              v
 *        UserDetailsImpl
 *              |
 *              v
 *     Spring Security UserDetails
 *
 * By implementing UserDetails, this class converts the fields from our
 * application-specific User entity into the standardized structure that
 * Spring Security understands.
 *
 * It contains:
 *
 * 1. User ID
 * 2. Username
 * 3. Email
 * 4. Password
 * 5. Two-factor-authentication flag
 * 6. Granted authorities / roles
 * 7. Account-status methods required by UserDetails
 *
 * ============================================================================================
 */
@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails 
{
	 /*
     * UserDetails extends Serializable.
     *
     * serialVersionUID is used during Java object serialization to identify
     * the version of the serialized class.
     */
    private static final long serialVersionUID = 1L;

    /*
     * Application-specific unique identifier for the user.
     *
     * This field is NOT one of the standard UserDetails methods, but we keep it
     * because our application may need access to the database/user ID after
     * authentication.
     */
    private Long id;
    /*
     * Username used by Spring Security to identify the user during authentication.
     *
     * This value is returned by getUsername().
     */
    private String username;

    /*
     * Email is a custom attribute belonging to our application's user model.
     *
     * UserDetails itself does not require an email field, but our custom
     * implementation can store additional attributes needed by the application.
     */
    private String email;

    /*
     * Password associated with the user.
     *
     * @JsonIgnore prevents this field from being included when this object is
     * serialized into JSON by Jackson.
     *
     * This is useful when UserDetailsImpl is exposed through an API response,
     * because the password should never be returned to the client.
     *
     * NOTE:
     * @JsonIgnore does not encrypt or hash the password. Password protection
     * still depends on how passwords are stored and encoded in the application.
     */
    @JsonIgnore
    private String password;

    /*
     * Custom application-specific security flag.
     *
     * This tells us whether two-factor authentication is enabled for this user.
     *
     * UserDetails does not define this field; it exists because our custom user
     * model has additional security requirements.
     */
    private boolean is2faEnabled;


    /*
     * Collection of authorities granted to the authenticated user.
     *
     * An authority can represent a role or permission, for example:
     *
     *     ROLE_USER
     *     ROLE_ADMIN
     *
     * Spring Security uses these authorities during authorization.
     */
    private Collection<? extends GrantedAuthority> authorities;

    /*
     * ========================================================================================
     * CONSTRUCTOR
     * ========================================================================================
     *
     * Creates a fully populated UserDetailsImpl object.
     *
     * This constructor is mainly used by the static build(User user) method below
     * after data has been retrieved from our application's User entity.
     */
    public UserDetailsImpl(Long id, String username, String email, String password,
                           boolean is2faEnabled, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.is2faEnabled = is2faEnabled;
        this.authorities = authorities;
    }

    /*
     * ========================================================================================
     * BUILD / CONVERSION METHOD
     * ========================================================================================
     *
     * This static method converts our application's User entity into a
     * UserDetailsImpl object.
     *
     * This is one of the most important parts of this class.
     *
     * Flow:
     *
     *     User Entity fetched from database
     *                  |
     *                  v
     *          UserDetailsImpl.build(user)
     *                  |
     *                  v
     *          UserDetailsImpl object
     *                  |
     *                  v
     *          Spring Security UserDetails
     *
     * In other words, this method translates our CUSTOM USER MODEL into a format
     * that Spring Security can understand.
     */
    public static UserDetailsImpl build(User user) 
    {
    	  /*
         * Convert the role stored in our application's User entity into a
         * Spring Security GrantedAuthority.
         *
         * Example:
         *
         *     user.getRole().getRoleName().name()
         *
         * could produce something such as:
         *
         *     ROLE_USER
         *     ROLE_ADMIN
         *
         * SimpleGrantedAuthority is Spring Security's simple implementation
         * of the GrantedAuthority interface.
         */
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName().name());

        /*
         * Create the UserDetailsImpl object by mapping values from our
         * application's User entity.
         *
         * Mapping:
         *
         * User.userId            -> UserDetailsImpl.id
         * User.userName          -> UserDetailsImpl.username
         * User.email             -> UserDetailsImpl.email
         * User.password          -> UserDetailsImpl.password
         * User.twoFactorEnabled  -> UserDetailsImpl.is2faEnabled
         * User.role              -> UserDetailsImpl.authorities
         *
         * List.of(authority) wraps the single GrantedAuthority into a collection
         * because UserDetails.getAuthorities() expects a collection.
         */
        return new UserDetailsImpl(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                user.isTwoFactorEnabled(),
                List.of(authority) // Wrapping the single authority in a list
        );
    }
    
    /*
     * ========================================================================================
     * USERDETAILS METHODS
     * ========================================================================================
     *
     * The following methods come from the UserDetails interface.
     *
     * Spring Security calls them when it needs security information about
     * the currently loaded user.
     */


    
    /*
     * Return all roles/permissions granted to the user.
     *
     * Spring Security uses these values for authorization checks.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /*
     * Custom helper method.
     *
     * Returns the application's internal user ID.
     *
     * This method is not required by UserDetails, but it allows application
     * code to retrieve the ID of the authenticated user when necessary.
     */
    public Long getId() {
        return id;
    }

    /*
     * Custom helper method.
     *
     * Email is not part of the standard UserDetails contract, but our custom
     * UserDetails implementation exposes it because our application stores it.
     */
    public String getEmail() {
        return email;
    }

    /*
     * Return the user's password.
     *
     * Spring Security's authentication provider can use this value while
     * verifying the credentials supplied during authentication.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /*
     * Return the username used to identify the user.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /*
     * Indicates whether the user's account has NOT expired.
     *
     * Current implementation:
     *
     *     true
     *
     * Therefore every loaded user is currently treated as having a
     * non-expired account.
     *
     * In the future, this could be connected to a database field such as:
     *
     *     accountExpired
     *     accountExpiryDate
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /*
     * Indicates whether the user's account is NOT locked.
     *
     * Current implementation:
     *
     *     true
     *
     * Therefore every loaded user is currently treated as unlocked.
     *
     * This can later be customized using fields such as:
     *
     *     accountLocked
     *     failedLoginAttempts
     *     lockedUntil
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /*
     * Indicates whether the user's credentials have NOT expired.
     *
     * Current implementation:
     *
     *     true
     *
     * Therefore Spring Security currently treats all user credentials
     * as non-expired.
     *
     * This could later be linked to password-expiration logic.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /*
     * Indicates whether the user account is enabled.
     *
     * Current implementation:
     *
     *     true
     *
     * Therefore every user returned by this implementation is currently
     * treated as enabled.
     *
     * This could later be mapped to an enabled/disabled field in the User entity.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
     * Custom getter for the application's two-factor-authentication flag.
     *
     * This allows other security logic to determine whether the user has
     * two-factor authentication enabled.
     *
     * This method is not part of the standard UserDetails interface.
     */
    public boolean is2faEnabled() {
        return is2faEnabled;
    }

    /*
     * ========================================================================================
     * EQUALITY CHECK
     * ========================================================================================
     *
     * Two UserDetailsImpl objects are considered equal when they represent
     * the same application user ID.
     *
     * Example:
     *
     *     UserDetailsImpl A -> id = 10
     *     UserDetailsImpl B -> id = 10
     *
     *     A.equals(B) -> true
     *
     * This gives the custom security principal an identity based on the
     * application's user ID.
     */
    @Override
    public boolean equals(Object o) 
    {
    	/*
         * Same object reference -> definitely equal.
         */
        if (this == o)
            return true;
        /*
         * Null or different class -> not equal.
         */
        if (o == null || getClass() != o.getClass())
            return false;
        /*
         * Cast the incoming object to UserDetailsImpl.
         */
        UserDetailsImpl user = (UserDetailsImpl) o;
        /*
         * Compare users using their application user ID.
         */
        return Objects.equals(id, user.id);
    }
}
