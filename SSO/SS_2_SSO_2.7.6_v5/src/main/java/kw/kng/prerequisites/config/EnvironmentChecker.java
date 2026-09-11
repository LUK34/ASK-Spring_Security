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
                "environment.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class EnvironmentChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(EnvironmentChecker.class);

    @PostConstruct
    public void checkEnvironment() {

        logger.info("#############################################################################################");
        logger.info("------------------------------- ENVIRONMENT CHECKER -----------------------------------------");
        logger.info("#############################################################################################");

        logger.info(
                ">>> File Encoding    : {}",
                System.getProperty("file.encoding"));

        logger.info(
                ">>> File Separator   : {}",
                System.getProperty("file.separator"));

        logger.info(
                ">>> Path Separator   : {}",
                System.getProperty("path.separator"));

        logger.info(
                ">>> Line Separator   : {}",
                escapeLineSeparator(
                        System.getProperty("line.separator")));

        logger.info(
                ">>> Temp Directory   : {}",
                System.getProperty("java.io.tmpdir"));

        logger.info("#############################################################################################");
    }

    private String escapeLineSeparator(String value) {

        if ("\r\n".equals(value)) {
            return "\\r\\n (Windows)";
        }

        if ("\n".equals(value)) {
            return "\\n (Unix/Linux)";
        }

        if ("\r".equals(value)) {
            return "\\r";
        }

        return "UNKNOWN";
    }
}