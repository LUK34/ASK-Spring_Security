package kw.kng.prerequisites.langutils;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;


@Component 
public class Langutils {

	
	public String createUrlWithLang(HttpServletRequest request, String lang) {
	    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
	    
	    // Add all current parameters, excluding 'lang'
	    request.getParameterMap().forEach((key, values) -> {
	        if (!"lang".equals(key)) {
	            for (String value : values) {
	                uriBuilder.queryParam(key, value);
	            }
	        }
	    });
	    
	    // Add or replace the 'lang' parameter
	    uriBuilder.queryParam("lang", lang);
	    
	    return uriBuilder.build().encode().toString(); // Ensure encoding in case of special characters
	}
	
	
}
