package ru.bio4j.spring.database.api;

import ru.bio4j.spring.model.transport.DataSourceProperties;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.User;

import java.sql.*;
import java.util.List;

public interface SQLContext {

    User getCurrentUser();
    Connection getCurrentConnection();

    void execBatch (final SQLActionVoid0 batch, final User usr);
    <P> void execBatch (final SQLActionVoid1 batch, final P param, final User usr);
    <R> R execBatch (final SQLActionScalar0<R> batch, final User usr);
    <P, R> R execBatch (final SQLActionScalar1<P, R> batch, final P param, final User usr);

    StoredProgMetadata prepareStoredProc(String sql, Connection conn, List<Param> paramsDeclaration);

    void addAfterEvent(SQLConnectionConnectedEvent e);
    void clearAfterEvents();

    SQLCursor createCursor();
    SQLCursor createDynamicCursor();
    SQLStoredProc createStoredProc();

    String getDBMSName();

    DataSourceProperties getDataSourceProperties();
    Wrappers getWrappers();
    SQLReader createReader();

}
