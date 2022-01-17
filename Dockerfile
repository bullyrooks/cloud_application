# syntax=docker/dockerfile:experimental
FROM openjdk:11-slim-buster as build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# download dependencies
RUN mvn dependency:go-offline

COPY src src

# Setup the maven cache
RUN --mount=type=cache,target=/root/.m2,rw ./mvnw -B package -DskipTests

FROM openjdk:11-jre-slim-buster
COPY --from=build target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
