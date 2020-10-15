package ru.bio4j.spring.dba.configs;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.dba.ExcelBuilderImpl;
import ru.bio4j.spring.model.CacheProperties;

@Configuration
@EnableConfigurationProperties({ CacheProperties.class })
public class ExcelBuilderConfiguration {

    @Bean
    public ExcelBuilder excelBuilder() {
        return new ExcelBuilderImpl();
    }
    
}
