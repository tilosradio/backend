package hu.tilos.radio.backend;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@TestConfiguration
public class MongoTestDB {

  private String mongoHost = "localhost";

  private String mongoDb = "test";

  @Bean
  public DB createMongoDB() throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(mongoHost, 27017);
    mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
    return mongoClient.getDB(mongoDb);
  }

}
