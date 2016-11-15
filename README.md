Be My Chef RESTful API
==================

Welcome to Be My Chef RESTful API!!

[https://www.bemychef.com.au](https://www.bemychef.com.au).

### Multi module project

ASCII dependency graph:

```
                   Logging
                      |
                      |
                   Commons
                      |
                      |
                    Entity       Config
                      |            |
                      |____________|
                      |
                    Utils
           ___________|____________
          |           |            |
      3rd Party      Dump         DAO
          |___________|____________|
                      |            |
                   Service     Validation
                      |            |
                      |____________|
                      |
                     Rest
```

When adding a new module, be careful with cyclic dependency error: Two modules must not depends on each other.

* Logging: Contains all the logging dependencies and configuration used everywhere in the project.
* Commons: Contains basic functionality that do not need any knowledge of main entity objects. Example: String or Date manipulation.
* Test: Contains test utilities for Stripe payment. Might be merged in the future with 3rd party.
* Entity: Contains all basic entity objects needed in the application. No logic there, just entities and Serializer/Deserializer classes.
* Utils: Contains reports, security, access rights and emails features.
* DAO: Access Mongo DB.
* 3rd Party: Access Amazon, Here and Stripe.
* Dump: Contains all the classes needed to create a MongoDB dump.
* Service: Contains all the logic to prepare data before storing them.
* Rest: Contains all the endpoint that the client use to retrieve data.
* Validation: Validate data using AspectJ.

###Prerequisites:
* Java SDK 8 http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
* MongoDB 3.0 http://www.mongodb.org
* Any IDE

###Install
Clone the Git repository in working directory:

`git clone https://github.com/carlphilipp/be-my-chef.git`

###Configure

## Project files
Two files need to be duplicated and renamed:

`config/src/main/resources/epickur-dev.template.properties`

to

`config/src/main/resources/epickur-dev.properties`


This file contains all the properties of the application. Some are linked with your environment like:

```
     epickur.name     = BE MY CHEF
     epickur.protocol = http
     epickur.host     = localhost
     epickur.port     = 8180
     epickur.path     = epickur
```

Some properties need to be updated to fit your environment.

####Lombok

Lombok is used in the project. Please reefer to [lombok web site](https://projectlombok.org) to make it work in your IDE.

###Maven profiles
* local: The default one that should be used in local
* aws: The Amazon Web Service profile, used to deploy documentation and archive on the production server

###Test

MongoDB must be started for integration tests.

Unit testing: `./mvnw test`

Integration testing: `./mvnw integration-test`

###Build
####From Maven:

Generate war with Maven:

`./mvnw clean package [-DskipTests] [-Dpmd.skip=true]`

The generated jar will be in their respective project target directory. The final war in `epickur-rest/target`.

Generate documentation with Maven in local: `./mvnw clean package install site` and then `./mvnw site:stage` to aggregate all the website in one. Find the result in the parent project `target/stage`.

Generate documentation with Maven and push it to AWS: `./mvnw site-deploy` or `./mvnw site:deploy` to just push it.

Generate ApiDoc documentation, run `epickur-rest/src/main/scripts/generate-api.bat` from Windows or `epickur-rest/src/main/scripts/generate-api.sh` from Linux or OSX.

###Run

To run the server in local, just start `com.epickur.api.Application.java` from your ide.
You can also run it from the console: `java -Dspring.profiles.active=dev -jar rest/target/epickur-version.jar`

###Deploy and run on Amazon Web Services

To deploy on AWS, start by packaging the app:

`./mvnw clean package -P aws`

Copy file on server:

`scp rest/target/epickur-version.jar user@ip:~/api`

Kill current instance running:

`ssh user@ip pkill java`

Start new instance:

`ssh user@ip 'nohup java -Dspring.profiles.active=prod -jar ~/api/epickur-version.jar &'`

Connect to the server and clean the old .jar if can.

###Credits

[@cpharmant](https://twitter.com/cpharmant)
