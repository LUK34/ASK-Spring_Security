package kw.kng.security.sso.hr.service;

import javax.servlet.http.HttpServletRequest;

public interface ClientService 
{
	String getClientIp(HttpServletRequest request);

}
