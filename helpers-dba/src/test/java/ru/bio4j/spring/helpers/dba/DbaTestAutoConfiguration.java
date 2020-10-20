package ru.bio4j.spring.helpers.dba;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.bio4j.spring.helpers.cache.CacheConfiguration;
import ru.bio4j.spring.helpers.cache.CacheProperties;
import ru.bio4j.spring.model.config.props.DataSourceProperties;

@Configuration
@Import({ DbaHelperConfiguration.class, CacheConfiguration.class })
@EnableConfigurationProperties
public class DbaTestAutoConfiguration {

    @Bean
    @ConfigurationProperties("ehcache")
    public CacheProperties cacheProperties() {
        return new CacheProperties();
    }

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