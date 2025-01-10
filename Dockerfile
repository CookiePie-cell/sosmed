FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/sosmed-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

EXPOSE 5005

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:5005", "-jar", "app.jar"]