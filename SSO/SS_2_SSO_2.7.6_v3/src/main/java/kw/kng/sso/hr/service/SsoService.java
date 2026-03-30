package kw.kng.sso.hr.service;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import kw.kng.sso.hr.dto.SsoDetailsDto;

public interface SsoService 
{
	SsoDetailsDto prepareSSOPageData(Principal principal, HttpServletRequest request);
	
	Long getLogged_SSO_MilitaryId();
}
