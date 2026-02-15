package kw.kng.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupLogger 
{

    @PostConstruct
    public void logStartup() 
    {
    	System.out.println("#############################################################################################");
    	System.out.println("--------------------------------- STARTUP LOGGER -------------------------------------------");
    	System.out.println("#############################################################################################");
        System.out.println(">>> Spring Boot Application Started");
        System.out.println(">>> Java Version : " + System.getProperty("java.version"));
        System.out.println("#############################################################################################");
    }
}
/*
Knowledge Transfer:
----------------------------
1. Check which Java version the springboot application is running
2. Full compatible with 2.x.x
3. Mainly compatibele Java 1.8
 
*/