package ru.bio4j.spring.commons.utils;

import ru.bio4j.spring.model.transport.jstore.filter.And;
import ru.bio4j.spring.model.transport.jstore.filter.Expression;

public class Filters {

    private And root;

    public static Filters instance() {
        return new Filters();
    }

}
