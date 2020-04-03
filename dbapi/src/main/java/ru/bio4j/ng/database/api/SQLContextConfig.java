package ru.bio4j.ng.database.api;

public class SQLContextConfig {
    private String currentSchema;

    public String getCurrentSchema() {
        return currentSchema;
    }

    public void setCurrentSchema(String currentSchema) {
        this.currentSchema = currentSchema;
    }
}
