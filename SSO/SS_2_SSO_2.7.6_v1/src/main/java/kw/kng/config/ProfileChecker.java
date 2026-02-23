package kw.kng.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileChecker 
{

    @Autowired
    private Environment env;

    @PostConstruct
    public void checkProfile() 
    {
    	System.out.println("#############################################################################################");
    	System.out.println("--------------------------------- PROFILE CHECKER -------------------------------------------");
    	System.out.println("#############################################################################################");
        System.out.println(">>> Active Profiles: " +Arrays.toString(env.getActiveProfiles()));
        System.out.println("#############################################################################################");
    }
}
/*
Knowledge Transfer:
----------------------------
1. Check which profile is active
2. Full compatible with 2.x.x
3. Mainly compatibele Java 1.8
 
*/



