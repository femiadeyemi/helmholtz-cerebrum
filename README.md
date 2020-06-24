# Helmholtz Marketplace Cerebrum

Cerebrum is the resources API for Helmholtz marketplace.

## Building and Running

In order to build and run Helmholtz Marketplace Cerebrum you will need
* OpenJDK 11
* Apache Maven 3.6

To start you will need to clone the project and then execute
```
mvn clean install
```

in order to run the project, build it and execute the Jar file from the command line:
```
java -jar target/helmholtz-cerebrum-<version number>-SNAPSHOT.jar
```

Since this is an API-only application, you will need to access the API endpoints in order to see something from the functionality. A startingpoint is

[http://localhost:8090/api/v0](http://localhost:8090/api/v0)

which should show you the existing endpoints.
