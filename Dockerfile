FROM maven:3.9.9-amazoncorretto-21 AS build

COPY . .
RUN mvn clean package

FROM amazoncorretto:21
COPY --from=build /target/contacts-0.0.1-SNAPSHOT.jar contacts.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","contacts.jar"]
