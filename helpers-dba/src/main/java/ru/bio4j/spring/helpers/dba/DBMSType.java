package ru.bio4j.spring.helpers.dba;

import ru.bio4j.spring.database.clickhouse.ClickhouseContext;
import ru.bio4j.spring.database.commons.DbContextAbstract;
import ru.bio4j.spring.database.h2.H2Context;
import ru.bio4j.spring.database.oracle.OracleContext;
import ru.bio4j.spring.database.pgsql.PgSQLContext;

public enum DBMSType {
    Oracle(OracleContext.class),
    PgSQL(PgSQLContext.class),
    H2(H2Context.class),
    Clickhouse(ClickhouseContext.class);

    private Class<? extends DbContextAbstract> sqlContextType;
    DBMSType(Class<? extends DbContextAbstract> sqlContextType) {
        this.sqlContextType = sqlContextType;
    }
    public Class<? extends DbContextAbstract> getSqlContextType() {
        return sqlContextType;
    }
    public static Class<? extends DbContextAbstract> getSqlContextTypeByName(String dbmsName) {
        for(DBMSType e: DBMSType.values()) {
            if(e.name().equalsIgnoreCase(dbmsName)) {
                return e.getSqlContextType();
            }
        }
        return null;
    }
}
