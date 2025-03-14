FROM ubuntu:latest as builder
RUN apt-get update
RUN apt-get install openjdk-21-jdk -y

COPY . .

RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:21-jdk-slim

EXPOSE 8080
COPY --from=builder /target/Todolist-1.0.0.jar Todolist-1.0.0.jar

ENTRYPOINT ["java", "-jar", "Todolist-1.0.0.jar"]