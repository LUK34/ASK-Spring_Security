package kw.kng.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
        	// ----------------------------------------------------
        		// Disable CSRF (required for stateless REST APIs)
        	// ----------------------------------------------------
            .csrf().disable()
            // ----------------------------------------------------
            
            
            // ----------------------------------------------------
            	// Make session STATELESS (NO JSESSIONID)
            // ----------------------------------------------------
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // ----------------------------------------------------
            
            // ----------------------------------------------------
            	// Authorization rules
            // ----------------------------------------------------
            .authorizeRequests()
                .antMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/kng/**",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            .and()
         // ----------------------------------------------------
            
         // ----------------------------------------------------
            // Enable HTTP Basic Authentication
         // ----------------------------------------------------  
            .httpBasic();
        // ----------------------------------------------------
        
        
        return http.build();
    }
}