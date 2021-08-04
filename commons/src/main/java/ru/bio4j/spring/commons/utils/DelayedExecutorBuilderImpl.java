package ru.bio4j.spring.commons.utils;

import ru.bio4j.spring.commons.types.DelayedExecutor;
import ru.bio4j.spring.commons.types.DelayedExecutorBuilder;

import java.util.concurrent.TimeUnit;

public class DelayedExecutorBuilderImpl implements DelayedExecutorBuilder {
    @Override
    public <T> DelayedExecutor<T> build() {
        return new DelayedExecutorImpl<>();
    }

    @Override
    public <T> DelayedExecutor<T> build(long delay, TimeUnit timeUnit) {
        return new DelayedExecutorImpl<>(delay, timeUnit);
    }
}
