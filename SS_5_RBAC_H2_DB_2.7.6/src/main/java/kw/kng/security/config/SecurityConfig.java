package kw.kng.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableGlobalMethodSecurity(prePostEnabled = true) // RBAC -> for Java 1.8 and 2.x.x ONLY
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception 
    {

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
            // H2 Console   
            // ----------------------------------------------------   
               /*
                h2Console runs on frames.But spring security will block the frame.
                So we have to enable by writing the below lines of code.             
               */
               .headers(headers ->
                  // headers.frameOptions(frame -> frame.disable())
                   headers.frameOptions(frameOptions -> frameOptions.sameOrigin())
               )
            // ----------------------------------------------------
            
            
            
            // ----------------------------------------------------
            	// Authorization rules
            // ----------------------------------------------------
            .authorizeRequests()
            	.antMatchers("/h2-console/**").permitAll() //h2 access given here as well
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