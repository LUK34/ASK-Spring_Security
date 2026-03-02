package kw.kng.sso.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kw.kng.sso.hr.dto.SsoDetailsDto;
import kw.kng.sso.hr.service.HrService;
import kw.kng.sso.hr.service.SsoService;

@Controller
public class GreetingsController 
{
	// ---------------------------------------------------------------------------------------------------
	private static final Logger logger =  LoggerFactory.getLogger(GreetingsController.class);
	
	private HrService hs;
	private SsoService ss;
	public GreetingsController(HrService hs,SsoService ss)
	{
		this.hs=hs;
		this.ss=ss;
	}
	// ---------------------------------------------------------------------------------------------------
	
	@GetMapping("/")
	public String rootRedirect() 
	{
	    return "redirect:/sso-hello";
	}
	
	
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
		
	@GetMapping("/sso-failed")
	public String saySsoFailed() 
	{
		return "sso-failed";
	}
	
	@GetMapping("/home1")
	public String home1() 
	{
		return "home1";
	}

	
	

}

/*



 
 
 
 
 

 */
