package kw.kng.security.medasApiSecurity.token;

public interface KngMedasTokenService 
{
	String getValidToken();
	String getAuthorizationHeaderValue();
	void invalidateToken();
	
}
