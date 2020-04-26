package ru.bio4j.spring.database.commons;

import ru.bio4j.spring.database.api.SQLCursor;
import ru.bio4j.spring.database.api.StatementPreparerer;
import ru.bio4j.spring.model.transport.BioSQLException;

import java.sql.SQLException;
import java.util.function.Supplier;

public class DbStatementPreparerer implements StatementPreparerer {

    private final DbCursor command;

    public DbStatementPreparerer(SQLCursor command) {
        this.command = (DbCursor) command;
    }

    @Override
    public void prepare(Supplier<String> sqlSupplier) {
        try {
            command.preparedSQL = sqlSupplier != null ? sqlSupplier.get() : command.sql;
            command.preparedStatement = DbNamedParametersStatement.prepareStatement(command.connection, command.preparedSQL, DbNamedParametersStatement.class);
            command.preparedStatement.setQueryTimeout(command.timeout);
        } catch(SQLException e) {
            throw BioSQLException.create(e);
        }
    }

}
