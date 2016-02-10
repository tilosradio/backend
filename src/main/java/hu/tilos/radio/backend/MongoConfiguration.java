package hu.tilos.radio.backend;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

@Configuration
public class MongoConfiguration {

    String host = "localhost";

    String db = "tilos";

    @Bean
    public DB createMongoDB() throws UnknownHostException {
        return new MongoClient(host).getDB(db);
    }
}
