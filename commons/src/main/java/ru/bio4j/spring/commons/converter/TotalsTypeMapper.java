package ru.bio4j.spring.commons.converter;

import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.jstore.Total;

import java.math.BigDecimal;

public class TotalsTypeMapper {
    /**
     * Определяет тип данных для {@link Total} по типу агрегата и типу самого поля.
     * @param aggFunc   агрегатная функция
     * @param fieldType тип поля, по которому вычисляется агрегатная функция
     * @return java-класс для типа данных в {@link Total}.
     */
    public static Class<?> write(Total.Aggregate aggFunc, MetaType fieldType) {
        if (aggFunc == Total.Aggregate.COUNT)
            return long.class;
        if (aggFunc == Total.Aggregate.AVG)
            return BigDecimal.class;
        return MetaTypeConverter.write(fieldType);
    }
}
