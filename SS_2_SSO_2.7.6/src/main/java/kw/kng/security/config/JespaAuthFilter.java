package kw.kng.security.config;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jespa.http.HttpSecurityFilter;

@Component
public class JespaAuthFilter extends OncePerRequestFilter 
{

    private HttpSecurityFilter jespaFilter;
  
    private static final Logger logger =  LoggerFactory.getLogger(JespaAuthFilter.class);
    
    // -------------------------------------------------------------------------------------------------------------------
    
       
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("============ SPRING SSO FILTER ============");
        logger.info("URL: {}", request.getRequestURL());
        logger.info("Authorization Header: {}", request.getHeader("Authorization"));

        // If already authenticated → continue
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // IMPORTANT:
        // JESPA already ran BEFORE this filter.
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            logger.warn("Principal still NULL (NTLM not completed yet)");
            filterChain.doFilter(request, response);
            return;
        }

        String fullUsername = principal.getName();
        logger.info("NTLM SUCCESS USER: {}", fullUsername);

        String militaryNo = extractMilitaryNumber(fullUsername);
        logger.info("Extracted Military No: {}", militaryNo);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        militaryNo,
                        null,
                        Collections.emptyList()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.info("Spring Security Context Updated.");
        
        filterChain.doFilter(request, response);
    }


    // -------------------------------------------------------------------------------------------------------------------
    
    private String extractMilitaryNumber(String username) 
    {

        if (username == null)
        {
            return null;
        }
            
        if (username.contains("@")) 
        {
            return username.substring(0, username.indexOf("@"));
        }

        if (username.contains("\\")) 
        {
            return username.substring(username.indexOf("\\") + 1);
        }

        return username;
    }
    
    // -------------------------------------------------------------------------------------------------------------------
    
}
