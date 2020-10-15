package ru.bio4j.spring.dba.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.oracle.OracleContext;
import ru.bio4j.spring.dba.*;
import ru.bio4j.spring.model.*;

@Configuration
@EnableConfigurationProperties({ OracleDataSourceProperties.class })
public class OracleHelperConfiguration {

    private final BaseDataSourceProperties dataSourceProperties;

    public OracleHelperConfiguration(OracleDataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Bean
    public DbaAdapter oracleAdapter(
            @Autowired(required = false) ExcelBuilder excelBuilder,
            @Autowired(required = false) CacheService cacheService) {
        final SQLContext oracleSqlContext = DbContextFactory.createHikariCP(dataSourceProperties, OracleContext.class);
        return new DbaAdapter(oracleSqlContext, excelBuilder, cacheService);
    }

}
