Epickur RESTful API
===================

Welcome to Epickur RESTful API.

For full endpoint documentation please go [here](../../apidoc/index.html).

###Prerequisites:
* Java SDK 7 http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html
* Tomcat8 http://tomcat.apache.org/download-80.cgi
* MongoDB http://www.mongodb.org
* Any IDE with Maven https://eclipse.org

###Install
Clone the Git repository in working directory:

`git clone ssh://carlphilipp@git.code.sf.net/p/epickurapi/code epickur-api`

Add Tomcat8 in Eclipse as a server.

###Configure
Two files need to be duplicated and renamed:

`src/main/resources/epickur.template.properties`

to

`src/main/resources/epickur.properties`


This file contains all the properties of the application. Some are linked with your environment like:

```
	name      = Epickur
	address	  = http://localhost:8380
	folder    = /epickur/api
	admins    = cp.harmant@gmail.com
```

Some of the properties need to be update to fit your environment.

There is also the same things for the test file:

`src/test/resources/test.template.properties`

to

`src/test/resources/test.properties`

###Test


####From Eclipse:

MongoDB and Tomcat8 must be started.

Run as JUnit test `com.epickur.AllTests.java`. It will run the unit testing and integration testing.

####From Maven:

MongoDB must be started.

Unit testing: `mvn test`

Integration testing: `mvn integration-test`


###Build
####From Maven:

Generate war: `mvn warify`

Generate Maven documentation: `mvn site`

Generate ApiDoc documentation, run `src/main/scripts/generate-api.bat` from Windows or `src/main/scripts/generate-api.sh` from Linux or OSX.


###Known issue with Eclipse
Issue with Maven dependencies not deployed

Bug in m2Clipse

###Credits

[@cpharmant](https://twitter.com/cpharmant)
