package kw.kng.notes.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig 
{
	/*
	 * ============================================================================================
	 * SPRING SECURITY FILTER CHAIN CONFIGURATION
	 * ============================================================================================
	 *
	 * This bean defines the main security rules for incoming HTTP requests.
	 *
	 * SecurityFilterChain tells Spring Security:
	 *
	 * 1. Which requests require authentication.
	 * 2. Whether CSRF protection is enabled or disabled.
	 * 3. Which authentication mechanism should be used.
	 *
	 * In this configuration:
	 *
	 * - Every request requires authentication.
	 * - CSRF protection is disabled.
	 * - Form Login is disabled/commented out.
	 * - HTTP Basic Authentication is enabled.
	 *
	 * Therefore, clients must provide a valid username and password
	 * using HTTP Basic Authentication to access protected endpoints.
	 *
	 * The users can be loaded from the UserDetailsService bean.
	 * In our current example, InMemoryUserDetailsManager is being used.
	 * ============================================================================================
	 */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception 
    {
        /*
         * -------------------------------------------------------------------------
         * AUTHORIZATION CONFIGURATION
         * -------------------------------------------------------------------------
         *
         * anyRequest().authenticated()
         *
         * This means EVERY HTTP request reaching the application
         * must be made by an authenticated user.
         *
         * Examples:
         *
         * GET    /api/notes
         * POST   /api/notes
         * PUT    /api/notes/1
         * DELETE /api/notes/1
         *
         * All of the above require authentication.
         *
         * This rule does NOT specify which roles are required.
         * It only requires the user to be successfully authenticated.
         */
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
        /*
         * -------------------------------------------------------------------------
         * CSRF CONFIGURATION
         * -------------------------------------------------------------------------
         *
         * Disable CSRF (Cross-Site Request Forgery) protection.
         *
         * CSRF protection is especially important for browser applications
         * where authentication credentials, such as session cookies, are
         * automatically sent by the browser.
         *
         * For stateless REST APIs using authentication mechanisms where
         * credentials are explicitly supplied with requests, CSRF protection
         * is commonly disabled depending on the application's architecture.
         *
         * IMPORTANT:
         * Do not blindly disable CSRF for every Spring Security application.
         * The decision should depend on how authentication is performed.
         */
        http.csrf(AbstractHttpConfigurer::disable);
        /*
         * -------------------------------------------------------------------------
         * FORM LOGIN
         * -------------------------------------------------------------------------
         *
         * Spring Security's default browser-based login form is currently
         * DISABLED because this line is commented out.
         *
         * If enabled:
         *
         * http.formLogin(withDefaults());
         *
         * Spring Security would configure form-based authentication.
         */
        
        //http.formLogin(withDefaults());
        /*
         * -------------------------------------------------------------------------
         * HTTP BASIC AUTHENTICATION
         * -------------------------------------------------------------------------
         *
         * Enable HTTP Basic Authentication using Spring Security's
         * default configuration.
         *
         * The client sends the username and password using the HTTP
         * Authorization header.
         *
         * Conceptually:
         *
         * Authorization: Basic <Base64(username:password)>
         *
         * Example credentials from our InMemoryUserDetailsManager:
         *
         * Username : user1
         * Password : password1
         *
         * Spring Security receives the credentials and passes them through
         * its authentication infrastructure.
         */
        http.httpBasic(withDefaults());
        /*
         * -------------------------------------------------------------------------
         * BUILD SECURITY FILTER CHAIN
         * -------------------------------------------------------------------------
         *
         * http.build() creates the configured SecurityFilterChain.
         *
         * Spring Security will use this filter chain to process incoming
         * HTTP requests according to the rules configured above.
         */
        return http.build();
    }
    
    
    /*
     * ============================================================================================
     * IN-MEMORY AUTHENTICATION
     * ============================================================================================
     *
     * This bean creates and manages application users directly in memory.
     *
     * No database, repository, or external authentication system is used to
     * retrieve these users.
     *
     * InMemoryUserDetailsManager is an implementation of UserDetailsService.
     * Spring Security uses this UserDetailsService during username/password
     * authentication to retrieve the corresponding UserDetails object.
     *
     * NOTE:
     * The users configured here exist in the application's in-memory user store.
     * They are recreated whenever the application is restarted.
     *
     * This approach is mainly suitable for:
     * 1. Development
     * 2. Testing
     * 3. Learning Spring Security
     * 4. Proof-of-concept applications
     *
     * "{noop}" tells Spring Security that the password is stored as plain text
     * and that no password encoding should be performed.
     *
     * IMPORTANT:
     * {noop} should only be used for learning/testing purposes.
     * For production applications, use a secure PasswordEncoder such as BCrypt.
     * ============================================================================================
     */
    @Bean
    public UserDetailsService userDetailsService() 
    {
    	 /*
         * InMemoryUserDetailsManager stores and manages UserDetails
         * objects directly in application memory.
         */
        InMemoryUserDetailsManager manager =
                new InMemoryUserDetailsManager();
        /*
         * Create USER account:
         *
         * Username : user1
         * Password : password1
         * Role     : USER
         *
         * userExists() prevents creation of the same username
         * more than once in this manager.
         */
        if (!manager.userExists("user1")) {
            manager.createUser(
                    User.withUsername("user1")
                    		// {noop} means the password is stored as plain text.
                            .password("{noop}password1")
                            // roles("USER") results in authority ROLE_USER.
                            .roles("USER")
                            // Creates the final UserDetails object.
                            .build()
            );
        }
        /*
         * Create ADMIN account:
         *
         * Username : admin
         * Password : adminPass
         * Role     : ADMIN
         */
        if (!manager.userExists("admin")) {
            manager.createUser(
                    User.withUsername("admin")
                            .password("{noop}adminPass")
                            .roles("ADMIN")
                            .build()
            );
        }
        /*
         * Create another ADMIN account:
         *
         * Username : kunjus
         * Password : kunjusPass
         * Role     : ADMIN
         */
        if (!manager.userExists("kunjus")) {
            manager.createUser(
                    User.withUsername("kunjus")
                            .password("{noop}kunjusPass")
                            .roles("ADMIN")
                            .build()
            );
        }
        /*
         * Create another ADMIN account:
         *
         * Username : kane
         * Password : kanePass
         * Role     : ADMIN
         *
         * NOTE:
         * The original code checked userExists("kane")
         * but created User.withUsername("kame").
         *
         * Both values should refer to the same username.
         */
        if (!manager.userExists("kane")) {
            manager.createUser(
                    User.withUsername("kane")
                            .password("{noop}kanePass")
                            .roles("ADMIN")
                            .build()
            );
        }
        
        /*
         * Return the configured InMemoryUserDetailsManager.
         *
         * Since InMemoryUserDetailsManager implements UserDetailsService,
         * Spring Security can use this bean during authentication.
         */
        return manager;
    }

}
