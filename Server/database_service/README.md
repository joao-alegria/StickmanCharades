# Stickman Charades Server - Database Service

Micro service in charge of interacting with the database

## How to run

### Using Maven

```sh
mvn clean package -Dmaven.test.skip=true

PERSISTENCE_HOST=...\
PERSISTENCE_PORT=...\
PERSISTENCE_DB=...\
PERSISTENCE_USER=...
PERSISTENCE_PASSWORD=...\
KAFKA_BOOTSTRAP_SERVERS=...\
LOGGING_HOST=...\
LOGGING_PORT=...\
LOGGING_USER=...\
LOGGING_PASSWORD=...\
LOGGING_INDEX=...\
java -jar target/database_service-0.0.1.jar
```

### Using Docker

```sh
mvn clean package -Dmaven.test.skip=true

docker build -t database-service .
docker run -d -p 8082:8082 \
-e PERSISTENCE_HOST=...\
-e PERSISTENCE_PORT=...\
-e PERSISTENCE_DB=...\
-e PERSISTENCE_USER=...
-e PERSISTENCE_PASSWORD=...\
-e KAFKA_BOOTSTRAP_SERVERS=...\
-e LOGGING_HOST=...\
-e LOGGING_PORT=...\
-e LOGGING_USER=...\
-e LOGGING_PASSWORD=...\
-e LOGGING_INDEX=...\
--name database-service database-service
```
