package ru.bio4j.spring.database.api;

import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Total;

import java.util.List;

public interface TotalsWrapper {

    String wrap(String sql, List<Total> totals, List<Field> fields);
}
