package ru.bio4j.spring.dba;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
//import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Jecksons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.commons.CrudReaderApi;
import ru.bio4j.ng.database.commons.CrudWriterApi;
import ru.bio4j.ng.database.commons.DbUtils;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.model.transport.RestParamNames;
import ru.bio4j.ng.model.transport.BioQueryParams;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class RestApiAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(RestApiAdapter.class);

    private static List<Param>_extractBioParams(final BioQueryParams queryParams) {
        Paramus.setQueryParamsToBioParams(queryParams);
        return queryParams.bioParams;
    }

    private static List<Param>_extractBioParams(final HttpServletRequest request) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        return _extractBioParams(queryParams);
    }

    private void prepareSQL(SQLDefinitionImpl sqlDefinition) {
        SQLContext context = this.getSQLContext();
        context.execBatch((ctx) -> {
            UpdelexSQLDef def = sqlDefinition.getUpdateSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
            def = sqlDefinition.getDeleteSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
            def = sqlDefinition.getExecSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
        }, null);
    }

    public SQLDefinitionImpl getSQLDefinition(String bioCode) {
        SQLDefinitionImpl cursor = CursorParser.pars(bundleContext(), bioCode);
        if(cursor == null)
            throw Utl.wrapErrorAsRuntimeException(String.format("Cursor \"%s\" not found in service \"%s\"!", bioCode, this.getClass().getName()));
        prepareSQL(cursor);
        return cursor;
    }

    public static ABeanPage loadPage(
            final OdacService module,
            final String bioCode,
            final Object params,
            final User user,
            final FilterAndSorter filterAndSorter,
            final boolean forceCalcCount
            ) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAll(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user);
        else
            return CrudReaderApi.loadPage(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, forceCalcCount, user);
    }
    public static ABeanPage loadAll(
            final OdacService module,
            final String bioCode,
            final Object params,
            final User user,
            final FilterAndSorter filterAndSorter,
            final boolean forceCalcCount
    ) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudReaderApi.loadAll(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user);
    }

    public static ABeanPage loadPage(
            final OdacService module,
            final String bioCode,
            final Object params,
            final User user) {
        return loadPage(module, bioCode, params, user, null, false);
    }
    public static ABeanPage loadAll(
            final OdacService module,
            final String bioCode,
            final Object params,
            final User user) {
        return loadAll(module, bioCode, params, user, null, false);
    }

    public static ABeanPage loadPage(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        boolean forceCalcCount = Converter.toType(queryParams.gcount, boolean.class);
        return loadPage(module, bioCode, params, user, fs, forceCalcCount);
    }
    public static ABeanPage loadAll(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        boolean forceCalcCount = Converter.toType(queryParams.gcount, boolean.class);
        return loadAll(module, bioCode, params, user, fs, forceCalcCount);
    }

    public static ABean calcTotalCount(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        ABean rslt = new ABean();
        ru.bio4j.ng.model.transport.jstore.filter.Filter filter = fs != null ? fs.getFilter() : null;
        sqlDefinition.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(sqlDefinition.getSelectSqlDef().getPreparedSql(), filter, sqlDefinition.getSelectSqlDef().getFields()));
        sqlDefinition.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(sqlDefinition.getSelectSqlDef().getPreparedSql()));
        long totalCount = CrudReaderApi.calcTotalCount(params, context, sqlDefinition, user);
        rslt.put("totalCount", totalCount);
        return rslt;
    }

    public static <T> List<T> loadPageExt(
            final OdacService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
        else
            return CrudReaderApi.loadPageExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
    }
    public static <T> List<T> loadAllExt(
            final OdacService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
    }

    public static <T> List<T> loadPageExt(
            final OdacService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        User user = null;
        Param usrParam  = Paramus.getParam(prms, "p_userbean");
        if(usrParam != null)
            user = (User)usrParam.getValue();

        return loadPageExt(module, bioCode, params, user, beanType, filterAndSorter);
    }
    public static <T> List<T> loadAllExt(
            final OdacService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        User user = null;
        Param usrParam  = Paramus.getParam(prms, "p_userbean");
        if(usrParam != null)
            user = (User)usrParam.getValue();

        return loadAllExt(module, bioCode, params, user, beanType, filterAndSorter);
    }

    public static <T> List<T> loadPageExt(
            final OdacService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) {
        return loadPageExt(module, bioCode, params, user, beanType, null);
    }
    public static <T> List<T> loadAllExt(
            final OdacService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) {
        return loadAllExt(module, bioCode, params, user, beanType, null);
    }

    public static <T> List<T> loadPageExt(
            final OdacService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType) {
        return loadPageExt(module, bioCode, params, beanType, null);
    }
    public static <T> List<T> loadAllExt(
            final OdacService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType) {
        return loadAllExt(module, bioCode, params, beanType, null);
    }

    public static <T> T loadFirstBean(
            final String bioCode,
            final List<Param> params,
            final OdacService module,
            final User usr,
            final Class<T> beanType) {
        final SQLContext context = module.getSQLContext();
        final SQLDefinition cursorDef = module.getSQLDefinition(bioCode);
        return CrudReaderApi.loadFirstRecordExt(params, context, cursorDef, usr, beanType);
    }

    public static HSSFWorkbook loadToExcel(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDef = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        return context.execBatch((ctx) -> {
            FilterAndSorter fs = createFilterAndSorter(queryParams);
            return CrudReaderApi.toExcel(params, fs.getFilter(), fs.getSorter(), ctx, sqlDef);
        }, user);

    }

    public static <T> List<T> loadPageExt(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module,
            final Class<T> beanType) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        return loadPageExt(module, bioCode, params, user, beanType, fs);
    }

    public static FilterAndSorter createFilterAndSorter(final BioQueryParams queryParams) {
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData)) {
            try {
                fs = Jecksons.getInstance().decodeFilterAndSorter(queryParams.jsonData);
            } catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.warn(String.format("Ошибка при восстановлении объекта %s. Json: %s", FilterAndSorter.class.getSimpleName(), queryParams.jsonData), e);
            }
        }
        if(fs == null) {
            fs = new FilterAndSorter();
            if(queryParams.sort != null) {
                fs.setSorter(new ArrayList<>());
                fs.getSorter().addAll(queryParams.sort);
            }
            fs.setFilter(queryParams.filter);
        }
        return fs;
    }

    public static <T> List<T> loadAllExt(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module,
            final Class<T> beanType) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        return loadAllExt(module, bioCode, params, user, beanType, fs);
    }

    public static ABean getMetadata(
            final String bioCode,
            OdacService module) {
        ABean rslt = new ABean();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        StoreMetadata metadata = new StoreMetadata();
        metadata.setReadonly(sqlDefinition.getReadOnly());
        metadata.setMultiSelection(sqlDefinition.getMultiSelection());
        List<Field> fields = sqlDefinition.getFields();
        metadata.setFields(fields);
        rslt.put("dataset", metadata);
        if(sqlDefinition.getUpdateSqlDef() != null) {
            ABean createUpdateObject = new ABean();
            for(Param p : sqlDefinition.getUpdateSqlDef().getParamDeclaration()){
                createUpdateObject.put(DbUtils.cutParamPrefix(p.getName()), p.getType().name());
            }
            rslt.put("createUpdateObject", createUpdateObject);
        }
        return rslt;
    }

    public static StoreMetadata getMetadataOld(
            final String bioCode,
            OdacService module) {

        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        StoreMetadata metadata = new StoreMetadata();
        metadata.setReadonly(sqlDefinition.getReadOnly());
        metadata.setMultiSelection(sqlDefinition.getMultiSelection());
        List<Field> fields = sqlDefinition.getFields();
        metadata.setFields(fields);
        return metadata;
    }

    public static ABean loadBean(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module,
            final Object id) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        if(id != null) {
            Paramus.setParamValue(params, RestParamNames.GETROW_PARAM_PKVAL, id, MetaTypeConverter.read(id.getClass()));
            ABeanPage rslt = CrudReaderApi.loadRecord(params, context, sqlDefinition, user);
            if (rslt.getRows() != null && rslt.getRows().size() > 0)
                return rslt.getRows().get(0);
        }
        return null;
    }

    public static StringBuilder loadJson(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        StringBuilder rslt = CrudReaderApi.loadJson(params, context, sqlDefinition, user);
        return rslt;
    }

    public static List<ABean> saveBeans(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module,
            final List<ABean> rows) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        List<ABean> rslt = CrudWriterApi.saveRecords(params, rows, context, sqlDefinition, user);
        return rslt;
    }
    public static ABean deleteBeans(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module,
            final List<Object> ids) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        int affected = CrudWriterApi.deleteRecords(params, ids, context, sqlDefinition, user);
        ABean rslt = new ABean();
        rslt.put("deleted", affected);
        return rslt;
    }

    public static void exec(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public static void exec(
            final String bioCode,
            final Object params,
            final OdacService module,
            final User user) {
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public static <T> T selectScalar(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module,
            final Class<T> clazz,
            final T defaultValue) {
        final List<Param> params = _extractBioParams(request);
        final User user = ((WrappedRequest)request).getUser();
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudReaderApi.selectScalar(params, context, sqlDefinition, clazz, defaultValue, user);
    }

    public static <T> T selectScalar(
            final String bioCode,
            final Object params,
            final OdacService module,
            final Class<T> clazz,
            final T defaultValue,
            final User user) {
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudReaderApi.selectScalar(params, context, sqlDefinition, clazz, defaultValue, user);
    }

    public static void execBatch(final SQLContext context, final SQLActionVoid0 action, final User user) {
        context.execBatch(action, user);
    }

    public static <T> T execBatch(final SQLContext context, final SQLActionScalar0<T> action, final User user) {
        return context.execBatch(action, user);
    }

    public static <P, T> T execBatch(final SQLContext context, final SQLActionScalar1<P, T> action, P param, final User user) {
        return context.execBatch(action, param, user);
    }

    public static void execLocal(
            final SQLDefinition sqlDefinition,
            final Object params,
            final SQLContext context) {
        CrudWriterApi.execSQL0(params, context, sqlDefinition);
    }

    public static <T> List<T> loadPage0Ext(
            final SQLDefinition sqlDefinition,
            final SQLContext context,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAll0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
        else
            return CrudReaderApi.loadPage0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
    }
    public static <T> List<T> loadAll0Ext(
            final SQLDefinition sqlDefinition,
            final SQLContext context,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        return CrudReaderApi.loadAll0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
    }


    public static void execForEach(
            final String bioCode,
            final HttpServletRequest request,
            final OdacService module) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

}
