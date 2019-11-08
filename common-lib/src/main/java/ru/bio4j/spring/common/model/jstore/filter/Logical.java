package ru.bio4j.spring.common.model.jstore.filter;

/**
 * Элемент фильтра
 */

public class Logical extends Expression {

    public Logical() {
        super();
    }

    public Logical(Expression ... expressions) {
        super(expressions);
    }

}
