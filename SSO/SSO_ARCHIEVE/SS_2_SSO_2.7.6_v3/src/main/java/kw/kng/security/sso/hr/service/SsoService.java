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
	
	List<SSoUserDto> get_NUTRIO_SSO_USER_details(Integer militaryId);
	
	void nutrio_sso_header_details(Integer mid, Model model);
	
	Long getLogged_SSO_MilitaryId();
	
}
