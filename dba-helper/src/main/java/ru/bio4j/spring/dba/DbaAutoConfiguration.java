package ru.bio4j.spring.dba;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.commons.utils.Jecksons;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.oracle.OraContext;
import ru.bio4j.spring.model.transport.DataSourceProperties;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties({ DataSourceProperties.class })
public class DbaAutoConfiguration {

    @Autowired
    private DataSourceProperties dataSourceProperties;

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


}
