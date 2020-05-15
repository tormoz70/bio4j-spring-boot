package ru.bio4j.spring.database.api;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar0<R> {
    R exec(Connection conn) throws SQLException;
}
