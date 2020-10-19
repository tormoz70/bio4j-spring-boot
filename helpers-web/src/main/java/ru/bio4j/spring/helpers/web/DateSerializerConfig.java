package ru.bio4j.spring.helpers.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.model.serializers.*;

@Configuration
public class DateSerializerConfig {
    @Bean
    @Qualifier("default")
    public DateSerializer dateSerializerDefault() {
        return new DateSerializerDefault();
    }


    @Bean
    public DateSerializerHolder dateSerializerHolder() {
        return new DateSerializerHolderImpl();
    }

    @Bean
    public BeanExplorer beanExplorer() {
        return new BeanExplorer();
    }

}
