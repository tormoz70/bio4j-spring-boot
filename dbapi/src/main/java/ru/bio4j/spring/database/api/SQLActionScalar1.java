package ru.bio4j.spring.database.api;

import java.sql.SQLException;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar1<P, R> {
    R exec(SQLContext context, P param) throws SQLException;
}
