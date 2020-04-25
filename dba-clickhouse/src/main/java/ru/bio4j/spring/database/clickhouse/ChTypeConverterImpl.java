package ru.bio4j.spring.database.clickhouse;

import ru.bio4j.spring.commons.converter.Types;
import ru.bio4j.spring.database.commons.SqlTypeConverterImpl;
import ru.yandex.clickhouse.domain.ClickHouseDataType;

import java.sql.ResultSet;

public class ChTypeConverterImpl extends SqlTypeConverterImpl {

    @Override
    public int read (Class<?> type, int stringSize, boolean isCallableStatment) {
        if (type == ResultSet.class)
            return java.sql.Types.REF_CURSOR;
        else if (Types.typeIsReal(type))
            return java.sql.Types.DECIMAL;
        else
            return super.read(type, stringSize, isCallableStatment);
    }

}
