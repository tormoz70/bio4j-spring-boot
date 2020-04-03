package ru.bio4j.spring.dba;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix= "ru.bio4j.jdbc-helper")
public class JdbcHelperProperties {
    private String currentSchema;

    public String getCurrentSchema() {
        return currentSchema;
    }

    public void setCurrentSchema(String currentSchema) {
        this.currentSchema = currentSchema;
    }
}
