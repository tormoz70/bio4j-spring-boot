package ru.bio4j.spring.dba;

import java.util.List;

public interface JdbcHelper {

    <T> List<T> query(final String sql, final Object prms, final Class<T> clazz);

}
