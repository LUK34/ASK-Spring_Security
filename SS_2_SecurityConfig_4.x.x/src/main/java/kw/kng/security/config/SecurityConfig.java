package kw.kng.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
}
