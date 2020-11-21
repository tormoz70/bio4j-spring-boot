package ru.bio4j.spring.database.api;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLActionVoid0 {
    void exec(SQLContext context) throws SQLException;
}
