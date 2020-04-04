package ru.bio4j.spring.database.api;

import java.sql.SQLException;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar0<R> {
    R exec(SQLContext context) throws SQLException;
}
