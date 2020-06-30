package ru.bio4j.spring.database.commons;

import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Sqls;
import ru.bio4j.spring.database.api.SQLNamedParametersStatement;
import ru.bio4j.spring.database.api.SQLParamSetter;
import ru.bio4j.spring.database.api.SqlTypeConverter;
import ru.bio4j.spring.model.transport.BioSQLException;
import ru.bio4j.spring.model.transport.ConvertValueException;
import ru.bio4j.spring.model.transport.Param;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Засовывает значения из params в CallableStatement
 */
public class DbCallableParamSetter implements SQLParamSetter {
    private static final LogWrapper LOG = LogWrapper.getLogger(DbCallableParamSetter.class);

//    private DbCommand owner;
    private SqlTypeConverter sqlTypeConverter = new SqlTypeConverterImpl();
//    public DbCallableParamSetter(DbCommand owner) {
//        this.owner = owner;
//    }

    @Override
    public void setParamsToStatement(SQLNamedParametersStatement statment, List<Param> params) throws SQLException {
        final String sql = statment.getOrigQuery();
        final List<String> paramsNames = Sqls.extractParamNamesFromSQL(sql);
        final List<Param> outParams = new ArrayList<>();
        try (Paramus p = Paramus.set(params)) {
            for (int i = 0; i < paramsNames.size(); i++) {
                String paramName = paramsNames.get(i);
                Param param = p.getParam(paramName);
                if (param != null) {
                    param.setId(i + 1);
                    if(param.getDirection() == Param.Direction.UNDEFINED)
                        param.setDirection(Param.Direction.IN);
                    if(Arrays.asList(Param.Direction.IN, Param.Direction.INOUT).contains((param.getDirection()))){
                        Object val = param.getValue();
                        Class<?> valType = (val != null ? val.getClass() : null);
                        int sqlType = DbUtils.getInstance().paramSqlType(param);
                        String sqlTypeName = DbUtils.getInstance().getSqlTypeName(sqlType);
                        int charSize = 0;
                        if(valType == String.class)
                            charSize = ((String)val).length();
                        Class<?> targetValType = (sqlType > 0 ? sqlTypeConverter.write(sqlType, charSize) : valType);
                        if(val instanceof InputStream && sqlType == Types.BLOB) {
                            // nop
                        } else
                            try {
                                val = (val != null) ? Converter.toType(val, targetValType, true) : val;
                            } catch (ConvertValueException e) {
                                throw BioSQLException.create(String.format("Error cast parameter \"%s\", value \"%s\" from type: \"%s\" to type: \"%s\". Message: %s",
                                        paramName, val, (valType != null ? valType.getSimpleName() : null),
                                        (targetValType != null ? targetValType.getSimpleName() : null), e.getMessage()), e);
                            }
                        try {
                            sqlType = (sqlType == 0 ? sqlTypeConverter.read(targetValType, charSize, false) : sqlType);
                            statment.setObjectAtName(paramName, val, sqlType);
                        } catch (Exception e) {
                            throw BioSQLException.create(String.format("Error on setting parameter \"%s\"(sqlType: %s) to value \"%s\"(type: %s). Message: %s",
                                    paramName, sqlTypeName, val, (valType != null ? valType.getSimpleName() : null), e.getMessage()), e);
                        }
                    }
                    if ((param.getDirection() == Param.Direction.OUT) || (param.getDirection() == Param.Direction.INOUT)) {
                        outParams.add(param);
                    }
                } else
                    throw new IllegalArgumentException("Parameter " + paramName + " not defined in input Params!");
            }
        }
        for (Param outParam : outParams) {
            int sqlType = DbUtils.getInstance().paramSqlType(outParam);
            String paramName = outParam.getName();
            statment.registerOutParameter(paramName, sqlType, outParam.getDirection() == Param.Direction.INOUT);
        }
    }
}
