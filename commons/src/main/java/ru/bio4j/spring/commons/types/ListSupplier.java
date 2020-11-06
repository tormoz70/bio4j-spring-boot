package ru.bio4j.spring.commons.types;

import java.util.List;

/**
 * Represents a supplier of a list of results.
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
public interface ListSupplier<T> {
    /**
     * Gets a list of results.
     *
     * @return a list of results
     */
    List<T> get();
}
