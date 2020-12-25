package ru.bio4j.spring.database.api;

import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Total;

import java.util.List;

public interface TotalsWrapper {
    /**
     * Заполняет список объектами {@link Total}, созданными из описаний полей.
     * Удаляет из списка те объекты, для которых нет соответствующих полей в списке полей.
     * @param totals список, который нужно заполнить
     * @param fields список полей, из которого создавать объекты {@link Total}
     * @return Изменённый список объектов {@link Total}.
     */
    List<Total> prepare(List<Total> totals, List<Field> fields);

    String wrap(String sql, List<Total> totals, List<Field> fields);
}
