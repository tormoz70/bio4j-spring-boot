package ru.bio4j.spring.dba;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.bio4j.spring.model.DataSourceProperties;

@ConfigurationProperties(prefix= "spring.datasource")
public class TestDataSourceProperties extends DataSourceProperties {
}
