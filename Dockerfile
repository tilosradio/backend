FROM openjdk:8
RUN adduser tilos  --gecos "" --disabled-password
USER tilos
ADD target/service-episode-1.1-SNAPSHOT.jar /home/tilos/episode.jar
CMD ["java", "-jar", "/home/tilos/episode.jar"]
