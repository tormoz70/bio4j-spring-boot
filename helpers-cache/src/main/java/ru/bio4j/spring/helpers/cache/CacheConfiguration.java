package ru.bio4j.spring.helpers.cache;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.helpers.cache.impl.CacheServiceImpl;

@Configuration
public class CacheConfiguration {

    private final CacheProperties cacheProperties;
    public CacheConfiguration(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }
    
    @Bean
    public CacheService cacheService() {
        return new CacheServiceImpl(cacheProperties);
    }
    
}
