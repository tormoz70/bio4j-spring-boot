package ru.bio4j.spring.commons.eureka;

@FunctionalInterface
public interface StubSupplier<T1, T2, R> {
    R get(T1 address, T2 port);
}
