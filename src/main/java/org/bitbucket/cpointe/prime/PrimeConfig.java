package org.bitbucket.cpointe.prime;

import java.util.List;

import org.aeonbits.owner.KrauseningConfig;
import org.aeonbits.owner.KrauseningConfig.KrauseningSources;

/**
 * Configuration values for Prime.
 */
@KrauseningSources("prime.properties")
public interface PrimeConfig extends KrauseningConfig {

    /**
     * Whether or not prime is active.
     *
     * @return active or not
     */
    @Key("active")
    @DefaultValue("true")
    Boolean isActive();

    /**
     * Whether to have Flyway baseline on migration if it has not already been done.
     *
     * @return baseline or not
     */
    @Key("baseline.on.migrate")
    @DefaultValue("true")
    Boolean shouldBaselineOnMigrate();

    /**
     * The baseline version to use, if baselining.
     *
     * @return version
     */
    @Key("baseline.version")
    @DefaultValue("0.0.0")
    String getBaselineVersion();

    /**
     * How long to wait before retrying the database on failure.
     *
     * @return retry delay in milliseconds
     */
    @Key("migration.retry.wait.time")
    @DefaultValue("20000")
    Integer getMigrationRetryWaitTime();

    /**
     * The database schema to use.
     *
     * @return schema
     */
    @Key("schema")
    String getSchema();

    /**
     * The JDBC url to use.
     *
     * @return JDBC url
     */
    @Key("url")
    String getUrl();

    /**
     * Database username.
     *
     * @return username
     */
    @Key("username")
    String getUsername();

    /**
     * Database password.
     *
     * @return password
     */
    @Key("password")
    String getPassword();

    /**
     * A comma separated list of Flyway-style migration locations.  For more information, please see the following url:
     * https://flywaydb.org/documentation/commandline/migrate#locations
     *
     * @return migration locations
     */
    @Key("migration.locations")
    @DefaultValue("classpath:migrations/common")
    List<String> getMigrationLocations();

    /**
     * A folder location of where to find migrations that are specific to the current database type.
     * @return migration location
     */
    @Key("migration.locations.database-type")
    @DefaultValue("classpath:migrations/mssql")
    String getMigrationLocationDatabaseType();

    /**
     * Prefix for replacing strings within SQL scripts.
     *
     * @return prefix
     */
    @Key("placeholder.prefix")
    @DefaultValue("^{")
    String getPlaceholderPrefix();

    /**
     * Suffix for replacing strings within SQL scripts.
     *
     * @return suffix
     */
    @Key("placeholder.suffix")
    @DefaultValue("}")
    String getPlaceholderSuffix();

    /**
     * When set, will customize the table in which Flyway stores history information.
     * @return name of the custom schema
     */
    @Key("schema.table")
    String getTable();

}
