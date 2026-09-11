package kw.kng.prerequisites.config;

import java.util.Locale;
import java.util.TimeZone;

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
                "timezone.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class TimeZoneChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(TimeZoneChecker.class);

    @PostConstruct
    public void checkTimeZone() {

        TimeZone timeZone =
                TimeZone.getDefault();

        Locale locale =
                Locale.getDefault();

        logger.info("#############################################################################################");
        logger.info("-------------------------------- TIMEZONE CHECKER -------------------------------------------");
        logger.info("#############################################################################################");

        logger.info(
                ">>> Time Zone ID     : {}",
                timeZone.getID());

        logger.info(
                ">>> Time Zone Name   : {}",
                timeZone.getDisplayName());

        logger.info(
                ">>> Locale           : {}",
                locale.toString());

        logger.info(
                ">>> Language         : {}",
                locale.getLanguage());

        logger.info(
                ">>> Country          : {}",
                locale.getCountry());

        logger.info("#############################################################################################");
    }
}