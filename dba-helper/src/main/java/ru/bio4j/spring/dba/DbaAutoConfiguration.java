package ru.bio4j.spring.dba;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.oracle.OraContext;
import ru.bio4j.spring.model.transport.DataSourceProperties;

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

    @Bean(name="sqlContext")
    public SQLContext sqlContext() {
        return DbContextFactory.createHikariCP(dataSourceProperties, OraContext.class);
    }

    @Bean(name="httpParamMap")
    public HttpParamMap httpParamMap() {
        return new HttpParamMap() {};
    }

    @Bean(name="dbaAdapter")
    @ConditionalOnBean(name = "sqlContext")
    public DbaAdapter dbaAdapter() {
        return new DbaAdapter();
    }

    @Bean
    public ErrorProcessor errorProcessor() {
        return new DefaultErrorProcessorImpl();
    }

    @Bean
    public LoginProcessor loginProcessor() {
        return new DefaultLoginProcessorImpl();
    }

    @Bean
    public SecurityService securityService() {
        return new DefaultSecurityModuleImpl();
    }
}
