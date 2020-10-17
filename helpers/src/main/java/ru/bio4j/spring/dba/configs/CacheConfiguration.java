package ru.bio4j.spring.dba.configs;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.cache.impl.CacheServiceImpl;
import ru.bio4j.spring.model.config.props.CacheProperties;

@Configuration
@EnableConfigurationProperties({ CacheProperties.class })
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
