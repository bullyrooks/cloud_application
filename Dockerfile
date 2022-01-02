FROM maven:3.8.1-openjdk-11 as build
# Create the workspace
ENV HOME=/home/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

# add pom.xml
ADD pom.xml $HOME

# download dependencies
RUN mvn dependency:go-offline

# add all source code and start compiling
ADD . $HOME

# Setup the maven cache
RUN --mount=type=cache,target=/root/.m2 mvn -f $HOME/pom.xml --batch-mode package -DskipTests

FROM openjdk:11.0.12
RUN mkdir /opt/app
COPY --from=build /home/usr/app/target/*.jar /opt/app/app.jar
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
