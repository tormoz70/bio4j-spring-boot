package ru.bio4j.spring.database.clickhouse;

import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.converter.hanlers.MetaTypeHandler;
import ru.bio4j.spring.commons.utils.Regexs;
import ru.bio4j.spring.commons.utils.Sqls;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.SQLNamedParametersStatement;
import ru.bio4j.spring.database.commons.DbNamedParametersStatement;
import ru.bio4j.spring.database.commons.DbUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChNamedParametersStatement extends DbNamedParametersStatement {

    public ChNamedParametersStatement(String query) {
        super(query);
    }

    @Override
    public void setObjectAtName(String name, Object value, int targetSqlType) throws SQLException {
        paramValues.put(name.toLowerCase(), value);
        paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(targetSqlType));

        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            int indx = indexes[i];
            if(targetSqlType == -999 && value != null)
                targetSqlType = DbUtils.getInstance().getConverter().read(value.getClass(), 0, false);
            if (value == null) {
                statement.setNull(indx, targetSqlType);
            } else if (Arrays.asList(Types.VARCHAR, Types.CHAR).contains(targetSqlType)) {
                statement.setString(indx, (String) value);
            } else if (Arrays.asList(Types.NVARCHAR, Types.NCHAR).contains(targetSqlType)) {
                statement.setNString(indx, (String) value);
            } else if (Arrays.asList(Types.INTEGER).contains(targetSqlType)) {
                statement.setInt(indx, Converter.toType(value, Integer.class));
            } else if (Arrays.asList(Types.SMALLINT).contains(targetSqlType)) {
                statement.setShort(indx, Converter.toType(value, Short.class));
            } else if (Arrays.asList(Types.BIGINT).contains(targetSqlType)) {
                statement.setLong(indx, Converter.toType(value, Long.class));
            } else if (Arrays.asList(Types.DECIMAL).contains(targetSqlType)) {
                statement.setBigDecimal(indx, Converter.toType(value, BigDecimal.class));
            } else if (Arrays.asList(Types.DOUBLE).contains(targetSqlType)) {
                statement.setDouble(indx, Converter.toType(value, Double.class));
            } else if (Arrays.asList(Types.FLOAT, Types.REAL).contains(targetSqlType)) {
                statement.setFloat(indx, Converter.toType(value, Float.class));
            } else if (Arrays.asList(Types.BOOLEAN).contains(targetSqlType)) {
                statement.setBoolean(indx, Converter.toType(value, Boolean.class));
            } else if (Arrays.asList(Types.DATE).contains(targetSqlType)) {
                statement.setDate(indx, new Date(((java.util.Date) value).getTime()));
            } else if (Arrays.asList(Types.TIMESTAMP).contains(targetSqlType)) {
                statement.setTimestamp(indx, new Timestamp(((java.util.Date) value).getTime()));
            } else {
                statement.setString(indx, (String)value);
            }
        }
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
    }

}