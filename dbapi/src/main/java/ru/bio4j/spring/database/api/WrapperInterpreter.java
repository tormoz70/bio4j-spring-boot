package ru.bio4j.spring.database.api;


import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.Total;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import java.util.List;

/**
 * Интерпретатор объектов Filter, Sort
 * Генерит SQL операторы для WHERE, ORDER BY соответственно
 */
public interface WrapperInterpreter {
    /**
     * Интерпретирует filter.Expression to SQL
     * @param alias
     * @param filter
     * @return
     */
    String filterToSQL(String alias, Filter filter, List<Field> fields);
    String sortToSQL(String alias, List<Sort> sort, List<Field> fields);

    /**
     * Интерпретирует {@code totals} to SQL.
     * <p>
     * <b>Важно!</b> При формировании SQL-запроса алиасы полей с агрегатными функциями должны
     * формирования по следующему шаблону:
     * <blockquote><pre>
     * {имя_поля}_{имя_агрегатной_функции_из_totals}
     * </pre></blockquote>
     * @param alias  алиас для оборачиваемого запроса
     * @param totals список описаний агрегртных функций
     * @param fields список описаний полей запроса
     * @return подготовленный SQL-запрос
     */
    String totalsToSQL(String alias, List<Total> totals, List<Field> fields);
}
