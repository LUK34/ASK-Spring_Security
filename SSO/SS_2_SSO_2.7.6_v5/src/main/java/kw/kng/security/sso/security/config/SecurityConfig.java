package kw.kng.security.sso.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import kw.kng.security.sso.hr.service.HrService;

@Configuration
public class SecurityConfig 
{

	@Bean
	public JespaAuthFilter jespaAuthFilter(HrService hs,SsoProps ssoProps)
	{
		return new JespaAuthFilter(hs,ssoProps);
	}
	
    @Bean
    public FilterRegistrationBean<JespaAuthFilter> jespaAuthFilterRegistration(JespaAuthFilter filter) 
    {
        FilterRegistrationBean<JespaAuthFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false); // ✅ prevents Tomcat auto filter registration
        return reg;
    }
	
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
            	"/ora_func/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/admin/**",
                "/kng/**",
                "/error",
                "/sso-failed",
                "/sso-hr-failed",
                "/sso-expired",
                "/sso-denied"
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

            .addFilterBefore(jespaAuthFilter, SecurityContextHolderAwareRequestFilter.class);
        
        return http.build();
    }
}