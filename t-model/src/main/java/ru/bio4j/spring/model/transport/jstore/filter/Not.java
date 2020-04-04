package ru.bio4j.spring.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Not extends Expression {

    public Not(Expression ... expressions) {
        super(expressions);
    }

    public Not() {
        super();
    }

}
