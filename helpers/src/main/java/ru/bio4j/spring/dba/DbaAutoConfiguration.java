package ru.bio4j.spring.dba;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.cache.impl.CacheServiceImpl;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.clickhouse.ChContext;
import ru.bio4j.spring.database.h2.H2Context;
import ru.bio4j.spring.database.oracle.OraContext;
import ru.bio4j.spring.model.transport.CacheProperties;
import ru.bio4j.spring.model.transport.DataSourceProperties;
import ru.bio4j.spring.model.transport.Sso2ClientProperties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties({ DataSourceProperties.class, CacheProperties.class, Sso2ClientProperties.class })
public class DbaAutoConfiguration {

    private final DataSourceProperties dataSourceProperties;
    private final CacheProperties cacheProperties;
    private final Sso2ClientProperties sso2ClientProperties;
    private final ApplicationContext applicationContext;

    public DbaAutoConfiguration(
            DataSourceProperties dataSourceProperties,
            CacheProperties cacheProperties,
            Sso2ClientProperties sso2ClientProperties,
            ApplicationContext applicationContext) {
        this.dataSourceProperties = dataSourceProperties;
        this.cacheProperties = cacheProperties;
        this.sso2ClientProperties = sso2ClientProperties;
        this.applicationContext = applicationContext;
    }


    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        ApplicationContextProvider result = new ApplicationContextProvider();
        result.setApplicationContext(applicationContext);
        return result;
    }

    @PostConstruct
    public void init() {
    }

    @Bean
    public SQLContext sqlContext() {
        if(Strings.compare(dataSourceProperties.getDbmsName(), "clickhouse", true))
            return DbContextFactory.createHikariCP(dataSourceProperties, ChContext.class);
        else if(Strings.compare(dataSourceProperties.getDbmsName(), "h2", true))
            return DbContextFactory.createHikariCP(dataSourceProperties, H2Context.class);
        return DbContextFactory.createHikariCP(dataSourceProperties, OraContext.class);
    }

    @Bean
    @Qualifier("default")
    public HttpParamMap httpParamMap() {
        return new DefaultHttpParamMapImpl();
    }

    @Bean
    public DbaAdapter dbaAdapter(
            SQLContext sqlContext,
            ExcelBuilder excelBuilder,
            CacheService cacheService
    ) {
        return new DbaAdapter(sqlContext, excelBuilder, cacheService);
    }

    @Bean
    @Qualifier("default")
    public ErrorProcessor errorProcessor() {
        return new DefaultErrorProcessorImpl();
    }

    @Bean
    public LoginProcessor loginProcessor() {
        return new DefaultLoginProcessorImpl();
    }

    @Bean
    @Qualifier("default")
    public SecurityService securityService() {
        return new DefaultSecurityModuleImpl();
    }

    @Bean
    public CacheService cacheService() {
        return new CacheServiceImpl(cacheProperties);
    }

    @Bean
    public Sso2Client sso2Client() {
        return new Sso2ClientImpl(sso2ClientProperties);
    }

    @Bean
    public ExcelBuilder excelBuilder() {
        return new ExcelBuilderImpl();
    }

    @Lazy
    @Bean
    public OraContext oraContext(final DataSource dataSource, final DataSourceProperties dataSourceProperties) {
        return new OraContext(dataSource, dataSourceProperties);
    }

    @Lazy
    @Bean
    public ChContext chContext(final DataSource dataSource, final DataSourceProperties dataSourceProperties) {
        return new ChContext(dataSource, dataSourceProperties);
    }

    @Lazy
    @Bean
    public H2Context h2Context(final DataSource dataSource, final DataSourceProperties dataSourceProperties) {
        return new H2Context(dataSource, dataSourceProperties);
    }
}
