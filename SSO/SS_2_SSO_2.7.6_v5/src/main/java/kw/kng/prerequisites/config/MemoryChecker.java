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
                "memory.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class MemoryChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(MemoryChecker.class);

    @PostConstruct
    public void checkMemory() {

        Runtime runtime =
                Runtime.getRuntime();

        long mb = 1024L * 1024L;

        long maxMemory =
                runtime.maxMemory() / mb;

        long totalMemory =
                runtime.totalMemory() / mb;

        long freeMemory =
                runtime.freeMemory() / mb;

        long usedMemory =
                totalMemory - freeMemory;

        logger.info("#############################################################################################");
        logger.info("---------------------------------- MEMORY CHECKER -------------------------------------------");
        logger.info("#############################################################################################");

        logger.info(
                ">>> Max JVM Memory   : {} MB",
                maxMemory);

        logger.info(
                ">>> Total Memory     : {} MB",
                totalMemory);

        logger.info(
                ">>> Used Memory      : {} MB",
                usedMemory);

        logger.info(
                ">>> Free Memory      : {} MB",
                freeMemory);

        logger.info(
                ">>> CPU Processors   : {}",
                runtime.availableProcessors());

        logger.info("#############################################################################################");
    }
}