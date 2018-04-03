package org.bitbucket.cpointe.prime;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context listener that runs database migrations from Flyway during application startup.
 */
public class PrimeContextListener implements ServletContextListener {

    private static final String PRIME_PROPERTIES_FILE_NAME = "prime.properties.file.name";

    private static final Logger logger = LoggerFactory.getLogger(PrimeContextListener.class);

    protected PrimeConfig primeConfig;
    protected Flyway flyway;

    /**
     * {@inheritDoc}
     */
    public void contextDestroyed(ServletContextEvent event) {
        // nothing to do when the context is destroyed
    }

    /**
     * {@inheritDoc}
     */
    public void contextInitialized(ServletContextEvent event) {
        long start = System.currentTimeMillis();

        instantiatePrimeConfig(event);

        if (!primeConfig.isActive()) {
            logger.warn("Prime is currently set to inactive!");
        } else {
            logger.info("START: Prime execution...");
        }

        initFlyway();

        // if the application hasn't already been baselined then baseline
        flyway.setBaselineOnMigrate(primeConfig.shouldBaselineOnMigrate());
        flyway.setBaselineVersionAsString(primeConfig.getBaselineVersion());
        flyway.setBaselineDescription("Initial Flyway Baseline via Prime Execution");

        // migrate application (if there is anything to migrate)
        String locations = StringUtils.join(flyway.getLocations(), ", ");
        logger.info("Migrating application if necessary with Flyway in: {}", locations);

        // try 3 times (w/ 20 sec pauses) in case the database is taking a minute to startup
        boolean flywayRan = false;
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                flyway.migrate();
                flywayRan = true;
                break;
            } catch (Exception e) {
                logger.error("Failed to migrate flyway...", e);
                try {
                    Thread.sleep(primeConfig.getMigrationRetryWaitTime());
                } catch (InterruptedException ie) {
                    logger.error("Failed to sleep to wait for database - attempt " + (i + 1) + " of " + maxRetries + 1,
                            ie);
                }
            }
        }
        
        if (!flywayRan) {
            // run one last time outside try/catch so if the migration failed the startup fails
            flyway.migrate();
        }

        long stop = System.currentTimeMillis();
        logger.info("COMPLETE: Prime migrations executed in {}ms", (stop - start));
    }

    /**
     * Loads PrimeConfig after giving the user an opportunity to override the default file name that will be 
     * used to provide properties.  This allows multiple wars in the same application server to use different
     * sets of properties.
     * @param event context potentially containing an updated properties file name
     */
    private void instantiatePrimeConfig(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        String overloadedPropertiesName = servletContext.getInitParameter(PRIME_PROPERTIES_FILE_NAME);

        if (StringUtils.isNotBlank(overloadedPropertiesName)) {
            primeConfig = KrauseningConfigFactory.create(PrimeConfig.class, overloadedPropertiesName);
            logger.info("Overriding prime configuration to look at properties named {}", overloadedPropertiesName);

        } else {
            primeConfig = KrauseningConfigFactory.create(PrimeConfig.class);

        }
    }

    protected void initFlyway() {
        flyway = new Flyway();

        // Set schema
        String schema = primeConfig.getSchema();
        if (StringUtils.isNotBlank(schema)) {
            flyway.setSchemas(schema);
        }

        // Point it to the database
        String url = primeConfig.getUrl();
        String username = primeConfig.getUsername();
        String password = primeConfig.getPassword();
        flyway.setDataSource(url, username, password);

        // Point it to the sql migrations
        flyway.setLocations(primeConfig.getMigrationLocations());
        flyway.setPlaceholderPrefix(primeConfig.getPlaceholderPrefix());
        flyway.setPlaceholderSuffix(primeConfig.getPlaceholderSuffix());
    }
}