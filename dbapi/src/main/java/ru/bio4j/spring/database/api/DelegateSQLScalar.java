package ru.bio4j.spring.database.api;

public interface DelegateSQLScalar<T> {
    public T callback(SQLReader reader) throws Exception;
}
