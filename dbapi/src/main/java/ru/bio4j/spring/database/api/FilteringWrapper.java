package ru.bio4j.spring.database.api;

import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import java.util.List;


public interface FilteringWrapper {
    String wrap(String sql, Filter filter, List<Field> fields) ;
}
