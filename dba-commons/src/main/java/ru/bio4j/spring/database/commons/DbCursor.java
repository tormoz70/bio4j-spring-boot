package ru.bio4j.spring.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.model.transport.errors.BioSQLException;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.User;
import ru.bio4j.spring.commons.utils.SrvcUtils;
import ru.bio4j.spring.database.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbCursor extends DbCommand<SQLCursor> implements SQLCursor {
    private static final Logger LOG = LoggerFactory.getLogger(DbCursor.class);


	protected boolean isActive = false;

	protected SQLContext context;


    protected String sql = null;
    protected SQLReader reader;

    public DbCursor(SQLContext context) {
        this.context = context;
        this.setParamSetter(new DbSelectableParamSetter());
        this.reader = createReader();
        this.statementPreparerer = this.context.createDbStatementPreparerer(this);
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public int getTimeout() {
        return this.timeout;
    }
    public void setPreparedStatement(SQLNamedParametersStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }
    public SQLNamedParametersStatement getPreparedStatement() {
        return preparedStatement;
    }
    public void setPreparedSQL(String preparedSQL) {
        this.preparedSQL = preparedSQL;
    }

	@Override
	public SQLCursor init(Connection conn, SelectSQLDef sqlDef, int timeout) {
        if(sqlDef == null)
            throw new IllegalArgumentException("Parameter \"sqlDef\" cannon be null!!!");
        if(Strings.isNullOrEmpty(sqlDef.getPreparedSql()))
            throw new IllegalArgumentException("Parameter \"sqlDef.getPreparedSql()\" cannon be empty!!!");
        this.sql = sqlDef.getPreparedSql();
		return super.init(conn, sqlDef, timeout);
	}

    @Override
    public SQLCursor init(Connection conn, SelectSQLDef sqlDef) {
        return this.init(conn, sqlDef, 60);
    }

    @Override
    public SQLCursor init(Connection conn, String sql, int timeout) {
        if(Strings.isNullOrEmpty(sql))
            throw new IllegalArgumentException("Parameter \"sql\" cannon be empty!!!");
        this.sql = sql;
        return super.init(conn, null, timeout);
    }

    @Override
    public SQLCursor init(final Connection conn, final String sql, final List<Param> paramDeclaration) {
        params = Paramus.clone(paramDeclaration);
        return init(conn, sql, 60);
    }

    @Override
    public SQLCursor init(Connection conn, String sql) {
        return this.init(conn, sql, 60);
    }

    @Override
    public SQLReader createReader() {
        return ThreadContextHolder.instance().getSQLContext().createReader();
    }

    @Override
	public String getSQL() {
		return this.sql;
	}

    @Override
    public boolean fetch(List<Param> params, User usr, DelegateSQLFetch onrecord) {
        boolean rslt = false;
        BioSQLException lastError = null;
        try {
            List<Param> prms = params != null ? params : new ArrayList<>();
            SrvcUtils.applyCurrentUserParams(usr, prms);
            try {

                if (this.params == null)
                    this.params = new ArrayList<>();

                DbUtils.applyParamsToParams(params, this.params, false, true, false);

                if (!doBeforeStatement(this.params)) // Обрабатываем события
                    return rslt;

                setParamsToStatement(); // Применяем параметры

                if(LOG.isDebugEnabled()) LOG.debug("Try to execute: {}", DbUtils.getSQL2Execute(this.preparedSQL, this.preparedStatement.getParamsAsString()));
                try (ResultSet result = this.preparedStatement.executeQuery()) {
                    this.isActive = true;
                    while (this.reader.next(result)) {
                        rslt = true;
                        if (onrecord != null) {
                            if (!onrecord.callback(this.reader))
                                break;
                        }
                    }
                }

            } catch (SQLException e) {
                lastError = DbUtils.getInstance().extractStoredProcAppErrorMessage(e);
                if(lastError == null)
                    lastError = BioSQLException.create(String.format("%s:\n - %s", "Error on execute command.", DbUtils.getSQL2Execute(this.preparedSQL, this.params)), e);
            }
        } finally {
            if (this.preparedStatement != null)
                try {
                    this.preparedStatement.close();
                } catch (Exception ignore) {
                }
            if(lastError != null)
                throw lastError;
            return rslt;
        }
    }

    @Override
    public boolean fetch(User usr, DelegateSQLFetch onrecord) {
        return this.fetch(null, usr, onrecord);
    }

    protected static class ScalarResult<T> {
        public T result;
    }

    @Override
    public <T> T scalar(final List<Param> params, final User usr, final String fieldName, final Class<T> clazz, T defaultValue) {
        final ScalarResult<T> rslt = new ScalarResult();
        if(this.fetch(params, usr, (rs -> {
            if(rs.getFields().size() > 0) {
                if(Strings.isNullOrEmpty(fieldName))
                    rslt.result = rs.getValue(1, clazz);
                else
                    rslt.result = rs.getValue(fieldName, clazz);
            } else {
                try {
                    if (Strings.isNullOrEmpty(fieldName))
                        rslt.result = Converter.toType(rs.getResultSet().getObject(1), clazz);
                    else {
                        if(fieldName.equals("json")) {
                            String jsonString = rs.getResultSet().getString(fieldName);
                            rslt.result = (T)jsonString;
                        } else {
                            rslt.result = Converter.toType(rs.getResultSet().getObject(fieldName), clazz);
                        }
                    }
                } catch(SQLException e) {
                    throw Utl.wrapErrorAsRuntimeException(e);
                }
            }
            return false;
        })))
            return Converter.toType(rslt.result, clazz);
        return defaultValue;
    }

    @Override
    public <T> T scalar(final List<Param> params, final User usr, final Class<T> clazz, T defaultValue) {
        return scalar(params, usr, null, clazz, defaultValue);
    }

    @Override
    public <T> T scalar(final User usr, final String fieldName, final Class<T> clazz, T defaultValue) {
        return scalar(null, usr, fieldName, clazz, defaultValue);
    }

    @Override
    public <T> T scalar(final User usr, final Class<T> clazz, T defaultValue) {
        return scalar(null, usr, null, clazz, defaultValue);
    }

    @Override
    public <T> List<T> beans(final List<Param> params, final User usr, final Class<T> clazz) {
        final List<T> rslt = new ArrayList<>();
        this.fetch(null, usr, (rs -> {
            rslt.add(DbUtils.createBeanFromReader(rs, clazz));
            return true;
        }));
        return rslt;
    }

    @Override
    public <T> List<T> beans(final User usr, final Class<T> clazz) {
        return beans(null, usr, clazz);
    }

    @Override
    public <T> T firstBean(final List<Param> params, final User usr, final Class<T> clazz) {
        final List<T> rslt = new ArrayList<>();
        this.fetch(params, usr, (rs -> {
            rslt.add(DbUtils.createBeanFromReader(rs, clazz));
            return false;
        }));
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    @Override
    public <T> T firstBean(final User usr, final Class<T> clazz) {
        return firstBean(null, usr, clazz);
    }

}
