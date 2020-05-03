package ru.bio4j.spring.dba;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.types.Paramus;
//import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.commons.types.WrappedRequest;
import ru.bio4j.spring.commons.utils.Jecksons;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.database.commons.*;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.StoreMetadata;
import ru.bio4j.spring.model.transport.jstore.Total;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DbaAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(DbaAdapter.class);

    @Autowired
    private SQLContext sqlContext;

    private static List<Param>_extractBioParams(final BioQueryParams queryParams) {
        Paramus.setQueryParamsToBioParams(queryParams);
        return queryParams.bioParams;
    }

    private static List<Param>_extractBioParams(final HttpServletRequest request) {
        final BioQueryParams queryParams = null; ///---((WrappedRequest)request).getBioQueryParams();
        return _extractBioParams(queryParams);
    }

    private void prepareSQL(SQLDefinition sqlDefinition) {
        SQLContext context = getSqlContext();
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

    public SQLContext getSqlContext() {
        return sqlContext;
    }

    public SQLDefinition getSQLDefinition(String bioCode) {
        SQLDefinition cursor = CursorParser.pars(bioCode);
        if(cursor == null)
            throw Utl.wrapErrorAsRuntimeException(String.format("Cursor \"%s\" not found in service \"%s\"!", bioCode, this.getClass().getName()));
        prepareSQL(cursor);
        return cursor;
    }

    public <T> BeansPage<T> loadPage(
            final String bioCode,
            final Object params,
            final User user,
            final FilterAndSorter filterAndSorter,
            final List<Total> totals,
            final CrudOptions crudOptions,
            final Class<T> beanType
            ) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAll(prms,
                    filterAndSorter != null ? filterAndSorter.getFilter() : null,
                    filterAndSorter != null ? filterAndSorter.getSorter() : null,
                    totals,
                    context, sqlDefinition, user, beanType);
        else
            return CrudReaderApi.loadPage(
                    prms,
                    filterAndSorter != null ? filterAndSorter.getFilter() : null,
                    filterAndSorter != null ? filterAndSorter.getSorter() : null,
                    totals, context, sqlDefinition, user, crudOptions, beanType);
    }
    public <T> BeansPage<T> loadAll(
            final String bioCode,
            final Object params,
            final User user,
            final FilterAndSorter filterAndSorter,
            final List<Total> totals,
            final Class<T> beanType
    ) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        return CrudReaderApi.loadAll(prms,
                filterAndSorter != null ? filterAndSorter.getFilter() : null,
                filterAndSorter != null ? filterAndSorter.getSorter() : null,
                totals, context, sqlDefinition, user, beanType);
    }

    public <T> BeansPage<T> loadPage(
            final String bioCode,
            final Object params,
            final User user,
            final List<Total> totals,
            final Class<T> beanType) {
        return loadPage(bioCode, params, user, null, totals, CrudOptions.builder().build(), beanType);
    }
    public <T> BeansPage<T> loadAll(
            final String bioCode,
            final Object params,
            final User user,
            final List<Total> totals,
            final Class<T> beanType) {
        return loadAll(bioCode, params, user, totals, beanType);
    }

    public <T> BeansPage<T> loadPage(
            final String bioCode,
            final HttpServletRequest request,
            final List<Total> totals,
            final Class<T> beanType) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        boolean forceCalcCount = Converter.toType(queryParams.gcount, boolean.class);
        return loadPage(bioCode, params, user, fs, totals, CrudOptions.builder().forceCalcCount(forceCalcCount).build(), beanType);
    }
    public <T> BeansPage<T> loadAll(
            final String bioCode,
            final HttpServletRequest request,
            final List<Total> totals,
            final Class<T> beanType) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        return loadAll(bioCode, params, user, fs, totals, beanType);
    }

    public ABean calcTotalCount(
            final String bioCode,
            final HttpServletRequest request) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        ABean rslt = new ABean();
        Filter filter = fs != null ? fs.getFilter() : null;
        sqlDefinition.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(sqlDefinition.getSelectSqlDef().getPreparedSql(), filter, sqlDefinition.getSelectSqlDef().getFields()));
        Total countDef = Total.builder().fieldName("*").fieldType(long.class).aggrigate(Total.Aggrigate.COUNT).fact(0L).build();
        sqlDefinition.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(sqlDefinition.getSelectSqlDef().getPreparedSql(), Arrays.asList(countDef), sqlDefinition.getSelectSqlDef().getFields()));
        List<Total> countFact = CrudReaderApi.calcTotalsRemote(Arrays.asList(countDef), params, context, sqlDefinition, user);
        rslt.put("totalCount", countFact.stream().findFirst().get().getFact());
        return rslt;
    }

    public <T> List<T> loadPageExt(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
        else
            return CrudReaderApi.loadPageExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
    }
    public <T> List<T> loadAllExt(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
    }

    public <T> List<T> loadPageExt(
            final String bioCode,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        User user = null;
        Param usrParam  = Paramus.getParam(prms, "p_userbean");
        if(usrParam != null)
            user = (User)usrParam.getValue();

        return loadPageExt(bioCode, params, user, beanType, filterAndSorter);
    }
    public <T> List<T> loadAllExt(
            final String bioCode,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        User user = null;
        Param usrParam  = Paramus.getParam(prms, "p_userbean");
        if(usrParam != null)
            user = (User)usrParam.getValue();

        return loadAllExt(bioCode, params, user, beanType, filterAndSorter);
    }

    public <T> List<T> loadPageExt(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) {
        return loadPageExt(bioCode, params, user, beanType, null);
    }
    public <T> List<T> loadAllExt(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) {
        return loadAllExt(bioCode, params, user, beanType, null);
    }

    public <T> List<T> loadPageExt(
            final String bioCode,
            final Object params,
            final Class<T> beanType) {
        return loadPageExt(bioCode, params, beanType, null);
    }
    public <T> List<T> loadAllExt(
            final String bioCode,
            final Object params,
            final Class<T> beanType) {
        return loadAllExt(bioCode, params, beanType, null);
    }

    public <T> T loadFirstBean(
            final String bioCode,
            final List<Param> params,
            final User usr,
            final Class<T> beanType) {
        final SQLContext context = getSqlContext();
        final SQLDefinition cursorDef = CursorParser.pars(bioCode);
        return CrudReaderApi.loadFirstRecordExt(params, context, cursorDef, usr, beanType);
    }

    public <T> T loadFirstBean(
            final String bioCode,
            final Filter filter,
            final User usr,
            final Class<T> beanType) {
        final SQLContext context = getSqlContext();
        final SQLDefinition cursorDef = CursorParser.pars(bioCode);
        return CrudReaderApi.loadFirstRecordExt(filter, context, cursorDef, usr, beanType);
    }

    public HSSFWorkbook loadToExcel(
            final String bioCode,
            final HttpServletRequest request) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDef = CursorParser.pars(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        return context.execBatch((ctx) -> {
            FilterAndSorter fs = createFilterAndSorter(queryParams);
            return CrudReaderApi.toExcel(params, fs.getFilter(), fs.getSorter(), ctx, sqlDef);
        }, user);

    }

    public <T> List<T> loadPageExt(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> beanType) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        return loadPageExt(bioCode, params, user, beanType, fs);
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

    public <T> List<T> loadAllExt(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> beanType) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = createFilterAndSorter(queryParams);
        return loadAllExt(bioCode, params, user, beanType, fs);
    }

    public static ABean getMetadata(
            final String bioCode) {
        ABean rslt = new ABean();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
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

    public static StoreMetadata getMetadataOld(final String bioCode) {
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        StoreMetadata metadata = new StoreMetadata();
        metadata.setReadonly(sqlDefinition.getReadOnly());
        metadata.setMultiSelection(sqlDefinition.getMultiSelection());
        List<Field> fields = sqlDefinition.getFields();
        metadata.setFields(fields);
        return metadata;
    }

    public ABean loadBean(
            final String bioCode,
            final HttpServletRequest request,
            final Object id) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        if(id != null) {
            Paramus.setParamValue(params, RestParamNames.GETROW_PARAM_PKVAL, id, MetaTypeConverter.read(id.getClass()));
            List<ABean> rslt = CrudReaderApi.loadRecordExt(params, context, sqlDefinition, user, ABean.class);
            if (rslt.size() > 0)
                return rslt.get(0);
        }
        return null;
    }

    public StringBuilder loadJson(
            final String bioCode,
            final HttpServletRequest request) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        StringBuilder rslt = CrudReaderApi.loadJson(params, context, sqlDefinition, user);
        return rslt;
    }

    public List<ABean> saveBeans(
            final String bioCode,
            final HttpServletRequest request,
            final List<ABean> rows) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        List<ABean> rslt = CrudWriterApi.saveRecords(params, rows, context, sqlDefinition, user);
        return rslt;
    }
    public ABean deleteBeans(
            final String bioCode,
            final HttpServletRequest request,
            final List<Object> ids) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        int affected = CrudWriterApi.deleteRecords(params, ids, context, sqlDefinition, user);
        ABean rslt = new ABean();
        rslt.put("deleted", affected);
        return rslt;
    }

    public void exec(
            final String bioCode,
            final HttpServletRequest request) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public void exec(
            final String bioCode,
            final Object params,
            final User user) {
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public <T> T selectScalar(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> clazz,
            final T defaultValue) {
        final List<Param> params = _extractBioParams(request);
        final User user = ((WrappedRequest)request).getUser();
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        return CrudReaderApi.selectScalar(params, context, sqlDefinition, clazz, defaultValue, user);
    }

    public <T> T selectScalar(
            final String bioCode,
            final Object params,
            final Class<T> clazz,
            final T defaultValue,
            final User user) {
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
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


    public void execForEach(
            final String bioCode,
            final HttpServletRequest request) {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public <T> List<T> loadBean(
            final String bioCode,
            final Object id,
            final User user,
            final Class<T> beanType) {
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        if(id != null) {
            return CrudReaderApi.loadRecordExt(id, context, sqlDefinition, user, beanType);
        }
        return new ArrayList<>();
    }

    public void setBioParamToRequest(String paramName, Object paramValue, HttpServletRequest request) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        Class<?> forceType = paramValue != null ? paramValue.getClass() : null;
        if(forceType != null) {
            MetaType forceMetaType = MetaTypeConverter.read(forceType);
            Paramus.setParamValue(queryParams.bioParams, paramName, paramValue, forceMetaType);
        } else
            Paramus.setParamValue(queryParams.bioParams, paramName, paramValue);
    }

    public void setSorterToRequest(String fieldName, Sort.Direction direction, HttpServletRequest request) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        if(queryParams.sort == null)
            queryParams.sort = new ArrayList<>();
        queryParams.sort.clear();
        if(!Strings.isNullOrEmpty(fieldName)) {
            Sort newSort = new Sort();
            newSort.setFieldName(fieldName);
            newSort.setDirection(direction);
            queryParams.sort.add(newSort);
        }
    }

    public List<Param> setBeanToBioParams(ABean bean, List<Param> params) {
        if(params == null)
            params = new ArrayList<>();
        if(bean == null)
            return params;
        for(String key : bean.keySet())
            Paramus.setParamValue(params, key, bean.get(key));
        return params;
    }

    public void setBeanToRequest(ABean bean, HttpServletRequest request) {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        setBeanToBioParams(bean, queryParams.bioParams);
    }

    public boolean bioParamExistsInRequest(String paramName, HttpServletRequest request) {
        return ((WrappedRequest)request).bioQueryParamExists(paramName);
    }

    public <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType, T defaultValue) {
        return ((WrappedRequest)request).getBioQueryParam(paramName, paramType, defaultValue);
    }

    public <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType) {
        return  getBioParamFromRequest(paramName, request, paramType, null);
    }

}
