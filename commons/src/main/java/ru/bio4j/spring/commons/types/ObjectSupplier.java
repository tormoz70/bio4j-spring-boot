package ru.bio4j.spring.commons.types;

/**
 * Represents a supplier of a result.
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a functional interface
 * whose functional method is {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier
 */
@FunctionalInterface
public interface ObjectSupplier<T> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
}
