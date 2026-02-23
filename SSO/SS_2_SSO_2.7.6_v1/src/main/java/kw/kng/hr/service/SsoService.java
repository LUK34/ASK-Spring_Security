package kw.kng.hr.service;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import kw.kng.hr.dto.SsoDetailsDto;

public interface SsoService 
{
	SsoDetailsDto prepareSSOPageData(Principal principal, HttpServletRequest request);
}
