# Tilos API backend
Unified service including all the previous microservices

1. Use Java 1.8

1. in `application-dev.properties` file:
  <br>`jwt.secret=YOUR SECRET`
  <br>`bearer.token=YOUR TOKEN`
  <br>Use http://jwtbuilder.jamiekurtz.com to generate token and key.

1. In `run configuration` make sure you set the `working directory` correctly. The `environment variables` field: `spring.profiles.active=dev`

1. Run 'Starter' should run it and give you a result similar to this: `Started Starter in 6.962 seconds (JVM running for 7.579)`

## Development

### Start docker container
This will start MongoDB server.
```
docker-compose up -d
```

<hr>

# Step by step documentation

If you're not so familiar with Java development, here's a step by step guide
to get the API up and running:


1. Get [Docker](https://www.docker.com/products/docker-desktop/) if you don't have it yet. In the terminal, run:

```
docker-compose up -d
```

To start the MongoDB server on port 27017.

2. Download [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/)

3. Open the IntelliJ IDE. Under `File > Project Structure`, select *JDK version 1.8* 
(you can download it right from the settings screen).

![](docs/01_project_structure.png)

![](docs/02_set_jdk.png)

4. Add a new *Run configuration*.

![](docs/03_add_configuration.png)

Set `hu.tilos.radio.backend.EpisodeStarter` as the *Main class*. 
Be sure *JDK version* 1.8 is selected. 
Set the profile *environment variable*:

```
SPRING_PROFILES_ACTIVE=dev 
```

![](docs/04_config.png)


5. Set your JWT secret in `src/main/resources/application-dev.properties`. It should be a long string.

```
jwt.secret=tilosbackenddevtitkoskulcsbizony
```

6. Create the first admin user

Using a MongoDB GUI tool, such as [MongoDB Compass](https://www.mongodb.com/try/download/compass),
connect to the Mongo server at `localhost:27017`.

Create a database named `tilos` database and collection named `user`, and add the following user data: 
```
{
  "_id": {
    "$oid": "62af594e8481fe0000ec74e0"
    },
  "email": "admin@tilos.hu",
  "username": "admin",
  "role_id": 4,
  "role": 4,
  "salt": "640894e35ede4192ca179c32eea929f7110be1fa",
  "created": {
    "$date": {
      "$numberLong": "1655658830236"
      }
    },
  "password": "41a86445f91613260e5b03f377027df9d1102b59"
}
```

![](docs/05_mongo_compass.png)

This will create an user called `admin` with the password `admin`.

7. Generate your *Bearer token*

If you set the `jwt.secret` to `tilosbackenddevtitkoskulcsbizony`, you can use this Bearer token:

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ0aWxvcy5odSIsImlhdCI6MTY1NTQ5MDc1MCwiZXhwIjoxNjg3MDI2NzUwLCJhdWQiOiJ0aWxvcy5odSIsInN1YiI6ImFkbWluQHRpbG9zLmh1IiwidXNlcm5hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AdGlsb3MuaHUiLCJyb2xlX2lkIjoiNCIsInJvbGUiOiI0In0.6ch43Dph26v9ggihhifJlMdqYs45qxUkcf1TErBwp4M
```

Otherwise, use [http://jwtbuilder.jamiekurtz.com/](http://jwtbuilder.jamiekurtz.com/) to build your JWT token, see the screenshots:

![](docs/06_jwt.png)

![](docs/07_jwt.png)

8. Run the application

Press the green play button in the IntelliJ IDE. 

![](docs/08_run.png)

9. Test the API

Use an API client like [Postman](https://www.postman.com/downloads/) or [PAW](https://paw.cloud/) (or just `curl`) to access the API. 
Send the `Authorization` header with the bearer token.

curl example:

```
curl "http://localhost:7777/api/v1/user/me" \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ0aWxvcy5odSIsImlhdCI6MTY1NTQ5MDc1MCwiZXhwIjoxNjg3MDI2NzUwLCJhdWQiOiJ0aWxvcy5odSIsInN1YiI6ImFkbWluQHRpbG9zLmh1IiwidXNlcm5hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AdGlsb3MuaHUiLCJyb2xlX2lkIjoiNCIsInJvbGUiOiI0In0.6ch43Dph26v9ggihhifJlMdqYs45qxUkcf1TErBwp4M'
```

Should return

```
{"username":"admin","email":"admin@tilos.hu","role":"USER","id":"62af594e8481fe0000ec74e0","author":null,"permissions":[]}
```

If you received a response, the backend API is up and running.

You are now ready to start the [admin frontend](https://github.com/tilosradio/web2-admin).
