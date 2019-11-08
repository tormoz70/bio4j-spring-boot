package ru.bio4j.spring.common.model.jstore.filter;

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
