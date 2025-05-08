#FROM openjdk:17
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

FROM maven:3.8.5-openjdk-17

WORKDIR /springsecurity
COPY . .
RUN mvn clean install
#
CMD mvn spring-boot:run