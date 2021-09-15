package ru.bio4j.spring.helpers.cache;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.types.ApplicationContextProvider;
import ru.bio4j.spring.helpers.cache.impl.CacheServiceImpl;

@Configuration
public class CacheConfiguration {

    private final CacheProperties cacheProperties;

    public CacheConfiguration(CacheProperties cacheProperties, ApplicationContext applicationContext) {
        this.cacheProperties = cacheProperties;
        ApplicationContextProvider.setApplicationContextStatic(applicationContext);
    }
    
    @Bean
    public CacheService cacheService() {
        return new CacheServiceImpl(cacheProperties);
    }
}
