package ru.bio4j.spring.database.commons;

public interface CursorSqlResolver {
    String tryLoadSQL(final String bioCode, String sqlText);
}
