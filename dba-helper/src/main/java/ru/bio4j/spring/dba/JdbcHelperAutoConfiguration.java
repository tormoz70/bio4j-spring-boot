package ru.bio4j.spring.dba;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.oracle.impl.OraContext;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(DataSource.class)
@EnableConfigurationProperties(JdbcHelperProperties.class)
public class JdbcHelperAutoConfiguration {

    @Autowired
    private JdbcHelperProperties jdbcHelperProperties;

    @Bean
    @ConditionalOnBean(name = "dataSource")
    public SQLContext sqlContext() {
        SQLContextConfig config = new SQLContextConfig();
        config.setCurrentSchema(jdbcHelperProperties.getCurrentSchema());
        DataSource dataSource = DataSourceBuilder.create().build();
        return new OraContext(dataSource, config);
    }

    @Bean
    @ConditionalOnBean(name = "sqlContext")
    public SQLContext sqlContext() {



}
