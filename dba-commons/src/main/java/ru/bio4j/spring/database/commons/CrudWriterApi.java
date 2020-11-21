package ru.bio4j.spring.database.commons;

import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.commons.utils.ABeans;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.model.transport.errors.BioError;
import ru.bio4j.spring.model.transport.errors.BioSQLException;
import ru.bio4j.spring.model.transport.jstore.Field;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.bio4j.spring.commons.utils.ABeans.applyValuesToABeanFromABean;

public class CrudWriterApi {


    public static List<ABean> saveRecords(
            final List<Param> params,
            final List<ABean> rows,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user) {
        UpdelexSQLDef sqlDef = cursor.getUpdateSqlDef();
        if(sqlDef == null)
            throw new BioError(String.format("For bio \"%s\" must be defined \"create/update\" sql!", cursor.getBioCode()));
        context.execBatch((ctx) -> {
            SQLStoredProc cmd = ctx.createStoredProc();
            try {
                cmd.init(ctx.currentConnection(), sqlDef);
                List<Param> prms = new ArrayList<>();
                for (ABean row : rows) {
                    prms.clear();
                    Paramus.setParams(prms, params);
                    DbUtils.applayRowToParams(row, prms);
                    cmd.execSQL(prms, ctx.currentUser(), true);
                    try (Paramus paramus = Paramus.set(cmd.getParams())) {
                        for (Param p : paramus.get()) {
                            if (Arrays.asList(Param.Direction.INOUT, Param.Direction.OUT).contains(p.getDirection())) {
                                Field fld = cursor.findField(DbUtils.trimParamNam(p.getName()));
                                row.put(fld.getName().toLowerCase(), p.getValue());
                            }
                        }
                    }
                }
            } finally {
                cmd.close();
            }

            List<Param> prms = new ArrayList<>();
            Field pkField = cursor.findPk();
            String pkFieldName = pkField.getName().toLowerCase();
            Class<?> pkClazz = MetaTypeConverter.write(pkField.getMetaType());
            for(ABean bean : rows){
                Object pkvalue = ABeans.extractAttrFromBean(bean, pkFieldName, pkClazz, null);
                Paramus.setParamValue(prms, Rest2sqlParamNames.GETROW_PARAM_PKVAL, pkvalue);
                BeansPage<ABean> pg = CrudReaderApi.loadRecord0(prms, ctx, cursor, ABean.class);
                if(pg.getRows().size() > 0)
                    applyValuesToABeanFromABean(pg.getRows().get(0), bean, true);
            }

            return 0;
        }, user);


        return rows;
    }

    public static int deleteRecords(
            final List<Param> params,
            final List<Object> ids,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user) {
        UpdelexSQLDef sqlDef = cursor.getDeleteSqlDef();
        if (sqlDef == null)
            throw new BioError(String.format("For bio \"%s\" must be defined \"delete\" sql!", cursor.getBioCode()));
        int affected = context.execBatch((ctx) -> {
            int r = 0;
            SQLStoredProc cmd = ctx.createStoredProc();
            try {
                cmd.init(ctx.currentConnection(), sqlDef);
                for (Object id : ids) {
                    Param prm = Paramus.getParam(cmd.getParams(), Rest2sqlParamNames.DELETE_PARAM_PKVAL);
                    if (prm == null)
                        prm = cmd.getParams().size() > 0 ? cmd.getParams().get(0) : null;
                    if (prm != null) {
                        Paramus.setParamValue(params, prm.getName(), id, MetaTypeConverter.read(id.getClass()));
                        cmd.execSQL(params, user, true);
                        r++;
                    } else
                        throw new BioSQLException(String.format("ID Param not found in Delete sql of \"%s\"!", cursor.getBioCode()));
                }
            } finally {
                cmd.close();
            }
            return r;
        }, user);
        return affected;
    }

    public static void execSQL0(
            final Object params,
            final SQLContext context,
            final SQLDefinition cursor) {
        UpdelexSQLDef sqlDef = cursor.getExecSqlDef();
        if (sqlDef == null)
            throw new BioSQLException(String.format("For bio \"%s\" must be defined \"exec\" sql!", cursor.getBioCode()));
        Connection connTest = context.currentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursor.getBioCode()));

        SQLStoredProc cmd = context.createStoredProc();
        cmd.init(context.currentConnection(), sqlDef);
        cmd.execSQL(params, context.currentUser());
    }

    public static void execSQL(
            final Object params,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user) {
        context.execBatch((conn) -> {
            execSQL0(params, context, cursor);
        }, user);
    }

}
