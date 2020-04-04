package ru.bio4j.spring.database.api;

public interface DelegateSQLFetch {
    public boolean callback(final SQLReader reader);
}
