package kw.kng.sso.hr.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kw.kng.sso.security.config.SsoProps;

@Service
public class SsoUserResolveServiceImpl implements SsoUserResolveService 
{

	private static final Logger logger =  LoggerFactory.getLogger(SsoUserResolveServiceImpl.class);
	  private final SsoProps ssoProps;

	  public SsoUserResolveServiceImpl(SsoProps ssoProps) 
	  {
	        this.ssoProps = ssoProps;
	  }
	
	@Override
	public Long resolveMilitaryId(HttpServletRequest request) 
	{
	     logger.info("SSO TYPE : " + ssoProps.getSsoType());

	     	// -------------------------------------------------------------------------------------
	        // DEV / TEST → bypass SSO
	        if (ssoProps.isDevOrTest()) 
	        {
	        	logger.info("SSO DISABLED → Using default MID : " + ssoProps.getSsoMid());
	            return ssoProps.getSsoMid();
	        }
	        // -------------------------------------------------------------------------------------
	     
	     // -------------------------------------------------------------------------------------
	        // PROD → use SSO principal
	        if (request.getUserPrincipal() == null) 
	        {
	            throw new RuntimeException("SSO principal not found in PROD mode");
	        }
	     
	        String principal = request.getUserPrincipal().getName();
	        logger.info("SSO PRINCIPAL : " + principal);
	        String midStr = principal.split("@")[0];
	     // -------------------------------------------------------------------------------------
	        return Long.parseLong(midStr);
	}
	

}
