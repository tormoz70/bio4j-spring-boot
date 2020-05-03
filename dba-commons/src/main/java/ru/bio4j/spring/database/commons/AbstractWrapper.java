package ru.bio4j.spring.database.commons;


import ru.bio4j.spring.database.api.WrapperInterpreter;

/**
 * Base class for all cursor.wrapper
 */
public abstract class AbstractWrapper {

    public static final String QUERY_PLACEHOLDER = "${QUERY_PLACEHOLDER}";
    public static final String WHERE_CLAUSE_PLACEHOLDER = "${WHERECLAUSE_PLACEHOLDER}";
    public static final String TOTALS_FIELDS_PLACEHOLDER = "${TOTALS_FIELDS_PLACEHOLDER}";
    protected WrapperInterpreter wrapperInterpreter;

    public AbstractWrapper(String template, WrapperInterpreter wrapperInterpreter) {
        this.wrapperInterpreter = wrapperInterpreter;
        this.parseTemplate(template);
    }

    /**
     * @title Разбор запроса
     * @param template
     */
    protected abstract void parseTemplate(String template);

    public void setWrapperInterpreter(WrapperInterpreter wrapperInterpreter) {
        this.wrapperInterpreter = wrapperInterpreter;
    }
}
