package ru.bio4j.spring.dba;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.bio4j.spring.dba.configs.DefaultConfiguration;
import ru.bio4j.spring.dba.configs.CacheConfiguration;
import ru.bio4j.spring.dba.configs.DbaHelperConfiguration;
import ru.bio4j.spring.dba.configs.DateSerializerConfig;
import ru.bio4j.spring.model.config.props.DataSourceProperties;

@Configuration
@Import({ DefaultConfiguration.class, DateSerializerConfig.class, DbaHelperConfiguration.class, CacheConfiguration.class })
public class DbaTestAutoConfiguration {

    @Bean
    @ConfigurationProperties("clickhouse.datasource")
    public DataSourceProperties clickhouseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DbaHelper clickhouseDbaHelper(DbaHelperFactory dbaHelperFactory, DataSourceProperties clickhouseDataSourceProperties) {
        return dbaHelperFactory.create(clickhouseDataSourceProperties);
    }

    @Bean
    @ConfigurationProperties("pgsql.datasource")
    public DataSourceProperties pgsqlDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DbaHelper pgsqlDbaHelper(DbaHelperFactory dbaHelperFactory, DataSourceProperties pgsqlDataSourceProperties) {
        return dbaHelperFactory.create(pgsqlDataSourceProperties);
    }

}
