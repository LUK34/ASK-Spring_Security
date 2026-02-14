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
	        // ---------------------------------------------------------------------------------------
	        									//For MVC
	        .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
	        .and()
	     // ---------------------------------------------------------------------------------------
	        								// Authentication Details
	        .authorizeRequests()
	            .antMatchers(
	                "/css/**",
	                "/js/**",
	                "/images/**",
	                "/kng/**",
	                "/error",
	                "/sso-failed.html"   // sso failed page
	            ).permitAll()
	            .anyRequest().authenticated()
	       // .and()

	        					//If SSO Authentication failed OR ACCESS DENIED ->  Redirect to `sso-failed.html` page
	       /* .exceptionHandling()
	            .authenticationEntryPoint((request, response, authException) -> 
	            {
	            	System.out.println("AUTHENTICATION FAILED -> calling -> sso-failed.html");
	                response.sendRedirect(request.getContextPath() + "/sso-failed.html");
	            })
	            .accessDeniedHandler((request, response, accessDeniedException) -> 
	            {
	            	System.out.println("ACCESS DENIED -> calling -> sso-failed.html");
	                response.sendRedirect(request.getContextPath() + "/sso-failed.html");
	            })*/
	        .and()
	        .addFilterBefore(jespaAuthFilter, 
	        		 UsernamePasswordAuthenticationFilter.class);
	 // ---------------------------------------------------------------------------------------
	    
	    
	    
	    return http.build();
	}
}


