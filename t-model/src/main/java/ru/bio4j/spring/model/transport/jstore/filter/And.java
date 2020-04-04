package ru.bio4j.spring.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class And extends Logical {

    public And() {
        super();
    }

    public And(Expression ... expressions) {
        super(expressions);
    }

}
