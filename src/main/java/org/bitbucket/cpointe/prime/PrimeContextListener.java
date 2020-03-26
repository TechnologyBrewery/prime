package org.bitbucket.cpointe.prime;

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.krausening.Krausening;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context listener that runs database migrations from Flyway during application
 * startup.
 */
public class PrimeContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(PrimeContextListener.class);

    protected static final String PRIME_PROPERTIES_FILE_NAME = "prime.properties.file.name";
    protected static final String PRIME_SCHEMA = "prime.schema";
    protected static final String PLACEHOLDER_PREFIX = "placeholder.";
    protected static final String PLACEHOLDER_SCHEMA = PLACEHOLDER_PREFIX + "schema";

    protected PrimeConfig primeConfig;
    protected Flyway flyway;
    protected String propertiesFileName = "prime.properties";

    private String primeSchemaFromContext;

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

            initFlyway();

            runFlyway();

            long stop = System.currentTimeMillis();
            logger.info("COMPLETE: Prime migrations executed in {}ms", (stop - start));
        }
    }

    private void runFlyway() {
        // try 3 times (w/ 20 sec pauses) in case the database is taking a
        // minute to startup
        boolean flywayRan = false;
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                flyway.migrate();
                flywayRan = true;
                break;
            } catch (Exception e) {
                logger.warn("Failed to migrate flyway (attempt {} of {})", i + 1, maxRetries + 1);
                try {
                    Thread.sleep(primeConfig.getMigrationRetryWaitTime());
                } catch (InterruptedException ie) {
                    logger.error("Failed to sleep to wait for database (attempt {} of {})", i + 1, maxRetries + 1, ie);
                }
            }
        }

        if (!flywayRan) {
            // run one last time outside try/catch so if the migration
            // failed the startup fails
            flyway.migrate();
        }
    }

    /**
     * Loads PrimeConfig after giving the user an opportunity to override the
     * default file name that will be used to provide properties. This allows
     * multiple wars in the same application server to use different sets of
     * properties.
     *
     * @param event
     *            context potentially containing an updated properties file name
     */
    private void instantiatePrimeConfig(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        String overloadedPropertiesName = servletContext.getInitParameter(PRIME_PROPERTIES_FILE_NAME);

        primeSchemaFromContext = servletContext.getInitParameter(PRIME_SCHEMA);

        if (StringUtils.isNotBlank(overloadedPropertiesName)) {
            primeConfig = KrauseningConfigFactory.create(PrimeConfig.class, overloadedPropertiesName,
                    System.getProperties());
            propertiesFileName = overloadedPropertiesName;
            logger.info("Overriding prime configuration to look at properties named {}", overloadedPropertiesName);

        } else {
            primeConfig = KrauseningConfigFactory.create(PrimeConfig.class, System.getProperties());

        }
    }

    protected void initFlyway() {
        flyway = new Flyway();

        Map<String, String> placeholders = getPlaceholders();

        // Set schema from web.xml if supplied, otherwise set if from the
        // krausening config
        String schema;
        if (StringUtils.isBlank(primeSchemaFromContext)) {
            schema = primeConfig.getSchema();
        } else {
            schema = primeSchemaFromContext;
        }
        if (StringUtils.isNotBlank(schema)) {
            flyway.setSchemas(schema);
            placeholders.put(PLACEHOLDER_SCHEMA, schema);
        }

        // Point it to the database
        String url = primeConfig.getUrl();
        String username = primeConfig.getUsername();
        String password = primeConfig.getPassword();
        flyway.setDataSource(url, username, password);

        // Point it to the sql migrations
        List<String> allLocations = new ArrayList<String>();
        allLocations.addAll(Arrays.asList(flyway.getLocations()));
        allLocations.addAll(primeConfig.getMigrationLocations());
        allLocations.add(primeConfig.getMigrationLocationDatabaseType());

        flyway.setLocations(allLocations.toArray(new String[0]));
        flyway.setPlaceholderPrefix(primeConfig.getPlaceholderPrefix());
        flyway.setPlaceholderSuffix(primeConfig.getPlaceholderSuffix());
        flyway.setPlaceholders(placeholders);

        if (StringUtils.isNotBlank(primeConfig.getTable())) {
            flyway.setTable(primeConfig.getTable());
        }

        // if the application hasn't already been baselined then baseline
        flyway.setBaselineOnMigrate(primeConfig.shouldBaselineOnMigrate());
        flyway.setBaselineVersionAsString(primeConfig.getBaselineVersion());
        flyway.setBaselineDescription("Initial Flyway Baseline via Prime Execution");

        // migrate application (if there is anything to migrate)

        logger.info("Migrating application if necessary with Flyway in: {}", allLocations);
    }

    protected Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        Properties primeProperties = Krausening.getInstance().getProperties(propertiesFileName);

        for (Object key : primeProperties.keySet()) {
            String keyAsString = key.toString();
            if (keyAsString.startsWith(PLACEHOLDER_PREFIX)) {
                placeholders.put(keyAsString, primeProperties.getProperty(keyAsString));
            }
        }

        return placeholders;
    }

}
