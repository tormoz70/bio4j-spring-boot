package ru.bio4j.spring.dba;


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(JdbcHelper.class)
public class JdbcHelperAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public JdbcHelper jdbcHelper() {
        return new JdbcHelperImpl(null);
    }

}
