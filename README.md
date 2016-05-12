SAML Authentication service
===========================

This service allows you to authenticate in yours flow using saml.

This service is under active development and is not stable.

#### Building

To build the service, you will need to have Apache Ant, Maven 3 and a Java 8, and  a redis database.

You will need to generate a configuration file for the service by running the provided `build.xml` script with Ant, and 
passing in a valid URL to a Redis instance:

```bash
$ ant -Dredis.url=redis.company.net
```

Now you can build the runnable shaded JAR:

```bash
$ mvn clean package
```

#### Running

The service is a Jersey JAX-RS application, that by default is run under the Grizzly2 server on port 8080 (if you use 
the packaged JAR).

##### Defaults

Running the following command will start the service listening on `0.0.0.0:8080/api/saml/1`:

```bash
$ java -jar target/saml-2.0-SNAPSHOT.jar
```

##### Custom Port

You can specify a custom port to run the service on by passing the `server.port` property when running the JAR. The
following command will start the service listening on port 9090 (`0.0.0.0:9090/api/saml/1`):

```bash
$ java -Dserver.port=9090 -jar target/saml-1.0-SNAPSHOT.jar
```

## Contributing

Contribution are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).

This code includes [onelogin code for saml](https://github.com/onelogin/java-saml.git). 