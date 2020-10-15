package ru.bio4j.spring.dba.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.clickhouse.ChContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.dba.DbaAdapter;
import ru.bio4j.spring.model.BaseDataSourceProperties;
import ru.bio4j.spring.model.ChDataSourceProperties;

@Configuration
@EnableConfigurationProperties({ ChDataSourceProperties.class })
public class ChHelperConfiguration {

    private final BaseDataSourceProperties dataSourceProperties;

    public ChHelperConfiguration(ChDataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Bean
    public DbaAdapter chAdapter(
            @Autowired(required = false) ExcelBuilder excelBuilder,
            @Autowired(required = false) CacheService cacheService) {
        final SQLContext chSqlContext = DbContextFactory.createHikariCP(dataSourceProperties, ChContext.class);
        return new DbaAdapter(chSqlContext, excelBuilder, cacheService);
    }

}
