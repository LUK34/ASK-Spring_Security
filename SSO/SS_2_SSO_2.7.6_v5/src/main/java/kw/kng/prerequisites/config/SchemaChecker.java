package kw.kng.prerequisites.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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
                "schema.enabled"
        },
        havingValue = "true",
        matchIfMissing = false
)
public class SchemaChecker {

    private static final Logger logger =
            LoggerFactory.getLogger(SchemaChecker.class);

    private final DataSource dataSource;

    public SchemaChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void checkSchema() {

        logger.info("#############################################################################################");
        logger.info("--------------------------------- SCHEMA CHECKER --------------------------------------------");
        logger.info("#############################################################################################");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            /*
             * Fetch currently connected Oracle user.
             */
            try (ResultSet rs =
                         statement.executeQuery(
                                 "SELECT USER FROM dual")) {

                if (rs.next()) {

                    logger.info(
                            ">>> Connected User   : {}",
                            rs.getString(1));
                }
            }

            /*
             * Fetch current Oracle schema.
             */
            try (ResultSet rs =
                         statement.executeQuery(
                                 "SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM dual")) {

                if (rs.next()) {

                    logger.info(
                            ">>> Connected Schema : {}",
                            rs.getString(1));
                }
            }

            logger.info(
                    ">>> Schema Check      : SUCCESS");

        } catch (Exception e) {

            logger.error(
                    ">>> Schema Check      : FAILED");

            logger.error(
                    ">>> Unable to determine Oracle user/schema.",
                    e);
        }

        logger.info("#############################################################################################");
    }
}