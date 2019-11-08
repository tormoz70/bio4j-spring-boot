package ru.bio4j.spring.common.model.jstore.filter;

/**
 * Элемент фильтра
 */

public class End extends Compare {

    public End(String column, Object value, boolean ignoreCase) {
        super(column, value, ignoreCase);
    }
    public End(String column, Object value) {
        this(column, value, false);
    }

    public End() {
        this(null, null, false);
    }

}
