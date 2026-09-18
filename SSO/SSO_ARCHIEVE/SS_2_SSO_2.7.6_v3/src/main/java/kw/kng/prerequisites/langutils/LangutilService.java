package kw.kng.prerequisites.langutils;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class LangutilService {
    private final Langutils langutils;
    private static final Logger log = LoggerFactory.getLogger(LangutilService.class);

    @Autowired
    public LangutilService(Langutils langutils) {
        this.langutils = langutils;
    }

    public void configureLanguage(HttpServletRequest request, Model model) 
    {
    	System.out.println("Request Value: "+request);
    	
        String lang = request.getParameter("lang");
        if (lang != null && !lang.isEmpty()) {
            // Directly use and set the 'lang' parameter if it's provided in the request
      
        	request.getSession().setAttribute("lang", lang);
            
        } else {
            // Retrieve from session if not provided in the current request
            lang = (String) request.getSession().getAttribute("lang");
            if (lang == null) {
                lang = "#"; // Default to English if not found
            	//lang = "ar";
            }
        }

        String urlWithArabic = langutils.createUrlWithLang(request, "ar");
        String urlWithEnglish = langutils.createUrlWithLang(request, "en");

        model.addAttribute("urlWithArabic", urlWithArabic);
        model.addAttribute("urlWithEnglish", urlWithEnglish);
        model.addAttribute("currentLang", lang); // Ensure 'currentLang' is updated based on the logic above

        log.info("Language set to: {}", lang);
        log.info("URL with Arabic: {}", urlWithArabic);
        log.info("URL with English: {}", urlWithEnglish);
    }
}
