package kw.kng.security.sso.hr.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService 
{

	@Override
	public String getClientIp(HttpServletRequest request) 
	{
		 String xfHeader = request.getHeader("X-Forwarded-For");
		 
		 System.out.println("xfheader -> "+xfHeader);
		 
		 
	      if (xfHeader != null && xfHeader.length() > 0) 
	      {
	            return xfHeader.split(",")[0];
	      }

	      return request.getRemoteAddr();
	}
	
	

}
