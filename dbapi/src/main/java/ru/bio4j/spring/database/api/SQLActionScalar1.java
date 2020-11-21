package ru.bio4j.spring.database.api;

import java.sql.SQLException;

/**
 *
 * @param <P> тип вх параметира
 * @param <R> тип результата
 */
@FunctionalInterface
public interface SQLActionScalar1<P, R> {
    R exec(SQLContext context, P param) throws SQLException;
}
