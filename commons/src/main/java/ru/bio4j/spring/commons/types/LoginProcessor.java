package ru.bio4j.spring.commons.types;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface LoginProcessor {
    void process(final ServletRequest request, final ServletResponse response, final FilterChain chain);
}
