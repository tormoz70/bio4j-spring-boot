package ru.bio4j.spring.dba.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.pgsql.PgSQLContext;
import ru.bio4j.spring.dba.DbaAdapter;
import ru.bio4j.spring.model.BaseDataSourceProperties;
import ru.bio4j.spring.model.PgSQLDataSourceProperties;

@Configuration
@EnableConfigurationProperties({ PgSQLDataSourceProperties.class })
public class PgSQLHelperConfiguration {

    private final BaseDataSourceProperties dataSourceProperties;

    public PgSQLHelperConfiguration(PgSQLDataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Bean
    public DbaAdapter pgsqlAdapter(
            @Autowired(required = false) ExcelBuilder excelBuilder,
            @Autowired(required = false) CacheService cacheService) {
        final SQLContext pgsqlSqlContext = DbContextFactory.createHikariCP(dataSourceProperties, PgSQLContext.class);
        return new DbaAdapter(pgsqlSqlContext, excelBuilder, cacheService);
    }

}
