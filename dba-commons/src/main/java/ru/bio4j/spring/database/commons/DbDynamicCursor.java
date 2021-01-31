package ru.bio4j.spring.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.SrvcUtils;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbDynamicCursor extends DbCursor implements SQLCursor {
    private static final Logger LOG = LoggerFactory.getLogger(DbDynamicCursor.class);


    public DbDynamicCursor(SQLContext context) {
        super(context);
    }

    @Override
    public SQLReader createReader() {
        return new DbReader();
    }

    @Override
	public String getSQL() {
		return this.sql;
	}

    public SQLCursor init(Connection conn, SQLDef sqlDef, int timeout) {
        this.connection = conn;
        this.timeout = timeout;
        if(sqlDef != null)
            this.params = Paramus.clone(sqlDef.getParamDeclaration());
        return this;
    }

    @Override
    public boolean fetch(List<Param> inParams, User usr, DelegateSQLFetch onrecord) {
        boolean rslt = false;
        List<Param> prms = inParams != null ? inParams : new ArrayList<>();
        SrvcUtils.applyCurrentUserParams(usr, prms);

        if (params == null)
            params = new ArrayList<>();

        DbUtils.applyParamsToParams(inParams, params, false, true, false);

        if (!doBeforeStatement(params)) // Обрабатываем события
            return rslt;

        try {
            if(statementPreparerer != null)
                statementPreparerer.prepare(() -> { return DbUtils.cutFilterConditions(sql, params); });
            setParamsToStatement(); // Применяем параметры

            try (ResultSet result = preparedStatement.executeQuery()) {
                isActive = true;
                while (reader.next(result)) {
                    rslt = true;
                    if (onrecord != null) {
                        if (!onrecord.callback(reader))
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            DbUtils.getInstance().tryForwardSQLAsApplicationError(e);
        } finally {
            if (preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (Exception ignore) {
                }
        }
        return rslt;
    }

}
