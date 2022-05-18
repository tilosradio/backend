# backend
Unified service including all the previous microservices

1. Use Java 1.8

1. in `application-dev.properties` file:
  <br>`jwt.secret=YOUR SECRET`
  <br>`bearer.token=YOUR TOKEN`
  <br>Use http://jwtbuilder.jamiekurtz.com to generate token and key.

1. In `run configuration` make sure you set the `working directory` correctly. The `environment variables` field: `spring.profiles.active=dev`

1. Run 'Starter' should run it and give you a result similar to this: `Started Starter in 6.962 seconds (JVM running for 7.579)`

# Development

#### Start docker container
This will start MongoDB server.
```
docker-compose up -d
```