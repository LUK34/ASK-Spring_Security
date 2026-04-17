package kw.kng.security.sso.hr.service;

import javax.servlet.http.HttpSession;

public interface SessionService 
{
	void printSessionAttributes(HttpSession session);

}
