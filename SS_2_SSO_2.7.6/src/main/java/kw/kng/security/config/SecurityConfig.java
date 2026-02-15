package kw.kng.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig 
{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JespaAuthFilter jespaAuthFilter)
            throws Exception 
    {

        http
            .csrf().disable()

            // Session for MVC app
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()

            // Authorisation rules
            .authorizeRequests()
                .antMatchers(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/kng/**",
                        "/error",
                        "/sso-failed"
                ).permitAll()
                .anyRequest().authenticated()
            .and()

            // VERY IMPORTANT
            .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> 
                {
                    response.sendRedirect(request.getContextPath() + "/sso-failed");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> 
                {
                    response.sendRedirect(request.getContextPath() + "/sso-failed");
                })
            .and()

            // Disable default login mechanisms
            .formLogin().disable()
            .httpBasic().disable()

            // Add JESPA bridge filter
            .addFilterBefore(jespaAuthFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}