package hu.tilos.radio.backend;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        GuavaCacheManager guavaCacheManager = new GuavaCacheManager("episodes","episodes-next","episodes-last","episodes-lastweek");

        guavaCacheManager.setCacheBuilder(CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.MINUTES).recordStats());
        return guavaCacheManager;
    }
}
