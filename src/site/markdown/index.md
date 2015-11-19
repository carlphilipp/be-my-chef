Epickur RESTful API
===================

Welcome to Epickur RESTful API.

For full endpoint documentation please go [here](epickur-rest/apidoc/index.html).

### Multi module project

Dependency is designed that way:

```
Rest -> Service -> DAO -> Utils -> Entity -> Logging
	 \                
	  `-> 3rd Party 
	   `-> Database dump
```

The test module contains classes for test purpose and is used here and there. 
When adding new module, take care of cyclic dependency error: Two modules must not depends on each other.

###Prerequisites:
* Java SDK 8 http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
* Tomcat8 https://tomcat.apache.org/download-80.cgi
* MongoDB 3.0 http://www.mongodb.org
* Any IDE with Maven https://eclipse.org

###Install
Clone the Git repository in working directory:

`git clone ssh://carlphilipp@git.code.sf.net/p/epickurapi/code epickur-api`

Add Tomcat8 in Eclipse as a server.

###Configure
Two files need to be duplicated and renamed:

`epickur-utils/src/main/resources/env/local.template.properties`

to

`epickur-utils/src/main/resources/env/local.properties`


This file contains all the properties of the application. Some are linked with your environment like:

```
  address               = http://localhost:8180
  folder                = /epickur/api
  mongo.address         = localhost
  mongo.port            = 27017
```

All those properties need to be updated to fit your environment.

There is also the same things for the test file:

`epickur-rest/src/test/resources/test.template.properties`

to

`epickur-rest/test/resources/test.properties`

Another notable file:

`epickur-rest/src/main/resources/epickur.properties`

This file contains all the application properties. Maven will inject the value of your local.properties into this fiel. The properties of that file should not be modified.

Lambock is used in the project. Please reefer to [lambock web site](https://projectlombok.org) to make it work in your IDE.

###Maven profiles
* local: The default one that should be used in local
* aws: The Amazon Web Service profil, used to deploy documentation and .war file on the production server

###Test


####From Eclipse:

MongoDB and Tomcat8 must be started.

~~Run as JUnit test `com.epickur.AllTests.java`. It will run the unit testing and integration testing.~~

####From Maven:

MongoDB must be started.

Unit testing: `mvn test`

Integration testing: `mvn integration-test`


###Build
####From Maven:

Generate war with Maven: `mvn package`. The generated jar will be in their respective project target directory. The final war in `epickur-rest/target`.

Generate documentation with Maven in local: `mvn site` and then `mvn site:stage` to aggregate all the website in one. Find the result in the parent project `target/stage`.

Generate documentation with Maven and push it to AWS: `mvn site-deploy` or `mvn site:deploy` to just push it.

Generate ApiDoc documentation, run `epickur-rest/src/main/scripts/generate-api.bat` from Windows or `epickur-rest/src/main/scripts/generate-api.sh` from Linux or OSX.

###Amazon Web Services

To deploy on AWS:

`mvn clean package "antrun:run@upload" -P aws`

The ant plugin run several commands:

* Stop tomcat
* Clean webbapps directory
* Clean other temp directory
* Push ROOT.war (war generatered) to $TOMCAT/webapps
* Start tomcat

To be able to deploy on AWS server, need to add to `~home/.m2/settings.xml`

```
<profiles>
    <profile>
      <id>aws</id>
      <properties>
        <server.address>ADDRESS</server.address>
        <server.login>LOGIN_SSH</server.login>
        <server.password>PASSWORD_SSH</server.password>
        <server.base>TOMCAT_BASE</server.base>
      </properties>
    </profile>
</profiles>
```

###Known issue with Eclipse
Issue with Maven dependencies not deployed

Bug in m2Clipse

###Known issue with Jersey
Some errors are not properly routed like #API-22.
See ticket in Jersey Jira: https://java.net/jira/browse/JERSEY-2722
It's not a big deal, the developer needs to pass a correctly formed request anyway.

###Credits

[@cpharmant](https://twitter.com/cpharmant)
