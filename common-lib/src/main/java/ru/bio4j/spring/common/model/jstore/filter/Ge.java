package ru.bio4j.spring.common.model.jstore.filter;

/**
 * Элемент фильтра
 */

public class Ge extends Compare {

    public Ge(String column, Object value) {
        super(column, value, false);
    }

    public Ge() {
        this(null, null);
    }
}
