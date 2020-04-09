FROM openjdk:8-jdk
RUN apt-get update -y && apt-get install -y maven
COPY stickman_charades/ /stickman_charades
WORKDIR /stickman_charades
RUN mvn clean package
ENTRYPOINT ["java","-jar","/stickman_charades/target/stickman_charades-0.0.1-SNAPSHOT.jar"]