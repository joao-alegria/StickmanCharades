# Stickman Charades Server - Session Commands

Micro service that triggers actions according to events received.

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
java -jar target/session_commands-0.0.1.jar
```

### Using Docker

```sh
mvn clean package -Dmaven.test.skip=true

docker build -t session-commands .
docker run -d -p 8083:8083 \
-e KAFKA_BOOTSTRAP_SERVERS=...\
-e LOGGING_HOST=...\
-e LOGGING_PORT=...\
-e LOGGING_USER=...\
-e LOGGING_PASSWORD=...\
-e LOGGING_INDEX=...\
--name session-commands session-commands
```
