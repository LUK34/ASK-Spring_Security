package kw.kng.security.medasApiSecurity.endpoint;

import java.util.List;

public interface KngMedasEndpointResolver 
{
	List<String> getCandidateBaseUrls();
    String getActiveBaseUrl();
    void markActive(String baseUrl);
    void invalidate(String baseUrl);
    void invalidate();

}
