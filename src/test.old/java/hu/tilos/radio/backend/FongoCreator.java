package hu.tilos.radio.backend;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;

import java.net.UnknownHostException;

@org.springframework.context.annotation.Configuration
public class FongoCreator {

    private FongoRule fongoRule;

    private boolean embedded = false;

    public void init() {
        if (embedded) {
            fongoRule = new FongoRule();
        } else {
            try {
                String host = "localhost";
                fongoRule = new FongoRule("unit", true, new MongoClient(host));
                fongoRule.getDB();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Bean
    public FongoRule createRule() {
        if (fongoRule == null) {
            init();
        }
        return fongoRule;
    }

    @Bean
    public DB createDB() {
        if (fongoRule == null) {
            init();
        }
        if (embedded) {
            return fongoRule.getFongo().getDB("test");
        } else {
            return fongoRule.getDB("unit");
        }
    }
}
