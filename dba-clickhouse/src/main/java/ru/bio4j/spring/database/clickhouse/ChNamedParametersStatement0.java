package ru.bio4j.spring.database.clickhouse;

import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.database.commons.DbNamedParametersStatement;
import ru.bio4j.spring.database.commons.DbUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;


public class ChNamedParametersStatement0 extends DbNamedParametersStatement {

    public ChNamedParametersStatement0(String query) {
        super(query);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
    }

}