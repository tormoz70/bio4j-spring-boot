package ru.bio4j.spring.database.api;

import ru.bio4j.spring.model.transport.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 17.12.13
 * Time: 20:19
 * To change this template use File | Settings | File Templates.
 */
public interface SQLParamSetter {
    void setParamsToStatement(SQLNamedParametersStatement statment, List<Param> params) throws SQLException ;
}
