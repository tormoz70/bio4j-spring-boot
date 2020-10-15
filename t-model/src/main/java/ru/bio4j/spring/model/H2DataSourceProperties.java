package ru.bio4j.spring.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix= "h2.datasource")
public class H2DataSourceProperties extends BaseDataSourceProperties {
}
