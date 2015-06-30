# common-ebean

This is a module to provide Ebean supports, and load the properties from System.properties instead of ebean.properties.

The latest version V3.x has been re-implemented based on Ebean ORM version 4.7.1, which is because Ebean V4.5+ has been deprecated GlobalProperties class.
If you have encounter any issues with V3.x, please raise the issue ticket, and restrict the version range as [2.2,3).

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nz.ac.auckland.groupapps.common/common-ebean/badge.svg)](https://maven-badges.herokuapp.com/maven-central/nz.ac.auckland.groupapps.common/common-ebean)

## Documentation
How to use Ebean is covered in the PDF documentation for Ebean:

    http://www.avaje.org/doc/ebean-userguide.pdf

## Spring
Ebean is surfaced in Spring using the nz.ac.auckland.common:common-ebean artifact. 

This will bring in the EbeanServer interface in your Spring Context. It requires a few properties in the System Properties:

[source]
    dataSource.username=db-username
    dataSource.password=db-password
    dataSource.url=jdbc:.....

These three lines must never be in your checked in project, they are stored in your home directory.

Implied unless otherwise specified are:
[source]
    dataSource.databaseDriver=oracle.jdbc.driver.OracleDriver
    dataSource.minConnections=1
    dataSource.maxConnections=25
    dataSource.heartbeatSql=select count(*) from dual
    dataSource.isolationLevel=read_committed

Also, class _SystemPropertyWrapper_ that extends from _PropertiesWrapper_ will provide an additional feature, which is not only try to return a value from System Properties, also try to find and return a value from all registered ebean.properties.

At the same time, _SystemPropertyWrapper_ will scan all properties by a combined key that is using both "dataSource" and "ebean".

For example, the "SystemPropertyWrapper" will return the correct value for both keys "username" and "password" based on following settings.
[source]
dataSource.username=db-username
ebean.password=db-password

**The property with "dataSource" as prefix will be treated as priority.**

## Loading the agent
As Ebean, like all modern JPA frameworks, uses instrumentation, you can either specify the Ebean agent on your java run path:

[source,bash]
java -javaagent:/path/to/ebean.jar ….

or you can simply include nz.ac.auckland.common:common-agent-loader and use:

[source,java]
AgentLoader.loadAgent(“avaje-ebeanorm”)

And it will load - you could do this in an @BeforeClass before a test loads (however, see common-testrunner’s README.md for @RunWith JUnit Runner’s that set all this up for you) or in your main method when running an application. 

If you wish your Web application to load any agent (including ebean), then a system property of

[source]
webapp.agents=avaje-ebeanorm (further agents can be appended by commas)

will cause the Web container to automatically load Instrumentation.

## Ebean logging

Ebean uses SLF4j and requires you as part of the configuration to turn the ability to log in. In your /ebean.properties file, make sure
you add the line:

[source]
ebean.debug.sql = true

Then you can control whether Ebean actually logs, and what it logs using "named" logback logging configuration:

[source,xml]
<logger name="org.avaje.ebean.SQL" level="TRACE"/>
<logger name="org.avaje.ebean.TXN" level="TRACE"/>
<logger name="org.avaje.ebean.SUM" level="TRACE"/>

## Auto-scanning for entity classes
SystemPropertyWrapper will automatically scan all libraries that contains "domain", and register them into the ClasspathSearch,
so that the Ebean can auto-scan them for entity classes.

However, you can setup "ebean.search.jars" in your /ebean.properties to override this behaviour.

[source]
ebean.search.jars=jar-partial-name1, jar-partial-name2

e.g.

[source]
    ebean.search.jars=pcf-domain

or

[source]
ebean.search.jars=fat-domain,organization-domain



