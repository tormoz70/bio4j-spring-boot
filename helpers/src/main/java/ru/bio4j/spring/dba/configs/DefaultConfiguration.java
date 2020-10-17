package ru.bio4j.spring.dba.configs;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.dba.defaults.DefaultErrorProcessorImpl;
import ru.bio4j.spring.dba.defaults.DefaultHttpParamMapImpl;
import ru.bio4j.spring.dba.defaults.DefaultLoginProcessorImpl;
import ru.bio4j.spring.dba.defaults.DefaultSecurityModuleImpl;

@Configuration
public class DefaultConfiguration {

    private final ApplicationContext applicationContext;

    public DefaultConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        ApplicationContextProvider result = new ApplicationContextProvider();
        result.setApplicationContext(applicationContext);
        return result;
    }

    @Bean
    @Qualifier("default")
    public HttpParamMap httpParamMap() {
        return new DefaultHttpParamMapImpl();
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
