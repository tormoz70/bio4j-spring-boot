package ru.bio4j.spring.database.api;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
public interface SQLActionVoid0 {
    void exec(Connection conn) throws SQLException;
}
