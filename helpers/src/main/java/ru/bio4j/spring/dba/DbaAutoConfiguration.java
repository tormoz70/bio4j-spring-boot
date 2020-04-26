package ru.bio4j.spring.dba;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.cache.impl.CacheServiceImpl;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.clickhouse.ChContext;
import ru.bio4j.spring.database.oracle.OraContext;
import ru.bio4j.spring.model.transport.CacheProperties;
import ru.bio4j.spring.model.transport.DataSourceProperties;
import ru.bio4j.spring.model.transport.Sso2ClientProperties;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties({ DataSourceProperties.class, CacheProperties.class, Sso2ClientProperties.class })
public class DbaAutoConfiguration {

    @Autowired
    private DataSourceProperties dataSourceProperties;
    @Autowired
    private CacheProperties cacheProperties;
    @Autowired
    private Sso2ClientProperties sso2ClientProperties;

    @Autowired
    private ApplicationContext applicationContext;

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
        return DbContextFactory.createHikariCP(dataSourceProperties, OraContext.class);
    }

    @Bean
    public HttpParamMap httpParamMap() {
        return new DefaultHttpParamMapImpl();
    }

    @Bean
    public DbaAdapter dbaAdapter() {
        return new DbaAdapter();
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
}
