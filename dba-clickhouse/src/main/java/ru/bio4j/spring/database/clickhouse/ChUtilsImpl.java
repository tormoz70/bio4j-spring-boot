package ru.bio4j.spring.database.clickhouse;

import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.model.transport.BioSQLApplicationError;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.Param;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

/**
 * Утилиты для работы с метаданными СУБД Oracle
 */
public class ChUtilsImpl implements RDBMSUtils {
	private static class PackageName {
        public final String schemaName;
		public final String pkgName;
		public final String methodName;
		public PackageName(String schemaName, String pkgName, String methodName) {
            this.schemaName = schemaName;
			this.pkgName = pkgName;
			this.methodName = methodName;
		}
	}

    /**
     * Вытаскивает из SQL имя пакета и метода
     * @param storedProcName  - имя процедуры в виде [methodName] или [packageName].[methodName]
     * @return
     */
    public static ChUtilsImpl.PackageName parsStoredProcName(String storedProcName) {
        throw new UnsupportedOperationException();
    }


    /**
     * Вытаскивает из SQL все вызовы хранимых процедур
     * @param sql
     * @return
     */
    private String[] detectExecsOfStoredProcs(String sql) {
        return new String[0];
    }


    private static MetaType decodeType(String oraTypeName) {
        if(Arrays.asList("CHAR", "VARCHAR", "VARCHAR2", "CLOB").contains(oraTypeName))
            return MetaType.STRING;
        if(Arrays.asList("NUMBER", "INTEGER", "SMALLINT", "FLOAT", "DECIMAL", "DOUBLE PRECISION", "BINARY_DOUBLE", "BINARY_FLOAT").contains(oraTypeName))
            return MetaType.DECIMAL;
        if(Arrays.asList("DATE", "TIMESTAMP", "TIME", "TIME WITH TZ", "TIMESTAMP WITH LOCAL TZ", "TIMESTAMP WITH TZ").contains(oraTypeName))
            return MetaType.DATE;
        if(Arrays.asList("BLOB").contains(oraTypeName))
            return MetaType.BLOB;
        if(Arrays.asList("REF").contains(oraTypeName))
            return MetaType.CURSOR;
        return MetaType.UNDEFINED;
    }

    private static Param.Direction decodeDirection(String oraDirName) {
        return Param.Direction.IN;
    }

    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> paramsOverride) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BioSQLApplicationError extractStoredProcAppError(Exception e) {
        return null;
    }


}
