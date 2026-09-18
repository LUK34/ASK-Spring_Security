package kw.kng.security.sso.hr.service;

import javax.servlet.http.HttpServletRequest;

public interface SsoUserResolveService 
{
	Long resolveMilitaryId(HttpServletRequest request);

}
