package kw.kng.security.sso.hr.service;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import kw.kng.security.sso.hr.dto.SSoUserDto;
import kw.kng.security.sso.hr.dto.SsoDetailsDto;

public interface SsoService 
{
	SsoDetailsDto prepareSSOPageData(Principal principal, HttpServletRequest request);
	
	// ####################################################################################################################################################################
											/* For NUTRIO App -> START */
	List<SSoUserDto> get_NUTRIO_SSO_USER_details(Integer militaryId);
	void nutrio_sso_header_details(Integer mid, Model model);
	
	Long getLogged_SSO_MilitaryId();
	void sso_header_details(Model model);
											/* For NUTRIO App -> END */
	// ####################################################################################################################################################################
	
	// ####################################################################################################################################################################
												/* For general purpose used in ITEHS app -> START */
	List<Long> getSsoMidListAsLong();
	Long military_Id_Bypasser(Long militaryId);
	Long itehs_mid_use_me();
	
												/* For general purpose used in ITEHS app -> END */											
	// ####################################################################################################################################################################
	
}
