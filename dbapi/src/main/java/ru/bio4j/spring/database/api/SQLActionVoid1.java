package ru.bio4j.spring.database.api;

import java.sql.SQLException;

/**
 *
 */
public interface SQLActionVoid1<P> {
    void exec(SQLContext context, P param) throws SQLException;
}
