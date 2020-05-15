package ru.bio4j.spring.database.h2;

import ru.bio4j.spring.database.commons.DbNamedParametersStatement;

import java.sql.SQLException;


public class H2NamedParametersStatement0 extends DbNamedParametersStatement {

    public H2NamedParametersStatement0(String query) {
        super(query);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
    }

}