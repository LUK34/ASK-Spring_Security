package kw.kng.security.sso.hr.service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import kw.kng.security.sso.hr.dto.HrFamilyDto;
import kw.kng.security.sso.hr.dto.SSoUserDto;
import kw.kng.security.sso.hr.dto.SsoDetailsDto;
import kw.kng.security.sso.hr.repo.HrRepo;

@Service
public class SsoServiceImpl implements SsoService 
{

    private static final Logger logger =   LoggerFactory.getLogger(SsoServiceImpl.class);
    
 // ############################################################################################################
    @Value("${app.name}")
    private String app_name;
    // ############################################################################################################
    
    // ############################################################################################################
    private HrRepo hr_jdbc_repo;
    private ModelMapper modelMapper;
    public SsoServiceImpl(HrRepo hr_jdbc_repo, ModelMapper modelMapper)
    {
    	this.hr_jdbc_repo=hr_jdbc_repo;
    	this.modelMapper=modelMapper;
    	
    }
    // ############################################################################################################
    

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

 // ####################################################################################################################################################################
    													/* GENERAL SSO FOR KNG  -> START*/
	@Override
	public Long getLogged_SSO_MilitaryId() 
	{
		logger.info("================== SERVICE -> getLoggedInMilitaryId -> START ==================");

		    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		    if (auth == null) 
		    {
		    	logger.info("❌ Authentication is NULL");
		        return null;
		    }

		    if (!auth.isAuthenticated()) 
		    {
		    	logger.info("❌ User is NOT authenticated");
		        return null;
		    }

		    Object principal = auth.getPrincipal();

		    if (principal == null) 
		    {
		    	logger.info("❌ Principal is NULL");
		        return null;
		    }

		    try 
		    {
		        Long mid = Long.parseLong(principal.toString());
		        logger.info("✅ MID resolved from SecurityContext: " + mid);

		        logger.info("================== SERVICE -> getLoggedInMilitaryId -> END ==================");
		        return mid;
		    } 
		    catch (NumberFormatException e) 
		    {
		    	logger.warn("❌ Failed to parse MID from principal: " + principal);
		        return null;
		    }
	}
	
	@Override
	public void sso_header_details(Model model)
	{
		logger.info("###############################################################################################################################");
		logger.info("SERVICE LAYER -> sso_header_details -> START ");
		Long militaryId;
		Integer militaryId_nutriv;
		List<HrFamilyDto> hrFamilyDto_list; 
		
		if (app_name.toLowerCase().contains("nutriv"))  // Specific to NUTRIO Schema ONLY -> nutriv
    	{
    		logger.info(" -------------------- > SERVICE LAYER -> Specific to NUTRIO Schema");
    		logger.info("App Name: {}", app_name);
    		militaryId = getLogged_SSO_MilitaryId();
    		militaryId_nutriv = militaryId.intValue();
    		 List<SSoUserDto> result= get_NUTRIO_SSO_USER_details(militaryId_nutriv);
    		List<HrFamilyDto> hrFamilyDto_list_nutriv = result.stream().map(n -> modelMapper.map(n, HrFamilyDto.class)).collect(Collectors.toList());
    		
    		model.addAttribute("username", militaryId);
    	    model.addAttribute("hrFamilyList", hrFamilyDto_list_nutriv);
    	}
		else 
		{
			logger.info(" -------------------- > SERVICE LAYER -> For General Puprose -> ECLINIC SCHEMA");
			logger.info("App Name: {}", app_name);
			militaryId = getLogged_SSO_MilitaryId();
			hrFamilyDto_list = hr_jdbc_repo.getHrFamilyDto_List(militaryId);
			
			model.addAttribute("username", militaryId);
    	    model.addAttribute("hrFamilyList", hrFamilyDto_list);
		}
		logger.info("SERVICE LAYER -> sso_header_details -> END ");
		logger.info("###############################################################################################################################");	
	}
	
	
															/* GENERAL SSO FOR KNG  -> END*/
	 // ####################################################################################################################################################################
	
	
	// ####################################################################################################################################################################
															/* NUTRIO -> START */
	@Override
	public List<SSoUserDto> get_NUTRIO_SSO_USER_details(Integer militaryId) 
	{
		System.out.println("================== SERVICE LAYER -> get_hr_family_KngOfficer_Details_List -> START ================== ");
		System.out.println("Military Id: " + militaryId);
		
		List<SSoUserDto> hrFamilyDto_list= hr_jdbc_repo.get_NUTRIO_SSO_USER_details(militaryId);
		hrFamilyDto_list.forEach(m ->
			System.out.println("Officers Family Details" +
				"Primary Id: " +m.getPrimaryId()+
				"Civil Id: "+m.getCivilId()+
				"Military Id: "+m.getMilitaryId()+
				"English Name: "+m.getNameEn()+
				"Arabic Name: "+m.getNameAr()+
				"Designation: "+m.getDesignation()+
				"Nationality: "+m.getNationality()+
				"Job Status: "+m.getJobStatus()+
				"Relation: "+m.getRelation()
				)		
		);
		System.out.println("================== SERVICE LAYER -> get_hr_family_KngOfficer_Details_List -> END ================== ");
		return hrFamilyDto_list;
	}



	@Override
	public void nutrio_sso_header_details(Integer mid, Model model) 
	{
		if (mid == null) 
	    {
		  Long midLong;
	        System.out.println("CASE 1 -> Initial load without military ID");
	        //mid=default_mid;   
	        midLong=getLogged_SSO_MilitaryId();
	        mid = (midLong != null) ? midLong.intValue() : 0;
	        
	        System.out.println("---------- SESSION ---------- | -> Military Id: "+mid);
	    }
	  List<SSoUserDto> hrFamilyDto_list = get_NUTRIO_SSO_USER_details(mid);
	    hrFamilyDto_list.forEach(m ->
	        System.out.println("Officer Family Details: " +
	            "Primary Id: " + m.getPrimaryId() +
	            " | Civil Id: " + m.getCivilId() +
	            " | Military Id: " + m.getMilitaryId() +
	            " | Name (EN): " + m.getNameEn() +
	            " | Name (AR): " + m.getNameAr() +
	            " | Designation: " + m.getDesignation() +
	            " | Nationality: " + m.getNationality() +
	            " | Job Status: " + m.getJobStatus() +
	            " | Relation: " + m.getRelation()
	        )
	    );
	    model.addAttribute("hrFamilyDto_list", hrFamilyDto_list);
		
	}
														/* NUTRIO -> END */
	// ####################################################################################################################################################################
	
	
}