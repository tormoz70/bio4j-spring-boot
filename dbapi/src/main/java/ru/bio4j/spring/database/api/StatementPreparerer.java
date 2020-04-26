package ru.bio4j.spring.database.api;

import java.util.function.Supplier;

public interface StatementPreparerer {
    void prepare(Supplier<String> sqlSupplier);
}
