package kw.kng.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ----------------------------------------------------
            // Disable CSRF (required for REST + Basic Auth)
            // ----------------------------------------------------
            .csrf(csrf -> csrf.disable())

            // ----------------------------------------------------
            // Make session STATELESS (NO JSESSIONID)
            // ----------------------------------------------------
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
         // ----------------------------------------------------
            
            // ----------------------------------------------------
            // Authorization rules
            // ----------------------------------------------------
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/kng/**",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // ----------------------------------------------------
            // Enable BASIC authentication
            // ----------------------------------------------------
            .httpBasic(httpBasic -> {});

        return http.build();
    }
    
    // ----------------------------------------------------
    // In-Memory User
    // ----------------------------------------------------
    @Bean
    public UserDetailsService userDetailsService() 
    {

    	UserDetails user1= User.withUsername("user")
                .password("{noop}user_world") 
                .roles("USER")
                .build();
    	
    	UserDetails admin= User.withUsername("admin")
                .password("{noop}admin_world") 
                .roles("ADMIN")
                .build();
    	
        return new InMemoryUserDetailsManager(user1,admin);
    }
    
    
}
/*
 1. {noop}
 1.1 Here the password will be saved as a plain text.

 */