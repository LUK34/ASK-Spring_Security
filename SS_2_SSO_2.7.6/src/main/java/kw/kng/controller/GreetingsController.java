package kw.kng.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kw.kng.hr.dto.HrFamilyDto;
import kw.kng.hr.service.HrService;

@Controller
public class GreetingsController 
{
	// ---------------------------------------------------------------------------------------------------
	private static final Logger logger =  LoggerFactory.getLogger(GreetingsController.class);
	
	private HrService hs;
	public GreetingsController(HrService hs)
	{
		this.hs=hs;
	}
	// ---------------------------------------------------------------------------------------------------
	
	@GetMapping("/")
	public String rootRedirect() 
	{
	    return "redirect:/hello";
	}
	
	
	@GetMapping("/hello")
	public String sayHello(Model model,
	                       Principal principal,
	                       HttpServletRequest request) 
	{

	    if (principal != null) 
	    {
	        model.addAttribute("username", principal.getName());
	    }
	    else 
	    {
	        model.addAttribute("username", "UNKNOWN USER");
	    }

	    Long militaryId = (Long) request.getSession().getAttribute("militaryId");

	    if (militaryId == null) 
	    {
	        logger.warn("Military ID not found in session.");
	        model.addAttribute("hrFamilyList", Collections.emptyList());
	     //   return "home";
	    }

	    @SuppressWarnings("unchecked")
	    List<HrFamilyDto> hrFamilyDto_list = (List<HrFamilyDto>) request.getSession().getAttribute("hrFamilyList");

	    if (hrFamilyDto_list == null) 
	    {
	        logger.warn("HR data not found in session.");
	        hrFamilyDto_list = Collections.emptyList();
	    }

	    model.addAttribute("hrFamilyList", hrFamilyDto_list);

	    return "home";
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
