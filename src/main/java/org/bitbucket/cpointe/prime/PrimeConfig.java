package org.bitbucket.cpointe.prime;

import org.aeonbits.owner.KrauseningConfig;
import org.aeonbits.owner.KrauseningConfig.KrauseningSources;

@KrauseningSources("prime.properties")
public interface PrimeConfig extends KrauseningConfig {

    @Key("active")
    @DefaultValue("true")
    Boolean isActive();
    
    @Key("baseline.on.migrate")
    @DefaultValue("true")
    Boolean shouldBaselineOnMigrate();
    
    @Key("baseline.version")
    @DefaultValue("0.0.0")
    String getBaselineVersion();
    
    @Key("migration.retry.wait.time")
    @DefaultValue("20000")
    Integer getMigrationRetryWaitTime();
    
    @Key("schema")
    String getSchema();
    
    @Key("url")
    String getUrl();
    
    @Key("username")
    String getUsername();
    
    @Key("password")
    String getPassword();
    
    @Key("migration.locations")
    @DefaultValue("classpath:migrations")
    String getMigrationLocations();
    
    @Key("placeholder.prefix")
    @DefaultValue("^{")
    String getPlaceholderPrefix();
    
    @Key("placeholder.suffix")
    @DefaultValue("}")
    String getPlaceholderSuffix();
    
}
