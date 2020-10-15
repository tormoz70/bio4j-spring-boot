package ru.bio4j.spring.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix= "cliclhouse.datasource")
public class ChDataSourceProperties extends BaseDataSourceProperties {
}
