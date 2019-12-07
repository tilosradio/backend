package hu.tilos.radio.backend.episode;

import javax.inject.Singleton;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import com.mongodb.DB;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@Singleton
public class MongoRule {

  private MongoDbRule mongoDbRule =
      newMongoDbRule().defaultSpringMongoDb("demo-test");

  @Bean
  public MongoDbRule getRule() {
    return mongoDbRule;
  }

  @Bean
  public DB getDB() {
    return mongoDbRule.getDatabaseOperation().connectionManager()
        .getDB("demo-test");
  }
}
