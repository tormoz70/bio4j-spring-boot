package ru.bio4j.spring.database.clickhouse;

import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.model.transport.BioSQLApplicationError;
import ru.bio4j.spring.model.transport.Param;

import java.sql.Connection;
import java.util.List;

/**
 * Утилиты для работы с метаданными СУБД Oracle
 */
public class ChUtilsImpl implements RDBMSUtils {

    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> paramsOverride) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BioSQLApplicationError extractStoredProcAppError(Exception e) {
        return null;
    }


}
