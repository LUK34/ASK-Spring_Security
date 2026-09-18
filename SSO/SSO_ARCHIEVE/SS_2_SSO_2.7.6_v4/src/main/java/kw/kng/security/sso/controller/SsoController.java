package kw.kng.security.sso.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kw.kng.security.sso.hr.dto.HrFamilyDto;
import kw.kng.security.sso.hr.service.HrService;
import kw.kng.security.sso.hr.service.SessionService;
import kw.kng.security.sso.hr.service.SsoService;
import kw.kng.security.sso.hr.service.SsoUserResolveService;

@Controller
public class SsoController 
{
	// ---------------------------------------------------------------------------------------------------
	private static final Logger logger =  LoggerFactory.getLogger(SsoController.class);
	
	private HrService hs;
	private SsoService ss;
	private SsoUserResolveService sus;
	private SessionService ses;
	public SsoController(HrService hs,
								SsoService ss,
							   SsoUserResolveService sus,
							   SessionService ses)
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
	    	/*
	    	ses.printSessionAttributes(session);
	    	Long militaryId = (Long) session.getAttribute("militaryId");
	        //Long militaryId = ss.getLogged_SSO_MilitaryId();

	        @SuppressWarnings("unchecked")
	        List<HrFamilyDto> list =  (List<HrFamilyDto>) session.getAttribute("hrFamilyList");

	        model.addAttribute("username", militaryId);
	        model.addAttribute("hrFamilyList", list);
	        */
	    	
	    	ss.sso_header_details(model);
	    	
	    	
	    }
	    ssoFlags("sso_success",model);
	    
	    return "sso";
	}
	
		
	@GetMapping("/sso-failed")
	public String saySsoFailed(Model model) 
	{
		ssoFlags("sso_failed",model);
		return "sso";
	}

	@GetMapping("/sso-hr-failed")
	public String saySsoHrFailed(Model model) 
	{
		ssoFlags("sso_hr_failed",model);
		return "sso";
	}
	
	@GetMapping("/sso-expired")
	public String saySsoExpired(Model model) 
	{
		ssoFlags("sso_expired",model);
		return "sso";
	}
	
	@GetMapping("/sso-denied")
	public String saySsoDenied(Model model) 
	{
		ssoFlags("sso_denied",model);
		return "sso";
	}
	
	@GetMapping("/home1")
	public String home1() 
	{
		return "home1";
	}

	
	public void ssoFlags(String code_value, Model model)
	{
		String flag = (code_value != null) ? code_value : "";

        logger.info("SSO FLAG SET -> {}", flag);
        model.addAttribute("flag", flag);
	}
	
	
	

}

