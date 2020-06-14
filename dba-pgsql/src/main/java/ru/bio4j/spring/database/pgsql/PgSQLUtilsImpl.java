package ru.bio4j.spring.database.pgsql;

import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Regexs;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.RDBMSUtils;
import ru.bio4j.spring.database.api.SQLNamedParametersStatement;
import ru.bio4j.spring.database.api.StoredProgMetadata;
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
 * Утилиты для работы с метаданными СУБД PostgreSQL
 */
public class PgSQLUtilsImpl implements RDBMSUtils {
	private static class PackageName {
		public final String pkgName;
		public final String methodName;
		public PackageName(String pkgName, String methodName) {
			this.pkgName = pkgName;
			this.methodName = methodName;
		}
	}

    /**
     * Вытаскивает из SQL имя пакета и метода
     * @param storedProcName  - имя процедуры в виде [methodName] или [packageName].[methodName]
     * @return
     */
    private PgSQLUtilsImpl.PackageName parsStoredProcName(String storedProcName) {
        //String pkgName = null;
        //String methodName = null;
        //String[] storedProcNameParts = Strings.split(storedProcName, ".");
        //if(storedProcNameParts.length == 1)
        //    methodName = storedProcNameParts[0];
        //if(storedProcNameParts.length == 2) {
        //    pkgName    = storedProcNameParts[0];
        //    methodName = storedProcNameParts[1];
        //}
        PackageName pkg = new PgSQLUtilsImpl.PackageName(null, storedProcName);
    	return pkg;
    }


    /**
     * Вытаскивает из SQL все вызовы хранимых процедур
     * @param sql
     * @return
     */
    private String[] detectExecsOfStoredProcs(String sql) {
        final String csDelimiter = "+|+";
        String resultStr = null;
        Matcher m = Regexs.match(sql, "\\b[\\w$]+\\b[.]\\b[\\w$]+\\b\\s*[(]\\s*[$]PRMLIST\\s*[)]", Pattern.CASE_INSENSITIVE);
        while(m.find())
            resultStr = Strings.append(resultStr, m.group(), csDelimiter);
        return !Strings.isNullOrEmpty(resultStr) ? Strings.split(resultStr, csDelimiter) : new String[0];
    }

    private static MetaType decodeType(String typeName) {
        if(Strings.isNullOrEmpty(typeName))
            return MetaType.UNDEFINED;
        typeName = typeName.toUpperCase();
        if(Arrays.asList("CHARACTER", "CHARACTER VARYING", "TEXT").contains(typeName))
            return MetaType.STRING;
        if(Arrays.asList("SMALLINT  ", "INTEGER", "BIGINT", "SMALLSERIAL", "SERIAL", "BIGSERIAL").contains(typeName))
            return MetaType.INTEGER;
        if(Arrays.asList("DECIMAL", "NUMERIC", "REAL", "DOUBLE PRECISION").contains(typeName))
            return MetaType.DECIMAL;
        if(Arrays.asList("DATE", "TIMESTAMP", "TIME", "TIMESTAMP WITH TIME ZONE", "TIMESTAMP WITHOUT TIME ZONE", "TIME WITH TIME ZONE", "TIME WITHOUT TIME ZONE").contains(typeName))
            return MetaType.DATE;
        if(Arrays.asList("BYTEA").contains(typeName))
            return MetaType.BLOB;
        if(Arrays.asList("REFCURSOR").contains(typeName))
            return MetaType.CURSOR;
        return MetaType.UNDEFINED;
    }

    public static final String DIRECTION_NAME_IN = "IN";
    public static final String DIRECTION_NAME_OUT = "OUT";
    public static final String DIRECTION_NAME_INOUT = "INOUT";
    private static Param.Direction decodeDirection(String dirName) {
        if(dirName.equals(DIRECTION_NAME_IN))
            return Param.Direction.IN;
        if(dirName.equals(DIRECTION_NAME_OUT))
            return Param.Direction.OUT;
        if(dirName.equals(DIRECTION_NAME_INOUT))
            return Param.Direction.INOUT;
        return Param.Direction.IN;
    }

    private static String cutFirstItem(String paramDesc, String dirName){
        if(paramDesc.toUpperCase().startsWith(dirName.toUpperCase()+" "))
            paramDesc = paramDesc.substring(dirName.length());
        return paramDesc.trim();
    }

    public static String cutDirNames(String paramDesc){
        paramDesc = cutFirstItem(paramDesc, DIRECTION_NAME_IN);
        paramDesc = cutFirstItem(paramDesc, DIRECTION_NAME_OUT);
        paramDesc = cutFirstItem(paramDesc, DIRECTION_NAME_INOUT);
        return paramDesc;
    }

