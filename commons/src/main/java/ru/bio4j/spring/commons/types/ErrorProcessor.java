package ru.bio4j.spring.commons.types;

import javax.servlet.ServletResponse;

public interface ErrorProcessor {
    void doResponse(Throwable exception, ServletResponse response);
}
