package ru.bio4j.spring.database.commons;

import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.model.config.props.DataSourceProperties;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.User;
import ru.bio4j.spring.model.transport.errors.BioSQLException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DbContextAbstract implements SQLContext {
    private static final LogWrapper LOG = LogWrapper.getLogger(DbContextAbstract.class);

    protected Wrappers wrappers;
    protected DataSource dataSource;

    protected final List<SQLConnectionConnectedEvent> afterEvents = new ArrayList<>();
    protected final List<SQLConnectionConnectedEvent> innerAfterEvents = new ArrayList<>();

    protected final DataSourceProperties dataSourceProperties;

    protected DbContextAbstract(final DataSource dataSource, final DataSourceProperties dataSourceProperties) {
        this.dataSource = dataSource;
        this.dataSourceProperties = dataSourceProperties;
    }

    public User currentUser() {
        return ThreadContextHolder.instance().getCurrentUser();
    }

    public Connection currentConnection() {
        return ThreadContextHolder.instance().getCurrentConnection();
    }

    public SQLReader createReader(){
        return new DbReader();
    }

    private void setCurrentContext(User user, Connection conn) {
        ThreadContextHolder.instance().setContext(user, conn, this);
    }

    private void closeCurrentContext() {
        ThreadContextHolder.instance().close();
    }

    @Override
    public void addAfterEvent(SQLConnectionConnectedEvent e) {
        this.afterEvents.add(e);
    }

    @Override
    public void clearAfterEvents() {
        this.afterEvents.clear();
    }

    protected void doAfterConnect(SQLConnectionConnectedEvent.Attributes attrs) throws SQLException {
        if (this.innerAfterEvents.size() > 0) {
            for (SQLConnectionConnectedEvent e : this.innerAfterEvents)
                e.handle(this, attrs);
        }
        if (this.afterEvents.size() > 0) {
            for (SQLConnectionConnectedEvent e : this.afterEvents)
                e.handle(this, attrs);
        }
    }


    private void forceCloseConnection(Connection conn){
        if (conn != null) {
            try {
                conn.rollback();
                conn.close();
            } catch (Exception e) {
            }
        }
    }

    protected synchronized Connection getConnection(User user) throws SQLException {
        LOG.debug("Getting connection from pool...");
        Connection conn = dataSource.getConnection();
        if(conn.isClosed()) {
            forceCloseConnection(conn);
            LOG.debug("Connection is closed!");
            throw new SQLException("Connection is closed!");
        }
        if(!conn.isValid(5)) {
            forceCloseConnection(conn);
            LOG.debug("Connection is not valid!");
            throw new SQLException("Connection is not valid!");
        }

        if(conn != null) {
            try {
                doAfterConnect(SQLConnectionConnectedEvent.Attributes.build(conn, user));
                LOG.debug("Connection is ok...");
                return conn;
            } catch (Exception e) {
                forceCloseConnection(conn);
                LOG.debug(String.format("Unexpected error on execute doAfterConnect event! Message: %s", e.getMessage()), e);
                throw e;
            }
        }
        return null;
    }


    /**
     * Выполняет action внутри batch.
     * Перед началом выполнения создается соединение.
     * Если в процессе выполнения последовательности не было ошибок,
     * по завершении выполняется commit,
     * иначе транзакция откатывается.
     */
    @Override
    public <P, R> R execBatch (final SQLActionScalar1<P, R> batch, final P param, final User usr) {
        try {
            R result = null;
            try (Connection conn = this.getConnection(usr)) {
                conn.setAutoCommit(false);
                setCurrentContext(usr, conn);
                try {
                    if (batch != null)
                        result = batch.exec(this, param);
                    currentConnection().commit();
                } catch (SQLException e) {
                    if (currentConnection() != null)
                        try {
                            currentConnection().rollback();
                        } catch (SQLException e1) {}
                    throw e;
                } finally {
                    closeCurrentContext();
                }
            }
            return result;
        } catch (SQLException e) {
            throw BioSQLException.create(e);
        }
    }

    @Override
    public void execBatch (final SQLActionVoid0 batch, final User usr) {
        execBatch((context, param) -> {
            if (batch != null)
                batch.exec(context);
            return null;
        }, null, usr);
    }

    @Override
    public <P> void execBatch (final SQLActionVoid1 batch, final P param, final User usr) {
        execBatch((context, prm) -> {
            if (batch != null)
                batch.exec(context, prm);
            return null;
        }, param, usr);
    }

    @Override
    public <R> R execBatch (final SQLActionScalar0<R> batch, final User usr) {
        return execBatch((context, p) -> {
            if (batch != null)
                return batch.exec(context);
            return null;
        }, null, usr);
    }

    @Override
    public SQLCursor createCursor(){
        return new DbCursor(this);
    }

    @Override
    public SQLCursor createDynamicCursor(){
        return new DbDynamicCursor(this);
    }

    public StatementPreparerer createDbStatementPreparerer(DbCursor cursor) {
        return new DbStatementPreparerer(cursor);
    }

    @Override
    public SQLStoredProc createStoredProc(){
        DbStoredProc cmd = new DbStoredProc();
        cmd.setParamSetter(new DbCallableParamSetter());
        cmd.setParamGetter(new DbCallableParamGetter());
        return cmd;
    }

    @Override
    public abstract String dbmsName();

    @Override
    public Wrappers getWrappers() {
        return wrappers;
    }

    @Override
    public DataSourceProperties getDataSourceProperties() {
        return dataSourceProperties;
    }

    @Override
    public StoredProgMetadata prepareStoredProc(String sql, Connection conn, List<Param> paramsDeclaration) {
        return DbUtils.getInstance().detectStoredProcParamsAuto(sql, conn, paramsDeclaration);
    }

    @Override
    public StatementPreparerer createDbStatementPreparerer(SQLCursor cursor) {
        return new DbStatementPreparerer(cursor);
    }

}
