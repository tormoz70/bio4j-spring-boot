package ru.bio4j.spring.database.api;

import ru.bio4j.spring.model.transport.errors.BioSQLApplicationError;
import ru.bio4j.spring.model.transport.Param;

import java.sql.Connection;
import java.util.List;

public interface RDBMSUtils {
    StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> fixedParamsOverride);
    BioSQLApplicationError extractStoredProcAppError(Exception c);
}
