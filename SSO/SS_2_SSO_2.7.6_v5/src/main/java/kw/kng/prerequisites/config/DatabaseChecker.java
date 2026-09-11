package kw.kng.prerequisites.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = "kng.prerequisites",
        name = {
                "startup.enabled",
                "database.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class DatabaseChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(DatabaseChecker.class);

    private final DataSource dataSource;

    public DatabaseChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void checkDatabase() {

        logger.info("#############################################################################################");
        logger.info("-------------------------------- DATABASE CHECKER -------------------------------------------");
        logger.info("#############################################################################################");

        try (Connection connection =
                     dataSource.getConnection()) {

            DatabaseMetaData metaData =
                    connection.getMetaData();

            logger.info(
                    ">>> Database Product : {}",
                    metaData.getDatabaseProductName());

            logger.info(
                    ">>> Database Version : {}",
                    metaData.getDatabaseProductVersion());

            logger.info(
                    ">>> JDBC Driver      : {}",
                    metaData.getDriverName());

            logger.info(
                    ">>> Driver Version   : {}",
                    metaData.getDriverVersion());

            logger.info(
                    ">>> Database Status  : CONNECTED");

        } catch (Exception e) {

            logger.error(
                    ">>> Database Status  : CONNECTION FAILED");

            logger.error(
                    ">>> Database connectivity check failed.",
                    e);
        }

        logger.info("#############################################################################################");
    }
}