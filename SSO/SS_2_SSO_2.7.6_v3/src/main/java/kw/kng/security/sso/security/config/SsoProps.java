package kw.kng.security.sso.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;


@Component
@Data
public class SsoProps 
{
	
	@Value("${sso.type}")
    private String ssoType;

    @Value("${sso.mid}")
    private Long ssoMid;
    
    public boolean isProd() 
    {
    	 return ssoType != null && "prod".equalsIgnoreCase(ssoType.trim());
      //  return "prod".equalsIgnoreCase(ssoType);
    }

    public boolean isDevOrTest() 
    {
        return "dev".equalsIgnoreCase(ssoType.trim()) || "test".equalsIgnoreCase(ssoType.trim());
    }

}
