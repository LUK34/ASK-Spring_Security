package kw.kng.notes.security.config;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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
     * JDBC-BASED AUTHENTICATION
     * ============================================================================================
     *
     * This bean configures a JDBC-based UserDetailsService for Spring Security.
     *
     * Unlike InMemoryUserDetailsManager, JdbcUserDetailsManager stores and retrieves
     * user information from a relational database through the configured DataSource.
     *
     * JdbcUserDetailsManager implements UserDetailsManager, which itself extends
     * UserDetailsService.
     *
     * Therefore, JdbcUserDetailsManager can:
     *
     * 1. Load users by username.
     * 2. Create users.
     * 3. Update users.
     * 4. Delete users.
     * 5. Check whether a user exists.
     * 6. Change passwords.
     *
     * Spring Security can use this UserDetailsService during username/password
     * authentication to retrieve the corresponding UserDetails from the database.
     *
     * IMPORTANT:
     *
     * JdbcUserDetailsManager expects the database to contain the required user and
     * authority tables/columns, unless its default SQL queries are customized.
     *
     * The users created below are persisted in the database. Therefore, unlike
     * purely in-memory users, they normally remain available after the application
     * is restarted.
     *
     * "{noop}" tells Spring Security that the stored password is plain text and
     * should be compared without password hashing.
     *
     * IMPORTANT SECURITY NOTE:
     *
     * {noop} is suitable only for learning/testing.
     * Production applications should use a secure PasswordEncoder such as BCrypt.
     *
     * ============================================================================================
     */
    
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) 
    {
    	 /*
         * Create JdbcUserDetailsManager using the application's configured DataSource.
         *
         * JdbcUserDetailsManager communicates with the relational database using JDBC
         * to load and manage users and their authorities.
         *
         * Since JdbcUserDetailsManager implements UserDetailsManager, and
         * UserDetailsManager extends UserDetailsService, this object can be returned
         * as a UserDetailsService bean.
         */
        JdbcUserDetailsManager manager =    new JdbcUserDetailsManager(dataSource);
    	
        /*
         * ----------------------------------------------------------------------------------------
         * CREATE USER ACCOUNT
         * ----------------------------------------------------------------------------------------
         *
         * Username : user1
         * Password : password1
         * Role     : USER
         *
         * userExists("user1") checks the database to determine whether the user
         * already exists.
         *
         * The user is created only when the username is not already present.
         *
         * This prevents the application from attempting to insert the same user
         * every time the application starts.
         */
        if (!manager.userExists("user1"))
        {
            manager.createUser(
                    User.withUsername("user1")

                            /*
                             * {noop} means that this password is stored without
                             * password hashing.
                             *
                             * Learning/testing only.
                             */
                            .password("{noop}password1")

                            /*
                             * roles("USER") automatically creates the authority:
                             *
                             * ROLE_USER
                             */
                            .roles("USER")

                            /*
                             * build() creates the UserDetails object that will
                             * be passed to JdbcUserDetailsManager.
                             */
                            .build()
            );
        }
        /*
         * ----------------------------------------------------------------------------------------
         * CREATE ADMIN ACCOUNT
         * ----------------------------------------------------------------------------------------
         *
         * Username : admin
         * Password : adminPass
         * Role     : ADMIN
         *
         * First check whether "admin" already exists in the database.
         * If it does not exist, create and persist the user.
         */
        if (!manager.userExists("admin"))
        {
            manager.createUser(
                    User.withUsername("admin")
                            .password("{noop}adminPass")

                            /*
                             * roles("ADMIN") results in the authority:
                             *
                             * ROLE_ADMIN
                             */
                            .roles("ADMIN")

                            .build()
            );
        }
        /*
         * ----------------------------------------------------------------------------------------
         * CREATE ANOTHER ADMIN ACCOUNT
         * ----------------------------------------------------------------------------------------
         *
         * Username : kunjus
         * Password : kunjusPass
         * Role     : ADMIN
         *
         * The user is inserted into the database only when the username
         * does not already exist.
         */
        if (!manager.userExists("kunjus"))
        {
            manager.createUser(
                    User.withUsername("kunjus")
                            .password("{noop}kunjusPass")
                            .roles("ADMIN")
                            .build()
            );
        }

        /*
         * ----------------------------------------------------------------------------------------
         * CREATE ANOTHER ADMIN ACCOUNT
         * ----------------------------------------------------------------------------------------
         *
         * Username : kane
         * Password : kanePass
         * Role     : ADMIN
         *
         * Both userExists() and withUsername() use the same username
         * so that the existence check matches the user being created.
         */
        if (!manager.userExists("kane"))
        {
            manager.createUser(
                    User.withUsername("kane")
                            .password("{noop}kanePass")
                            .roles("ADMIN")
                            .build()
            );
        }
        
        /*
         * ----------------------------------------------------------------------------------------
         * RETURN JDBC USER DETAILS SERVICE
         * ----------------------------------------------------------------------------------------
         *
         * Return the configured JdbcUserDetailsManager.
         *
         * JdbcUserDetailsManager implements UserDetailsManager, and
         * UserDetailsManager extends UserDetailsService:
         *
         * UserDetailsService
         *        ^
         *        |
         * UserDetailsManager
         *        ^
         *        |
         * JdbcUserDetailsManager
         *
         * Therefore, JdbcUserDetailsManager can be returned from this
         * UserDetailsService @Bean method.
         *
         * Spring Security can then use it during authentication to load
         * users and their authorities from the database.
         */
        return manager;
    }

}
