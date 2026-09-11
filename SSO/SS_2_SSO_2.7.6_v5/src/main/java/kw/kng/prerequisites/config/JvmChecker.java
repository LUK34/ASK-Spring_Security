package kw.kng.prerequisites.config;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

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
                "jvm.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class JvmChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(JvmChecker.class);

    @PostConstruct
    public void checkJvm() {

        RuntimeMXBean runtime =
                ManagementFactory.getRuntimeMXBean();

        logger.info("#############################################################################################");
        logger.info("------------------------------------ JVM CHECKER --------------------------------------------");
        logger.info("#############################################################################################");

        logger.info(
                ">>> Java Version      : {}",
                System.getProperty("java.version"));

        logger.info(
                ">>> Java Vendor       : {}",
                System.getProperty("java.vendor"));

        logger.info(
                ">>> Java Home         : {}",
                System.getProperty("java.home"));

        logger.info(
                ">>> JVM Name          : {}",
                System.getProperty("java.vm.name"));

        logger.info(
                ">>> JVM Vendor        : {}",
                System.getProperty("java.vm.vendor"));

        logger.info(
                ">>> JVM Version       : {}",
                System.getProperty("java.vm.version"));

        logger.info(
                ">>> Runtime Name      : {}",
                runtime.getName());

        logger.info(
                ">>> JVM Uptime (ms)   : {}",
                runtime.getUptime());

        logger.info("#############################################################################################");
    }
}