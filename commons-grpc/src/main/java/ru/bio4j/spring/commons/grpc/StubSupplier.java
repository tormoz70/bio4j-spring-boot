package ru.bio4j.spring.commons.grpc;

@FunctionalInterface
public interface StubSupplier<T, R> {
    R get(T channel);
}
