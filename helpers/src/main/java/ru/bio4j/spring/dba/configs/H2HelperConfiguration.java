package ru.bio4j.spring.dba.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.h2.H2Context;
import ru.bio4j.spring.dba.DbaAdapter;
import ru.bio4j.spring.model.BaseDataSourceProperties;
import ru.bio4j.spring.model.H2DataSourceProperties;

@Configuration
@EnableConfigurationProperties({ H2DataSourceProperties.class })
public class H2HelperConfiguration {

    private final BaseDataSourceProperties dataSourceProperties;

    public H2HelperConfiguration(H2DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Bean
    public DbaAdapter h2Adapter(
            @Autowired(required = false) ExcelBuilder excelBuilder,
            @Autowired(required = false) CacheService cacheService) {
        final SQLContext h2SqlContext = DbContextFactory.createHikariCP(dataSourceProperties, H2Context.class);
        return new DbaAdapter(h2SqlContext, excelBuilder, cacheService);
    }

}
