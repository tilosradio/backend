package hu.tilos.radio.backend;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.CodecRegistryProvider;

@Configuration
public class Mongodb {

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.db}")
    private String mongoDb;

    @Bean
    public MongoClient createClient() {

        final CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
//                CodecRegistries.fromCodecs(),
                MongoClient.getDefaultCodecRegistry());

        MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();
        MongoClient mongoClient = new MongoClient(mongoHost, 27017);
        mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return mongoClient;
    }

    @Bean
    public DB createMongoDB() throws UnknownHostException {
        return createClient().getDB(mongoDb);
    }

}
