package ru.bio4j.spring.database.api;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
public interface SQLActionVoid1<P> {
    void exec(Connection conn, P param) throws SQLException;
}
