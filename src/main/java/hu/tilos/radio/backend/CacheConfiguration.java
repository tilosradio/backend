package hu.tilos.radio.backend;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<GuavaCache> caches = new ArrayList<>();
        for (String cacheName : new String[]{"episodes", "episodes-next", "episodes-last", "episodes-lastweek"}) {
            caches.add(new GuavaCache(cacheName, CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).recordStats().build()));
        }
        for (String cacheName : new String[]{"feed-tilos-type-format", "feed-weekly-type", "feed-show", "feed-tilos", "feed-tilos-type", "feed-weekly", "m3u"}) {
            caches.add(new GuavaCache(cacheName, CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).recordStats().build()));
        }
        cacheManager.setCaches(caches);

        return cacheManager;
    }
}
