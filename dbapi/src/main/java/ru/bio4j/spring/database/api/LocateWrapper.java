package ru.bio4j.spring.database.api;

public interface LocateWrapper {

    String wrap(String sql, String pkFieldName);
}
