package ru.bio4j.spring.database.clickhouse;

import ru.bio4j.spring.database.api.SQLCursor;
import ru.bio4j.spring.database.api.StatementPreparerer;
import ru.bio4j.spring.database.commons.DbCursor;
import ru.bio4j.spring.database.commons.DbNamedParametersStatement;
import ru.bio4j.spring.model.transport.BioSQLException;

import java.sql.SQLException;
import java.util.function.Supplier;

public class ChStatementPreparerer implements StatementPreparerer {

    private final DbCursor command;

    public ChStatementPreparerer(SQLCursor command) {
        this.command = (DbCursor) command;
    }

    @Override
    public void prepare(Supplier<String> sqlSupplier) {
        try {
            command.setPreparedSQL(command.getSQL());
            command.setPreparedStatement(ChNamedParametersStatement.prepareStatement(command.getConnection(), command.getPreparedSQL(), ChNamedParametersStatement.class));
            command.getPreparedStatement().setQueryTimeout(command.getTimeout());
        } catch(SQLException e) {
            throw BioSQLException.create(e);
        }
    }

}
