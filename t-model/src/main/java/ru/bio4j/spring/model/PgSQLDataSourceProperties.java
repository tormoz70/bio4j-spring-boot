package ru.bio4j.spring.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix= "pgsql.datasource")
public class PgSQLDataSourceProperties extends BaseDataSourceProperties {
}
