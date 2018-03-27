# launch-moquette-via-spring-boot

Moquette in this project is based on a [moquette](https://github.com/andsel/moquette) version of 0.10. With the maven build project, you can start moquette directly through spring boot.

# install

```shell
mvn clean package -DskipTests
java -jar ./broker/target/moquette-broker-0.10.jar
```

# extension

You can use your own authorization or authentication, even add handler to netty's pipeline, to implement the full definition of the MQTT protocol.
