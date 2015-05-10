Epickur RESTful API
===================

Welcome to Epickur RESTful API.

For full endpoint documentation please go [here](../../apidoc/index.html).

###Prerequisites:
* Java SDK 7 http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html
* Tomcat8 http://tomcat.apache.org/download-80.cgi
* MongoDB 3.0 http://www.mongodb.org
* Any IDE with Maven https://eclipse.org

###Install
Clone the Git repository in working directory:

`git clone ssh://carlphilipp@git.code.sf.net/p/epickurapi/code epickur-api`

Add Tomcat8 in Eclipse as a server.

###Configure
Two files need to be duplicated and renamed:

`src/main/resources/env/local.template.properties`

to

`src/main/resources/env/local.properties`


This file contains all the properties of the application. Some are linked with your environment like:

```
  address               = http://localhost:8180
  folder                = /epickur/api
  mongo.address         = localhost
  mongo.port            = 27017
```

All those properties need to be updated to fit your environment.

There is also the same things for the test file:

`src/test/resources/test.template.properties`

to

`src/test/resources/test.properties`

Another notable file:

`src/main/resources/epickur.properties`

This file contains all the application properties. Maven will inject the value of your local.properties into this fiel. The properties of that file should not be modified.

###Maven

Two profils are definied in pom.xml: 
* local: The default one that should be used in local. 
* heroku: Here to deploy in heroku.

###Test


####From Eclipse:

MongoDB and Tomcat8 must be started.

Run as JUnit test `com.epickur.AllTests.java`. It will run the unit testing and integration testing.

####From Maven:

MongoDB must be started.

Unit testing: `mvn test -P local`

Integration testing: `mvn integration-test -P local`


###Build
####From Maven:

Generate war with Maven: `mvn warify -P local`

Generate documentation with Maven: `mvn site -P local`

Generate ApiDoc documentation, run `src/main/scripts/generate-api.bat` from Windows or `src/main/scripts/generate-api.sh` from Linux or OSX.

###Heroku

To deploy on heroku:

`mvn heroku deploy-war -P heroku`

To resolve an issue, I had to configure heroku with:

`heroku config:set WEBAPP_RUNNER_OPTS="--expand-war` or in the pom.xml:

```
<plugin>
  <groupId>com.heroku.sdk</groupId>
  <artifactId>heroku-maven-plugin</artifactId>
  <version>0.3.7</version>
  <configuration>
    <appName>epickur-api</appName>
    <jdkVersion>1.7</jdkVersion>
    <configVars>
      <WEBAPP_RUNNER_OPTS>--expand-war</WEBAPP_RUNNER_OPTS>
    </configVars>
   </configuration>
</plugin>
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
