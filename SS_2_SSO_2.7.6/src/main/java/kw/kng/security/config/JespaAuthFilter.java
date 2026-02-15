package kw.kng.security.config;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import kw.kng.dto.HrFamilyDto;
import kw.kng.hr.service.HrService;

@Component
public class JespaAuthFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JespaAuthFilter.class);

    private final HrService hs;

    public JespaAuthFilter(HrService hs) {
        this.hs = hs;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.equals("/error")
                || path.equals("/sso-failed");
    }

    // ---------------------------------------------------------------------------------------------

    //MAIN SSO
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("==== SPRING SSO FILTER ====");
        logger.info("URL: {}", request.getRequestURL());

        // IMPORTANT: Spring may set AnonymousAuthenticationToken (isAuthenticated() == true)
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        
        // Already authenticated → skip
        if (existingAuth != null
            && existingAuth.isAuthenticated()
            && !(existingAuth instanceof AnonymousAuthenticationToken))
        {
        	logger.info("==== SPRINGBOOT SSO FILTER START ====");
        	logger.info("Already authenticated in Spring Security (non-anonymous). Skipping JespaAuthFilter.");

            filterChain.doFilter(request, response);
            return;
        }

        // JESPA principal
        Principal principal = request.getUserPrincipal();

        if (principal == null) 
        {
            logger.warn("Principal NULL - JESPA NTLM not completed yet");
            filterChain.doFilter(request, response);
            return;
        }

        String fullUsername = principal.getName();
        logger.info("JESPA Principal: {}", fullUsername);

        String extractedUsername = extractMilitaryNumber(fullUsername);
        Long militaryId = extractMilitaryId(extractedUsername);

        if (militaryId == null) 
        {
            logger.error("Could not extract Military ID from {}", fullUsername);
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("Military ID extracted: {}", militaryId);

        // Store militaryId in session
        request.getSession().setAttribute("militaryId", militaryId);

        // Call service ONLY ONCE per session
        if (request.getSession().getAttribute("hrFamilyList") == null) 
        {
        	
            try 
            {
                List<HrFamilyDto> familyList = hs.getHrFamilyDto_List(militaryId);
                request.getSession().setAttribute("hrFamilyList", familyList);
                logger.info("HR FAMILY DATA LOADED. Count={}", (familyList == null ? 0 : familyList.size()));
            }
            catch (Exception e) 
            {
                logger.error("Error fetching HR data for militaryId={}", militaryId, e);
            }
        }
        

        // Create Spring Security Authentication Object from IN MEMORY
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // Update Spring Security Context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        militaryId, //(Principal
                        null,		//Credential
                        authorities //Roles
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.info("Spring Security Context Updated -> ROLE_USER from IN MEMORY assigned");

        logger.info("Spring Security Context set. principal={}, roles={}", militaryId, authorities);
        
        logger.info("==== SPRING SSO FILTER END ====");
        
        filterChain.doFilter(request, response);
    }

    // ---------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------
    // Extract Military Username from NTLM format
    // -----------------------------------------------------------------------
    private String extractMilitaryNumber(String username) 
    {

        if (username == null) return null;

        // DOMAIN\959636
        if (username.contains("\\")) 
        {
            return username.substring(username.indexOf("\\") + 1);
        }

        // 959636@kng.kw
        if (username.contains("@")) 
        {
            return username.substring(0, username.indexOf("@"));
        }

        return username;
    }

    // ---------------------------------------------------------------------------------------------

    
    
    // -----------------------------------------------------------------------
    // Extract digits only (military ID)
    // -----------------------------------------------------------------------
    private Long extractMilitaryId(String username) 
    {

        if (username == null) return null;

        String digitsOnly = username.replaceAll("[^0-9]", "");

        if (digitsOnly.isEmpty()) return null;

        try 
        {
            return Long.parseLong(digitsOnly);
        }
        catch (NumberFormatException e) 
        {
            logger.error("Invalid military number: {}", digitsOnly);
            return null;
        }
    }

}


/*
 START
   ↓
Is SecurityContext already authenticated?
   ↓
Yes → Skip everything → Continue chain
No  → Continue
   ↓
Is principal NULL?
   ↓
Yes → NTLM not completed → Continue chain
No  → Continue
   ↓
Extract username
   ↓
Extract military ID
   ↓
If militaryId NULL → Continue
   ↓
Store militaryId in session
   ↓
Call HR Service (only once per session)
   ↓
Store hrFamilyList in session
   ↓
Create Spring Authentication object
   ↓
Set SecurityContext
   ↓
Continue filter chain
END

 */




