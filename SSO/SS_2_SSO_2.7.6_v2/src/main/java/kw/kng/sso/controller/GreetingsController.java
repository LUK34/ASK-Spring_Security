package kw.kng.sso.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kw.kng.sso.hr.dto.HrFamilyDto;
import kw.kng.sso.hr.service.HrService;
import kw.kng.sso.hr.service.SessionService;
import kw.kng.sso.hr.service.SsoService;
import kw.kng.sso.hr.service.SsoUserResolveService;

@Controller
public class GreetingsController 
{
	// ---------------------------------------------------------------------------------------------------
	private static final Logger logger =  LoggerFactory.getLogger(GreetingsController.class);
	
	private HrService hs;
	private SsoService ss;
	private SsoUserResolveService sus;
	private SessionService ses;
	public GreetingsController(HrService hs,
								SsoService ss,
							   SsoUserResolveService sus,
							   SessionService sss)
	{
		this.hs=hs;
		this.ss=ss;
		this.sus=sus;
		this.ses=ses;
	}
	// ---------------------------------------------------------------------------------------------------
	
	@GetMapping("/")
	public String rootRedirect() 
	{
	    return "redirect:/sso-hello";
	}
	
	
	@GetMapping("/sso-hello")
	public String sayHello(HttpServletRequest request, Model model) 
	{
		
	    HttpSession session = request.getSession(false);

	    if (session != null) 
	    {
	    	ses.printSessionAttributes(session);
	        Long militaryId = (Long) session.getAttribute("militaryId");

	        @SuppressWarnings("unchecked")
	        List<HrFamilyDto> list =  (List<HrFamilyDto>) session.getAttribute("hrFamilyList");

	        model.addAttribute("username", militaryId);
	        model.addAttribute("hrFamilyList", list);
	    }
		// ----------------------------------------------------------------------------------
		/* 
	    Long militaryId = sus.resolveMilitaryId(request);
		 System.out.println("Resolved MID : " + militaryId);
		 List<HrFamilyDto> list = hs.getHrFamilyDto_List(militaryId);
		 model.addAttribute("hrFamilyList", list);
		 */
	    // ----------------------------------------------------------------------------------
	    
	    return "sso-home";
	}
	
	
	/*
	@GetMapping("/sso-hello")
	public String sayHello(Model model,
	                       Principal principal,
	                       HttpServletRequest request) 
	{
		// ----------------------------------------------------------------------------------
	    SsoDetailsDto ssoData= ss.prepareSSOPageData(principal, request);
	    model.addAttribute("username", ssoData.getUsername());
	    model.addAttribute("hrFamilyList", ssoData.getHrFamilyList());
	    // ----------------------------------------------------------------------------------
	    
	    return "sso-home";
	}
	*/
	
		
	@GetMapping("/sso-failed")
	public String saySsoFailed() 
	{
		return "sso-failed";
	}

	@GetMapping("/sso-hr-failed")
	public String saySsoHrFailed() 
	{
		return "sso-hr-failed";
	}
	
	@GetMapping("/sso-expired")
	public String saySsoExpired() 
	{
		return "sso-expired";
	}
	
	@GetMapping("/sso-denied")
	public String saySsoDenied() 
	{
		return "sso-denied";
	}
	
	@GetMapping("/home1")
	public String home1() 
	{
		return "home1";
	}

	
	

}

/*



 
 
 
 
 

 */
