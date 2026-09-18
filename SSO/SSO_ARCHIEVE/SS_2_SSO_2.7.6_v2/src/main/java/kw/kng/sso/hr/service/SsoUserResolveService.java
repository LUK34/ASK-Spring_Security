package kw.kng.sso.hr.service;

import javax.servlet.http.HttpServletRequest;

public interface SsoUserResolveService 
{
	Long resolveMilitaryId(HttpServletRequest request);

}
