package kw.kng.security.sso.security.config;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import kw.kng.security.sso.hr.dto.HrFamilyDto;
import kw.kng.security.sso.hr.service.HrService;


public class JespaAuthFilter extends OncePerRequestFilter 
{

    private static final Logger logger = LoggerFactory.getLogger(JespaAuthFilter.class);
    private static final String NTLM_PRINCIPAL_RETRY_FLAG = "NTLM_PRINCIPAL_RETRY_DONE";

    // ---------------------------------------------------------------------------------------------
    @Value("${sso.mid}")
    private Long ssoMid;
    
    @Value("${sso.mid.list}")
    private String ssoMidList;
    
    @Value("${app.name}")
    private String app_name;
    // ---------------------------------------------------------------------------------------------
    
    
    private final HrService hs;
    private final SsoProps ssoProps;

    public JespaAuthFilter(HrService hs,
    					   SsoProps ssoProps) 
    {
        this.hs = hs;
        this.ssoProps=ssoProps;
    }
    
    
 // ##############################################################################################################################################################
    public List<Long> getSsoMidListAsLong() 
    {
        System.out.println("Raw Property Value: " + ssoMidList);

        if (ssoMidList == null || ssoMidList.trim().isEmpty()) 
        {
            return Collections.emptyList();
        }

        List<Long> midList = Arrays.stream(ssoMidList.split(","))
                .map(String::trim)              // remove spaces
                .filter(s -> !s.isEmpty())     // ignore empty values
                .map(Long::parseLong)          // convert to Long
                .collect(Collectors.toList());

        System.out.println("Converted MID List: " + midList);

        return midList;
    }
    
    public Long military_Id_Bypasser(Long militaryId)
    {
    	List<Long> mids = getSsoMidListAsLong();
    	System.out.println("Exception Militray Id List -> "+mids);
    	Long use_me_mid=militaryId;
    	
    	if (militaryId != null && mids.contains(militaryId)) 
    	{
    		
    	    logger.info("Military ID {} found in configured list. Overriding with property file value", militaryId);
    	    use_me_mid = ssoMid;
    	    logger.info("------- By Pass -> by using use_me_mid ------- -> "+use_me_mid);
    	} 
    	else 
    	{
    	    logger.info("Military ID {} NOT in configured list. Using original value", militaryId);
    	    use_me_mid=militaryId;
    	}
    	return use_me_mid;
    }
    
    
    
   // ##############################################################################################################################################################
    
    

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
                || path.startsWith("/admin/")
                || path.startsWith("/kng/")
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
                //List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            	List<GrantedAuthority> authorities = Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority("ROLE_USER"));
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
        Long effectiveMilitaryId = military_Id_Bypasser(militaryId);
        
        if (militaryId == null) 
        {
            logger.error("Could not extract Military ID from {}", extractedUsername);
            SecurityContextHolder.clearContext();
            response.sendRedirect(request.getContextPath() + "/sso-failed");
            return;   
        }

        // Now create session only for valid identity
        HttpSession session = request.getSession(true);
        session.setAttribute("militaryId", effectiveMilitaryId);

        if (session.getAttribute("hrFamilyList") == null)
        {
            try 
            {
            	//Long use_me_mid=military_Id_Bypasser(militaryId);
            	
            	List<HrFamilyDto> familyList;
            	
            	if (app_name.toLowerCase().contains("nutriv"))  // Specific to NUTRIO Schema ONLY -> nutriv
            	{
            		logger.info(" -------------------- > JESPA -> APP_NAME: ", app_name," specific to NUTRIO schema ONLY");
            		familyList = hs.nutrio_getHrFamilyDto_List(effectiveMilitaryId);	 
            	}
                
            	else// Specific to ECLINIC Schema ONLY -> mepi, patmrd,sss
            	{
            		logger.info(" -------------------- > JESPA -> APP_NAME: ", app_name," specific to ECLINIC schema ONLY");
            		familyList = hs.getHrFamilyDto_List(effectiveMilitaryId);
            	}
                
            	logger.info("Data using Military id is as follows: {}",familyList);
                if (familyList == null || familyList.isEmpty())
                {
                    logger.error("No HR data found for Military ID: {}", effectiveMilitaryId);

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
                		effectiveMilitaryId.toString(), //Principal
                        null,		//Credential
                        authorities //Roles
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.info("Spring Security Context Updated -> ROLE_USER from IN MEMORY assigned");

        logger.info("Spring Security Context set. principal={}, roles={}", effectiveMilitaryId, authorities);
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




