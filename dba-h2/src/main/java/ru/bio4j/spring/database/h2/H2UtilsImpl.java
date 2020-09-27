package ru.bio4j.spring.database.h2;

import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.model.transport.errors.BioSQLApplicationError;
import ru.bio4j.spring.model.transport.Param;

import java.sql.Connection;
import java.util.List;

/**
 * Утилиты для работы с метаданными СУБД Oracle
 */
public class H2UtilsImpl implements RDBMSUtils {

    @Override
    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> fixedParamsOverride) {
        return null;
    }

    @Override
    public BioSQLApplicationError extractStoredProcAppError(Exception c) {
        return null;
    }


}
