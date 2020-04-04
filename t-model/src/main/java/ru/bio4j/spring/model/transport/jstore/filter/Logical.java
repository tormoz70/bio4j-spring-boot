package ru.bio4j.spring.model.transport.jstore.filter;

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
