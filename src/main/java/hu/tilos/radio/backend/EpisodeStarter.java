package hu.tilos.radio.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableCaching
@EnableEurekaClient
public class EpisodeStarter {
    public static void main(String[] args) {
        SpringApplication.run(EpisodeStarter.class, args);
    }
}
