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

import kw.kng.dto.HrFamilyDto;
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
		        return "home";
		    }

		    List<HrFamilyDto> hrFamilyDto_list = hs.getHrFamilyDto_List(militaryId);

		    if (hrFamilyDto_list == null || hrFamilyDto_list.isEmpty()) 
		    {
		        logger.warn("No HR Data Found for Military ID: {}", militaryId);
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
