package kw.kng.prerequisites.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnProperty(
        prefix = "kng.prerequisites",
        name = {
                "startup.enabled",
                "profile.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class ProfileChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(ProfileChecker.class);

    private final Environment environment;

    public ProfileChecker(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void checkProfile() {

        logger.info("#############################################################################################");
        logger.info("--------------------------------- PROFILE CHECKER -------------------------------------------");
        logger.info("#############################################################################################");

        logger.info(
                ">>> Active Profiles : {}",
                Arrays.toString(
                        environment.getActiveProfiles()));

        logger.info("#############################################################################################");
    }
}