# Prime - Deploy-Time Database Migrations #

[![Maven Central](https://img.shields.io/maven-central/v/org.technologybrewery.prime/prime.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.technologybrewery.prime%22%20AND%20a%3A%prime%22)
[![License](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/mit)

In brewing, you prime nearly finished beer with a little extra sugar (or similar fermentable
product) just before bottling to help the beer carbonate. In Java, Prime adds a little
structure or manipulates data in your database just before starting up your application.
Prime leverages Flyway to deliver "sugar" to your database - we just do a little bit to
package it up to run when your war is loaded.

# Prime in One Pint (Executing migrations when one WAR is using Prime)

Prime is easy to setup - it just takes a couple of simple steps:

1.) Update your web.xml file with the following ServletContextListener:

```XML
    <listener>
        <listener-class>org.technologybrewery.prime.PrimeContextListener</listener-class>
    </listener>
```

2.) Ensure that you have [Krausening](https://github.com/technologybrewery/krausening) configured, and specify properties in
the Krausening-managed `prime.properties`:

The following property is required:

```Java Properties
url=myJdbcUrl
```

These following common properties are optional (defaults listed):

```Java Properties
username=
password=
schema=dbo
schema.table=schema_version
```

For an entire list of what is configurable what default values are used, please
see [PrimeConfig](https://github.com/TechnologyBrewery/prime/blob/dev/src/main/java/org/technologybrewery/prime/PrimeConfig.java)
for the version you are leveraging.

3.) Make sure you have at least one Flyway migration defined in `src/main/resources/db/migrations`
or `src/main/resources/migrations/common`

4.) You're done. When you next deploy your WAR, Prime will run your migrations. order your next pint!

# Prime in Two Pints (Executing migrations when *at least* one WAR is using Prime)

It's common to have multiple deployment units deployed to a single application server. In this scenario, you'll want the
ability to differentiate `prime.properties` between deployment utils. The instructions are almost the same - you just
need a bit more sugar. There are two options for this:

## Option 1 - multiple prime.properties ##

*This option works well when you need entirely different properties for each WAR deployment.*

1.) Add an extra configuration parameter to each web.xml file in order to specify which properties file will contain
your configurations (rather than the standard `prime.properties`).  NOTE: by extending PrimeConfig, you can change
the properties file name to point to something other than prime.properties, effectively working around the need for 
different versions of prime.properties for different war files deployed to the same server:

```XML
	<context-param>
		<param-name>prime.config.extensions.class.name</param-name>
		<param-value>org.technologybrewery.example.WarAPrimeConfig.class</param-value>
	</context-param>
```

2.) Ensure that you have [Krausening](https://github.com/technologybrewery/krausening) configured, minimally specifying the
following values in the Krausening-managed `prime.properties`:

## Option 2 - specify schema in web.xml ##

*This option works well when you just need to have a different schema for each WAR deployment.*

1.) Add an extra configuration parameter to each web.xml file in order to specify the schema that prime should use for
migrations.

```XML
	<context-param>
		<param-name>prime.schema</param-name>
		<param-value>this_deployments_schema</param-value>
	</context-param>
```

# Prime Advanced Configuration #

You can leverage Flyway placeholders by adding any property to your `prime.properties` file that begins
with `placeholder.`. By default, you will get a placeholder property called `placeholder.schema` when you set a schema
in your `prime.properties`.

For a full list of configuration options, please see the interface and javadoc
for [PrimeConfig](https://github.com/TechnologyBrewery/prime/blob/dev/src/main/java/org/technologybrewery/prime/PrimeConfig.java).

# Distribution Channel

Want Prime in your project? The following Maven dependency will add it to your Maven project from the Maven Central
Repository:

```XML
<dependency>
    <groupId>org.technologybrewery.prime</groupId>
    <artifactId>prime</artifactId>
    <version>1.3.0</version>
</dependency>
```

## Releasing to Maven Central Repository

Krausening uses both the `maven-release-plugin` and the `nexus-staging-maven-plugin` to facilitate the release and
deployment of new Prime builds. In order to perform a release, you must:

1.) Obtain a [JIRA](https://issues.sonatype.org/secure/Dashboard.jspa) account with Sonatype OSSRH and access to
the `org.technologybrewery` project group

2.) Ensure that your Sonatype OSSRH JIRA account credentials are specified in your `settings.xml`:

```XML
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>your-jira-id</username>
      <password>your-jira-pwd</password>
    </server>
  </servers>
</settings>
```

3.) Install `gpg` and distribute your key pair -
see [here](http://central.sonatype.org/pages/working-with-pgp-signatures.html). OS X users may need to execute:

```Shell
export GPG_TTY=`tty`;
```

4.) Execute `mvn release:clean release:prepare`, answer the prompts for the versions and tags, and
perform `mvn release:perform`

## Licensing

Prime is available under the [MIT License](http://opensource.org/licenses/mit-license.php).