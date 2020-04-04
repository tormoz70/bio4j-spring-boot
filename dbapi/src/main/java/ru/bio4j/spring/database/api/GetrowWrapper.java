package ru.bio4j.spring.database.api;


public interface GetrowWrapper {
    String wrap(String sql, String pkFieldName);
}
