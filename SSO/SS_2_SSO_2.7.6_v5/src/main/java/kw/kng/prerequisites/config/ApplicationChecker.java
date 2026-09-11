package kw.kng.prerequisites.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnProperty(
        prefix = "kng.prerequisites",
        name = {
                "startup.enabled",
                "application.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class ApplicationChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(ApplicationChecker.class);

    private final Environment environment;

    @Value("${spring.application.name:UNKNOWN}")
    private String applicationName;

    public ApplicationChecker(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void checkApplication() {

        logger.info("#############################################################################################");
        logger.info("-------------------------------- APPLICATION CHECKER ----------------------------------------");
        logger.info("#############################################################################################");

        logger.info(
                ">>> Application Name : {}",
                applicationName);

        String[] profiles =
                environment.getActiveProfiles();

        if (profiles != null && profiles.length > 0) {

            logger.info(
                    ">>> Active Profiles  : {}",
                    Arrays.toString(profiles));

        } else {

            logger.info(
                    ">>> Active Profiles  : DEFAULT");
        }

        logger.info("#############################################################################################");
    }
}