package kw.kng.prerequisites.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = "kng.prerequisites",
        name = {
                "startup.enabled",
                "operating-system.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class OperatingSystemChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(OperatingSystemChecker.class);

    @PostConstruct
    public void checkOperatingSystem() {

        logger.info("#############################################################################################");
        logger.info("----------------------------- OPERATING SYSTEM CHECKER --------------------------------------");
        logger.info("#############################################################################################");

        logger.info(
                ">>> OS Name          : {}",
                System.getProperty("os.name"));

        logger.info(
                ">>> OS Version       : {}",
                System.getProperty("os.version"));

        logger.info(
                ">>> OS Architecture  : {}",
                System.getProperty("os.arch"));

        logger.info(
                ">>> User Name        : {}",
                System.getProperty("user.name"));

        logger.info(
                ">>> User Directory   : {}",
                System.getProperty("user.dir"));

        logger.info(
                ">>> User Home        : {}",
                System.getProperty("user.home"));

        logger.info("#############################################################################################");
    }
}