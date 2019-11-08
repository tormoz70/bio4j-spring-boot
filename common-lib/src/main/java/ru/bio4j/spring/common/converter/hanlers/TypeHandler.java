package ru.bio4j.spring.common.converter.hanlers;

import ru.bio4j.spring.common.errors.ConvertValueException;

public interface TypeHandler <H> {


    /**
     * Конвертирует входящее значение типа S в значение типа обработчика H
     * @param value
     * @return
     */
    default H read(Object value, Class<?> targetType) throws ConvertValueException {
        return null;
    }

    /**
     * Конвертирует входящее значение типа обработчика H в значение целевого типа T
     * @param value
     * @param <T>
     * @return
     */
    <T> T write(H value, Class<T> targetType) throws ConvertValueException;

    <T> T write(H value, Class<T> targetType, String format) throws ConvertValueException;


    /**
     * Возвращает true если обработчик подходит для заданного java-типа
     * @param type
     * @return
     */
    boolean isHandler(Class<?> type);
}
