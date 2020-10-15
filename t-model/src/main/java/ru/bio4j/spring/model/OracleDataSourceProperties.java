package ru.bio4j.spring.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix= "oracle.datasource")
public class OracleDataSourceProperties extends BaseDataSourceProperties {
}
