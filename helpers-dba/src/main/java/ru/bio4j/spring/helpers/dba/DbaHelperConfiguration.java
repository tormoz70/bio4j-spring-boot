package ru.bio4j.spring.helpers.dba;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.helpers.cache.CacheService;

@Configuration
public class DbaHelperConfiguration {

    @Bean
    public DbaHelperFactory dbaHelperFactory (
            @Autowired(required = false) ExcelBuilder excelBuilder,
            @Autowired(required = false) CacheService cacheService) {
        return new DbaHelperFactory(excelBuilder, cacheService);
    }

    @Bean
    public ExcelBuilder excelBuilder() {
        return new ExcelBuilderImpl();
    }

}
