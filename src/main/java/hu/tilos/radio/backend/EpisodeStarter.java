package hu.tilos.radio.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configuration
@EnableCaching
@EnableScheduling
public class EpisodeStarter {
    public static void main(String[] args) {
        SpringApplication.run(EpisodeStarter.class, args);
    }
}
