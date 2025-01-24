FROM maven:3.9-eclipse-temurin-23 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:23-alpine-3.21
COPY --from=build /target/grocery-0.0.1-SNAPSHOT.jar grocery-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "grocery-0.0.1-SNAPSHOT.jar"]