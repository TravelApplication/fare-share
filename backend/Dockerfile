FROM jelastic/maven:3.9.5-openjdk-21 AS build
WORKDIR /home/app

COPY ./pom.xml /home/app/pom.xml
COPY ./src/main/java/share/fare/backend/BackendApplication.java /home/app/src/main/java/share/fare/backend/BackendApplication.java

RUN mvn -f /home/app/pom.xml clean package -DskipTests=true

COPY . /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests=true

FROM amazoncorretto:21.0.4-alpine3.18
EXPOSE 8080
COPY --from=build /home/app/target/*.jar backend.jar
ENTRYPOINT ["sh", "-c", "java -jar /backend.jar"]

