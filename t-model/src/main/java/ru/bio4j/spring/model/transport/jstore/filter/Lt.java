package ru.bio4j.spring.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Lt extends Compare {

    public Lt(String fieldName, Object value) {
        super(fieldName, value);
    }
    public Lt() {
        this(null, null);
    }
}
