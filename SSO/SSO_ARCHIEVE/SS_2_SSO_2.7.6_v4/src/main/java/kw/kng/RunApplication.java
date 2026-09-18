package kw.kng;

import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootApplication
public class RunApplication implements WebMvcConfigurer 
{
	
	// --------------------------------------------------------------------------------------------------------------------
    @Bean
    public ModelMapper modelMapper()
    {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
              .setSkipNullEnabled(false)  // include nulls instead of skipping
              .setFieldMatchingEnabled(true)
              .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return mapper;
    }
	
    
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> registration(HiddenHttpMethodFilter filter) {
        FilterRegistrationBean<HiddenHttpMethodFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(0); // runs before Spring Security
        return registration;
    }
    // --------------------------------------------------------------------------------------------------------------------
	

	public static void main(String[] args) {
		SpringApplication.run(RunApplication.class, args);
		
	}
	

	@Bean
	public SessionLocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(new Locale("en", "US"));
		return slr;
	}

	
	
	@Bean
	public MessageSource messageSource() {

		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("messages");
		source.setDefaultEncoding("UTF-8");
		return source;

	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
}
