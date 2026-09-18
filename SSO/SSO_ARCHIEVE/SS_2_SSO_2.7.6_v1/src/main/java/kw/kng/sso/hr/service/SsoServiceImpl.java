package kw.kng.sso.hr.service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kw.kng.sso.hr.dto.HrFamilyDto;
import kw.kng.sso.hr.dto.SsoDetailsDto;

@Service
public class SsoServiceImpl implements SsoService 
{

    private static final Logger logger =
            LoggerFactory.getLogger(SsoServiceImpl.class);

    // =====================================================================================
    // MAIN METHOD
    // =====================================================================================
    @Override
    public SsoDetailsDto prepareSSOPageData(Principal principal,
                                             HttpServletRequest request) 
    {
        logger.info("========== SSO SERVICE -> prepareHomePageData -> START ==========");

        // -------------------------------------------------
        // 1. Extract Username from Principal
        // -------------------------------------------------
        String username = extractUsername(principal);

        // -------------------------------------------------
        // 2. Validate Session
        // -------------------------------------------------
        HttpSession session = request.getSession(false);

        if (session == null) 
        {
            logger.warn("No HTTP session found.");
            return new SsoDetailsDto(username, Collections.emptyList());
        }

        // -------------------------------------------------
        // 3. Get Military ID
        // -------------------------------------------------
        Long militaryId = extractMilitaryId(session);

        if (militaryId == null) 
        {
            logger.warn("Military ID not found in session.");
            return new SsoDetailsDto(username, Collections.emptyList());
        }

        // -------------------------------------------------
        // 4. Get HR Family List
        // -------------------------------------------------
        List<HrFamilyDto> hrFamilyDtoList =
                extractHrFamilyList(session);

        logger.info("========== SSO SERVICE -> prepareHomePageData -> END ==========");

        return new SsoDetailsDto(username, hrFamilyDtoList);
    }

    // =====================================================================================
    // HELPER METHODS
    // =====================================================================================

    private String extractUsername(Principal principal)
    {
        if (principal != null) 
        {
            logger.info("Authenticated User: {}", principal.getName());
            return principal.getName();
        }

        logger.warn("Principal is NULL. Using fallback username.");
        return "UNKNOWN USER";
    }

    private Long extractMilitaryId(HttpSession session)
    {
        Object militaryIdObj = session.getAttribute("militaryId");

        if (militaryIdObj instanceof Long) 
        {
            return (Long) militaryIdObj;
        }

        if (militaryIdObj instanceof String) 
        {
            try 
            {
                return Long.parseLong((String) militaryIdObj);
            }
            catch (NumberFormatException e) 
            {
                logger.error("Military ID in session is not a valid number.");
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private List<HrFamilyDto> extractHrFamilyList(HttpSession session)
    {
        Object hrObj = session.getAttribute("hrFamilyList");

        if (hrObj instanceof List<?>) 
        {
            return (List<HrFamilyDto>) hrObj;
        }

        logger.warn("HR Family List not found in session.");
        return Collections.emptyList();
    }
}