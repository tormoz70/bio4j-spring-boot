package ru.bio4j.spring.dba.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.types.ApplicationContextProvider;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.pgsql.PgSQLContext;
import ru.bio4j.spring.dba.DbaHelper;
import ru.bio4j.spring.dba.DbaHelperFactory;
import ru.bio4j.spring.model.config.props.DataSourceProperties;

@Configuration
public class DbaHelperConfiguration {

    @Bean
    public DbaHelperFactory dbaHelperFactory (
            @Autowired(required = false) ExcelBuilder excelBuilder,
            @Autowired(required = false) CacheService cacheService) {
        return new DbaHelperFactory(excelBuilder, cacheService);
    }

}
