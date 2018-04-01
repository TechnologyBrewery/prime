# Prime - WAR Deploy Time Database Migrations #
[![Maven Central](https://img.shields.io/maven-central/v/org.bitbucket.cpointe.prime/prime.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.bitbucket.cpointe.prime%22%20AND%20a%3A%prime%22)
[![License](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/mit)

In brewing, your prime your nearly finished beer with a little extra sugar (or similar fermentable product) just before bottling to help the beer have carbonation.  In  Java, Prime helps add a little structure or manipulate data in your database just before starting up your application.  Prime leverages Flyway to deliver "sugar" to your database - we just do a little bit to package it up to run when your war is loaded.

# Prime in One Pint#

To be completed - document how to configure the basics.

# Prime in Two Pints#

To be completed - document how to configure support for multiple wars.

# Distribution Channel

Want Prime in your project? The following Maven dependency will add it to your Maven project from the Maven Central Repository:

```
#!xml
<dependency>
    <groupId>org.bitbucket.cpointe.prime</groupId>
    <artifactId>prime</artifactId>
    <version>1.0.0</version>
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

3.) Install `gpg` and distribute your key pair - see [here](http://central.sonatype.org/pages/working-with-pgp-signatures.html).  OS X users may need to execute:

```
#!bash
export GPG_TTY=`tty`;
```

4.) Execute `mvn release:clean release:prepare`, answer the prompts for the versions and tags, and perform `mvn release:perform`

## Licensing
Krausening is available under the [MIT License](http://opensource.org/licenses/mit-license.php).

## Session Beer
Prime would like to thank [Counterpointe Solutions](http://cpointe-inc.com/) for providing continuous integration and static code analysis services for Prime.