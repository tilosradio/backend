FROM openjdk:8
RUN adduser tilos  --gecos "" --disabled-password
USER tilos
ADD target/backend-2.0-SNAPSHOT.jar /home/tilos/backend.jar
CMD ["java", "-jar", "/home/tilos/backend.jar"]
