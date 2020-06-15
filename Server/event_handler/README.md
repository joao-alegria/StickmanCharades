# Stickman Charades Server - Event Handler

Micro service in charge of detecting events on continous data.

## How to run

### Using Maven

```sh
mvn clean package -Dmaven.test.skip=true

KAFKA_BOOTSTRAP_SERVERS=...\
LOGGING_HOST=...\
LOGGING_PORT=...\
LOGGING_USER=...\
LOGGING_PASSWORD=...\
LOGGING_INDEX=...\
java -jar target/event_handler-0.0.1.jar
```

### Using Docker

```sh
mvn clean package -Dmaven.test.skip=true

docker build -t event-handler .
docker run -d -p 8083:8083 \
-e KAFKA_BOOTSTRAP_SERVERS=...\
-e LOGGING_HOST=...\
-e LOGGING_PORT=...\
-e LOGGING_USER=...\
-e LOGGING_PASSWORD=...\
-e LOGGING_INDEX=...\
--name event-handler event-handler
```
