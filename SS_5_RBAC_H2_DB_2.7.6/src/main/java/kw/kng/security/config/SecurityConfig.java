package kw.kng.security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableGlobalMethodSecurity(prePostEnabled = true) // RBAC -> for Java 1.8 and 2.x.x ONLY
@Configuration
public class SecurityConfig 
{
	@Autowired
	DataSource datasource;

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

     /*
   	 		If we want to store the password as plain text we use {noop} along with the actual password
   	 */

   	UserDetails user1= User.withUsername("user")
               .password(passwordEncoder().encode("user_world")) //"{noop}user_world"
               .roles("USER")
               .build();
   	
   	UserDetails admin= User.withUsername("admin")
   			.password(passwordEncoder().encode("admin_world")) //"{noop}admin_world"
               .roles("ADMIN")
               .build();
    	
    	// -----------------------------------------------------------------------------------
    	/*
    	 	Database Authentication -> Replace In-memory 
    	 */
    	JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(datasource);
    	userDetailsManager.createUser(user1);
    	userDetailsManager.createUser(admin);
    	return userDetailsManager;
    	// -----------------------------------------------------------------------------------
       
    	
    	
    	// return new InMemoryUserDetailsManager(user1,admin);
    }
        
    
    //To encrypt the password and save into DB we use passwwordEncoder
    @Bean
    public PasswordEncoder passwordEncoder()
    {
    	return new BCryptPasswordEncoder();
    }
    
}



/*

1. {noop}
1.1 Here the password will be saved as a plain text.

*/