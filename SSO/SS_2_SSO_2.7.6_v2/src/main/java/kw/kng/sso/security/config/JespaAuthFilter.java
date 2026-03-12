package kw.kng.sso.security.config;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import kw.kng.sso.hr.dto.HrFamilyDto;
import kw.kng.sso.hr.service.HrService;


public class JespaAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JespaAuthFilter.class);
    private static final String NTLM_PRINCIPAL_RETRY_FLAG = "NTLM_PRINCIPAL_RETRY_DONE";

    private final HrService hs;
    private final SsoProps ssoProps;

    public JespaAuthFilter(HrService hs,SsoProps ssoProps) 
    {
        this.hs = hs;
        this.ssoProps=ssoProps;
    }

    // ---------------------------------------------------------------------------------------------
    // Skip static resources
    // ---------------------------------------------------------------------------------------------
     @Override
    protected boolean shouldNotFilter(HttpServletRequest request) 
     {

        String path = request.getServletPath();

        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.equals("/error")
                || path.equals("/sso-failed")
                || path.equals("/sso-hr-failed")
                || path.equals("/sso-expired")
                || path.equals("/sso-denied")
                || path.equals("/favicon.ico");
     }

     // ---------------------------------------------------------------------------------------------
     // MAIN SSO BRIDGE FILTER
     // ---------------------------------------------------------------------------------------------
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException 
    {
    	
    	  // DEV / TEST MODE
        if (!ssoProps.isProd()) 
        {
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuth == null || !existingAuth.isAuthenticated()) 
            {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                UsernamePasswordAuthenticationToken authentication =   new UsernamePasswordAuthenticationToken(
                                ssoProps.getSsoMid().toString(),
                                null,
                                authorities
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
            return;
        }
    	

        logger.info("==== SPRING SSO FILTER ====");
        logger.info("URL: {}", request.getRequestURL());
        logger.info("DispatcherType: {}", request.getDispatcherType());
        logger.info("Principal: {}", request.getUserPrincipal());
        logger.info("RemoteUser: {}", request.getRemoteUser());
        logger.info("AuthType: {}", request.getAuthType());

        // IMPORTANT: Spring may set AnonymousAuthenticationToken (isAuthenticated() == true)
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        
        // Already authenticated → skip re-authentication
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
        String fullUsername=null;
        Principal principal = request.getUserPrincipal();
        if (principal != null) 
        {
            fullUsername = principal.getName();
        } 
        else if (request.getRemoteUser() != null) 
        {
            fullUsername = request.getRemoteUser();
        }

        if (fullUsername == null) 
        {
            boolean isNtlm = "NTLM".equalsIgnoreCase(request.getAuthType());
            Object retryDone = request.getSession().getAttribute(NTLM_PRINCIPAL_RETRY_FLAG);

            logger.warn("No Principal/RemoteUser yet. authType={}, uri={}", request.getAuthType(), request.getRequestURI());

            if (isNtlm && retryDone == null) 
            {
                request.getSession().setAttribute(NTLM_PRINCIPAL_RETRY_FLAG, Boolean.TRUE);

                String target = request.getRequestURI();
                String qs = request.getQueryString();
                if (qs != null && !qs.trim().isEmpty()) 
                {
                    target = target + "?" + qs;
                }

                logger.warn("NTLM detected but principal not bound yet. Redirecting once to: {}", target);
                response.sendRedirect(target);
                return;
            }

            // Already retried once and still no identity -> continue (or redirect to failure)
            // You can redirect to /sso-failed here if you prefer:
            // response.sendRedirect(request.getContextPath() + "/sso-failed"); return;

            filterChain.doFilter(request, response);
            return;
        }
        // Clear retry flag once identity is visible
        request.getSession().removeAttribute(NTLM_PRINCIPAL_RETRY_FLAG);
        logger.info("JESPA Identity resolved: {}", fullUsername);

        logger.info("=======================================================");
        logger.info("JESPA Identity: {}", fullUsername);
        logger.info("Principal: {}", request.getUserPrincipal());
        logger.info("RemoteUser: {}", request.getRemoteUser());
        logger.info("AuthType: {}", request.getAuthType());
        logger.info("=======================================================");
        
        logger.info("=======================================================");
        logger.info("Debug all Request Attributes:");
        java.util.Enumeration<String> attrs = request.getAttributeNames();
        while (attrs.hasMoreElements()) 
        {
        	String name = attrs.nextElement();
        	Object val = request.getAttribute(name);
        	logger.info("ATTR: {} = {}", name, val);
        }
        logger.info("=======================================================");
        
        String extractedUsername = extractMilitaryNumber(fullUsername);

     // STRICT VALIDATION
        if (!isPureNumeric(extractedUsername))
        {
            logger.error("Invalid AD username detected (non-numeric): {}", extractedUsername);
            SecurityContextHolder.clearContext();
            response.sendRedirect(request.getContextPath() + "/sso-failed");
            return;     
        }
        
        Long militaryId = extractMilitaryId(extractedUsername);
        
        if (militaryId == null) 
        {
            logger.error("Could not extract Military ID from {}", extractedUsername);
            SecurityContextHolder.clearContext();
            response.sendRedirect(request.getContextPath() + "/sso-failed");
            return;   
        }

        // Now create session only for valid identity
        HttpSession session = request.getSession(true);
        session.setAttribute("militaryId", militaryId);

        if (session.getAttribute("hrFamilyList") == null)
        {
            try 
            {
                List<HrFamilyDto> familyList = hs.getHrFamilyDto_List(militaryId);
                logger.info("Data using Military id is as follows: {}",familyList);
                if (familyList == null || familyList.isEmpty())
                {
                    logger.error("No HR data found for Military ID: {}", militaryId);

                    SecurityContextHolder.clearContext();
                    response.sendRedirect(request.getContextPath() + "/sso-hr-failed");
                    return;
                }

                session.setAttribute("hrFamilyList", familyList);
                logger.info("HR FAMILY DATA LOADED. Count={}", familyList.size());
            }
            catch (Exception e) 
            {
            	 logger.error("Error fetching HR data for militaryId={}", militaryId, e);

                 SecurityContextHolder.clearContext();
                 response.sendRedirect(request.getContextPath() + "/sso-failed");
                 return;
            }
        }
        

        // Create Spring Security Authentication Object from IN MEMORY
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Update Spring Security Context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                		militaryId.toString(), //Principal
                        null,		//Credential
                        authorities //Roles
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.info("Spring Security Context Updated -> ROLE_USER from IN MEMORY assigned");

        logger.info("Spring Security Context set. principal={}, roles={}", militaryId, authorities);
        logger.info("Principal={}, Authorities={}",
                authentication.getPrincipal(),
                authentication.getAuthorities());
        
        
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
    
	 // -----------------------------------------------------------------------
	 // Check if username is strictly numeric
	 // -----------------------------------------------------------------------
	 private boolean isPureNumeric(String username) 
	 {
	     if (username == null) return false;
	
	     return username.matches("^\\d+$");
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




