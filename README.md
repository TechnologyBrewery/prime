# Prime - Deploy-Time Database Migrations #
[![Maven Central](https://img.shields.io/maven-central/v/org.bitbucket.cpointe.prime/prime.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.bitbucket.cpointe.prime%22%20AND%20a%3A%prime%22)
[![License](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/mit)

In brewing, you prime nearly finished beer with a little extra sugar (or similar fermentable product) just before bottling to help the beer carbonate.  In  Java, Prime adds a little structure or manipulates data in your database just before starting up your application.  Prime leverages Flyway to deliver "sugar" to your database - we just do a little bit to package it up to run when your war is loaded.

# Prime in One Pint (Executing migrations when one WAR is using Prime)#

Prime is easy to setup - it just takes a couple of simple steps:

1.) Update your web.xml file with the following ServletContextListener:

```
#!xml
	<listener>
        <listener-class>org.bitbucket.cpointe.prime.PrimeContextListener</listener-class>
    </listener>
```

2.) Ensure that you have [Krausening](https://bitbucket.org/cpointe/krausening) configured, and specify properties in the Krausening-managed `prime.properties`:

The following property is required:
```
#!bash
url=myJdbcUrl
```

These following common properties are optional (defaults listed):
```
#!bash
username=
password=
schema=dbo
schema.table=schema_version
```

For an entire list of what is configurable what default values are used, please see [PrimeConfig](https://bitbucket.org/cpointe/prime/src/a9bc4fe5e7c73857e2621e13b5e7073d06c2a27e/src/main/java/org/bitbucket/cpointe/prime/PrimeConfig.java?at=master&fileviewer=file-view-default) for the version you are leveraging.

3.) Make sure you have at least one Flyway migration defined in `src/main/resources/db/migrations` or `src/main/resources/migrations/common`

4.) You're done. When you next deploy your WAR, Prime will run your migrations. order your next pint!

# Prime in Two Pints (Executing migrations when *at least* one WAR is using Prime)#

It's common to have multiple deployment units deployed to a single application server. In this scenario, you'll want the ability to differentiate `prime.properties` between deployment utils. The instructions are almost the same - you just need a bit more sugar. There are two options for this:

## Option 1 - multiple prime.properties ##
*This option works well when you need entirely different properties for each WAR deployment.*

1.) Add an extra configuration parameter to each web.xml file in order to specify which properties file will contain your configurations (rather than the standard `prime.properties`):

```
#!xml
	<context-param>
		<param-name>prime.properties.file.name</param-name>
		<param-value>foo-prime.properties</param-value>
	</context-param>
```

2.) Ensure that you have [Krausening](https://bitbucket.org/cpointe/krausening) configured, minually specifying the following values in the Krausening-managed `prime.properties`:

## Option 2 - specify schema in web.xml ##
*This option works well when you just need to have a different schema for each WAR deployment.*

1.) Add an extra configuration parameter to each web.xml file in order to specify the schema that prime should use for migrations.

```
#!xml
	<context-param>
		<param-name>prime.schema</param-name>
		<param-value>this_deployments_schema</param-value>
	</context-param>
```


# Prime Advanced Configuration #

You can leverage Flyway placeholders by adding any property to your `prime.properties` file that begins with `placeholder.`. By default, you will get a placeholder property called `placeholder.schema` when you set a schema in your `prime.properties`.

For a full list of configuration options, please see the inteface and javadoc for [PrimeConfig](https://bitbucket.org/cpointe/prime/src/a9bc4fe5e7c73857e2621e13b5e7073d06c2a27e/src/main/java/org/bitbucket/cpointe/prime/PrimeConfig.java?at=master&fileviewer=file-view-default).

# Distribution Channel

Want Prime in your project? The following Maven dependency will add it to your Maven project from the Maven Central Repository:

```
#!xml
<dependency>
    <groupId>org.bitbucket.cpointe.prime</groupId>
    <artifactId>prime</artifactId>
    <version>1.1.3</version>
</dependency>
```

## Releasing to Maven Central Repository

Krausening uses both the `maven-release-plugin` and the `nexus-staging-maven-plugin` to facilitate the release and deployment of new Prime builds. In order to perform a release, you must:

1.) Obtain a [JIRA](https://issues.sonatype.org/secure/Dashboard.jspa) account with Sonatype OSSRH and access to the `org.bitbucket.cpointe` project group

2.) Ensure that your Sonatype OSSRH JIRA account credentials are specified in your `settings.xml`:

```
#!xml
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

3.) Install `gpg` and distribute your key pair - see [here](http://central.sonatype.org/pages/working-with-pgp-signatures.html). OS X users may need to execute:

```
#!bash
export GPG_TTY=`tty`;
```

4.) Execute `mvn release:clean release:prepare`, answer the prompts for the versions and tags, and perform `mvn release:perform`

## Licensing
Krausening is available under the [MIT License](http://opensource.org/licenses/mit-license.php).

## Session Beer
Prime would like to thank [Counterpointe Solutions](http://cpointe-inc.com/) for providing continuous integration and static code analysis services for Prime.
