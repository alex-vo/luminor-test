Application assumes that docker is installed on the host. To run the application at port 8080 execute
```
./gradlew bootRun
```
After stopping the application execute
```
./gradlew composeDown
```
to shut down RabbitMQ container