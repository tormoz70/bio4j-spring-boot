package ru.bio4j.spring.commons.types;

/**
 * Значения системных параметров, которые используются по умолчанию
 */
public interface HttpParamDefaults {
    /** Размер страницы по умолчанию */
    default int pageSize() {
        return 50;
    }
}
