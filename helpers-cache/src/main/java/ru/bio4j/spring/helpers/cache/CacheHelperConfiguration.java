package ru.bio4j.spring.helpers.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheHelperConfiguration {

    @Bean
    public CacheHelperFactory cacheHelperFactory (@Autowired(required = false) CacheService cacheService) {
        return new CacheHelperFactory(cacheService);
    }
}
