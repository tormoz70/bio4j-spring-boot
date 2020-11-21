package ru.bio4j.spring.database.api;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLActionVoid1<P> {
    void exec(SQLContext context, P param) throws SQLException;
}