    private static boolean checkDirName(String paramDesc, String dirName){
        if(!Strings.isNullOrEmpty(paramDesc.trim()) && !Strings.isNullOrEmpty(dirName) && paramDesc.trim().toUpperCase().startsWith(dirName+" ")) {
            return true;
        }
        return false;
    }

    public static String extractDirName(String paramDesc){
        if(checkDirName(paramDesc, DIRECTION_NAME_IN))
            return DIRECTION_NAME_IN;
        if(checkDirName(paramDesc, DIRECTION_NAME_OUT))
            return DIRECTION_NAME_OUT;
        if(checkDirName(paramDesc, DIRECTION_NAME_INOUT))
            return DIRECTION_NAME_INOUT;
        return DIRECTION_NAME_IN;
    }

    private static final String SQL_GET_DOMINE_TYPE_DBMS =
            "select data_type from information_schema.domains a\n" +
                    "where a.domain_schema = 'public'\n" +
                    "and a.domain_name = :domain_name";
    private static String detectDomineType(String type, Connection conn) {
        try {
            try (SQLNamedParametersStatement st = DbNamedParametersStatement.prepareStatement(conn, SQL_GET_DOMINE_TYPE_DBMS, DbNamedParametersStatement.class)) {
                st.setStringAtName("domain_name", type);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw BioSQLException.create(e);
        }
    }

    private static Param parsParamDesc(String paramDesc) {
        String dirName = extractDirName(paramDesc);
        paramDesc = cutDirNames(paramDesc);
        String paramNameFromDesc = paramDesc.substring(0, paramDesc.indexOf(" ")).trim().toLowerCase();
        paramNameFromDesc = paramNameFromDesc.startsWith("\"") ? paramNameFromDesc.substring(1) : paramNameFromDesc;
        paramNameFromDesc = paramNameFromDesc.endsWith("\"") ? paramNameFromDesc.substring(0, paramNameFromDesc.length()-1) : paramNameFromDesc;
        paramDesc = cutFirstItem(paramDesc, paramNameFromDesc);
        String typeName = paramDesc;
        MetaType type = decodeType(typeName);
        return Param.builder()
                .name(paramNameFromDesc)
                .type(type)
                .direction(decodeDirection(dirName))
                .innerObject(typeName)
                .build();
    }

    //"p_param1 character varying, OUT p_param2 integer"
    public static void parsParams(String paramsList, List<Param> params, List<Param> paramsOverride) {
        String[] substrs = Strings.split(paramsList, ",");
        int i = 0;
        for (String prmDesc : substrs) {
            Param newParam = parsParamDesc(prmDesc);
            Param overrideParam = null;
            if(paramsOverride != null && paramsOverride.size() > i)
                overrideParam = paramsOverride.get(i).getOverride() ? paramsOverride.get(i) : null;
            if(overrideParam != null) {
                if(overrideParam.getOverride())
                    newParam.setName(DbUtils.normalizeParamName(overrideParam.getName()));
                if(overrideParam.getValue() != null)
                    newParam.setValue(Converter.toType(overrideParam.getValue(), MetaTypeConverter.write(newParam.getType())));
            }
            DbUtils.checkParamName(newParam.getName());
            params.add(newParam);
            i++;
        }
    }

    private static final String SQL_GET_PARAMS_FROM_DBMS = "SELECT pg_get_function_identity_arguments(:method_name::regproc) as rslt";

    private static final String[] DEFAULT_PARAM_PREFIX = {"P_", "V_"};
    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> paramsOverride) {
        PgSQLUtilsImpl.PackageName pkg = this.parsStoredProcName(storedProcName);
        List<Param> params = new ArrayList<>();
        try {
            try (SQLNamedParametersStatement st = DbNamedParametersStatement.prepareStatement(conn, SQL_GET_PARAMS_FROM_DBMS, DbNamedParametersStatement.class)) {
                st.setStringAtName("method_name", pkg.methodName);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        String parsList = rs.getString("rslt");
                        parsParams(parsList, params, paramsOverride);
                    }
                }
            }
        }catch(SQLException e) {
            throw BioSQLException.create(e);
        }
        try(Paramus pp = Paramus.set(params)) {
            for(Param p : pp.get()){
                if(p.getType() == MetaType.UNDEFINED){
                    String typeName = detectDomineType((String)p.getInnerObject(), conn);
                    p.setType(decodeType(typeName));
                }
            }
        }

        String newExec = DbUtils.generateSignature(storedProcName, params);
        return new StoredProgMetadata(newExec, params);
    }

    @Override
    public BioSQLApplicationError extractStoredProcAppError(Exception e) {
        return null;
    }

}
