package ru.bio4j.spring.database.h2;

import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.database.commons.DbNamedParametersStatement;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.transport.BioSQLApplicationError;
import ru.bio4j.spring.model.transport.BioSQLException;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.Param;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
