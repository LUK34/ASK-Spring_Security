package kw.kng.security.config;

import java.net.URL;
import java.util.EnumSet;

import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import jespa.http.HttpSecurityFilter;

@Configuration
public class JespaConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(JespaConfig.class);

    // -----------------------------------------------------------------------------------------

    @PostConstruct
    public void loadJespaConfig() throws Exception {

        URL configUrl = getClass()
                .getClassLoader()
                .getResource("sso.prp");

        if (configUrl == null) {
            throw new RuntimeException("❌ sso.prp NOT FOUND in classpath!");
        }

        logger.info("✅ JESPA CONFIG LOADED FROM: {}", configUrl.getPath());

        System.setProperty("jespa.config", configUrl.getPath());
    }

    // -----------------------------------------------------------------------------------------

    @Bean
    public FilterRegistrationBean<HttpSecurityFilter> jespaServletFilter() {

        FilterRegistrationBean<HttpSecurityFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(new HttpSecurityFilter());

        // Intercept all application requests
        registration.addUrlPatterns("/*");

        registration.setName("JespaFilter");

        // VERY IMPORTANT → Prevents /error recursion
        registration.setDispatcherTypes(
                EnumSet.of(DispatcherType.REQUEST)
        );

        // Run before everything
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registration;
    }

    // -----------------------------------------------------------------------------------------
}