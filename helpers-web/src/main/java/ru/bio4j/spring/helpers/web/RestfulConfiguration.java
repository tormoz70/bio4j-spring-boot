package ru.bio4j.spring.helpers.web;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.helpers.web.defaults.*;

@Configuration
public class RestfulConfiguration {

    private final ApplicationContext applicationContext;

    public RestfulConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        ApplicationContextProvider result = new ApplicationContextProvider();
        result.setApplicationContext(applicationContext);
        return result;
    }

    @Bean("defaultHttpParamMap")
    public HttpParamMap httpParamMap() {
        return new DefaultHttpParamMapImpl();
    }
    @Bean("defaultHttpParamDefaults")
    public HttpParamDefaults httpParamDefaults() {
        return new DefaultHttpParamDefaultsImpl();
    }

    @Bean("defaultErrorProcessor")
    public ErrorProcessor errorProcessor() {
        return new DefaultErrorProcessorImpl();
    }

    @Bean("defaultLoginProcessor")
    public LoginProcessor loginProcessor() {
        return new DefaultLoginProcessorImpl();
    }

    @Bean("defaultSecurityService")
    public SecurityService securityService() {
        return new DefaultSecurityModuleImpl();
    }


}
