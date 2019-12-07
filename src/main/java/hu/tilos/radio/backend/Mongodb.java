package hu.tilos.radio.backend;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Mongodb {

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.db}")
    private String mongoDb;

    @Bean
    public MongoClient createClient() {
        MongoClient mongoClient = new MongoClient(mongoHost, 27017);
        mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return mongoClient;
    }

    @Bean
    public DB createMongoDB() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(mongoHost, 27017);
        mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return mongoClient.getDB(mongoDb);
    }

}
