package ru.bio4j.spring.database.commons;

import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.commons.utils.ABeans;
import ru.bio4j.spring.commons.utils.Sqls;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.errors.BioError;
import ru.bio4j.spring.model.transport.errors.BioSQLException;
import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.Total;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;
import ru.bio4j.spring.database.api.*;

import java.sql.Connection;
import java.util.*;
import java.util.function.LongSupplier;

import static ru.bio4j.spring.commons.utils.Reflex.fieldValue;

public class CrudReaderApi {
    protected final static LogWrapper LOG = LogWrapper.getLogger(CrudReaderApi.class);

    //TODO Перенести в настройки
    private static final int MAX_RECORDS_FETCH_LIMIT = 250000;

    private static void preparePkParamValue(final List<Param> params, final Field pkField) {
        Param pkParam = Paramus.getParam(params, Rest2sqlParamNames.GETROW_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setType(pkField.getMetaType());
            pkParam.setValue(newValue);
        }
        pkParam = Paramus.getParam(params, Rest2sqlParamNames.LOCATE_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setValue(newValue);
        }
        pkParam = Paramus.getParam(params, Rest2sqlParamNames.DELETE_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setValue(newValue);
        }
    }
    private static List<Param> preparePkParamValue(final Object pkValue, final Field pkField) {
        List<Param> params = new ArrayList<>();
        Object newValue = Converter.toType(pkValue, MetaTypeConverter.write(pkField.getMetaType()));
        Paramus.setParamValue(params, Rest2sqlParamNames.GETROW_PARAM_PKVAL, newValue);
        return params;
    }

    private static <T> BeansPage<T> readStoreData(
            final PreparePageParams prepareLoadPageResult,
            final SQLContext context,
            final SQLDefinition cursorDef,
            final CrudOptions crudOptions,
            final Class<T> beanType) {
        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        BeansPage result = new BeansPage();
        final long paginationPagesize = Paramus.paramValue(prepareLoadPageResult.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, long.class, 0L);
        result.setTotalCount(Paramus.paramValue(prepareLoadPageResult.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_TOTALCOUNT, long.class, 0L));
        result.setPaginationOffset(Paramus.paramValue(prepareLoadPageResult.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET, long.class, 0L));
        if(crudOptions.isAppendMetadata()) result.setMetadata(cursorDef.getFields());
        result.setRows(readStoreDataExt(prepareLoadPageResult.preparedParams, context, cursorDef, crudOptions, beanType));
        result.setTotals(prepareLoadPageResult.preparedTotals);
        result.setPaginationCount(result.getRows().size());
        result.setPaginationPageSize(paginationPagesize);
        result.setPaginationPage(paginationPagesize > 0 ? (int)Math.floor(result.getPaginationOffset() / paginationPagesize) + 1 : 0);
        if(result.getRows().size() < paginationPagesize) {
            result.setTotalCount(result.getPaginationOffset() + result.getRows().size());
            Paramus.setParamValue(prepareLoadPageResult.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_TOTALCOUNT, result.getTotalCount());
        }
        return result;
    }

    private static <T> BeansPage<T> readStoreData(
            final PreparePageParams prepareLoadPageResult,
            final SQLContext context,
            final SQLDefinition cursorDef,
            final Class<T> beanType) {
        return readStoreData(prepareLoadPageResult, context, cursorDef, CrudOptions.builder().build(), beanType);
    }

    private static long calcOffset(long locatedPos, long pageSize){
        long pg = ((long)((double)(locatedPos - 1) / (double)pageSize) + 1);
        return (pg - 1) * pageSize;
    }

    private static void _addTotal2Totals(List<Total> totals, Total total) {
        Total foundTotal = totals.stream().filter(f -> {
            return (total.getAggregate() == Total.Aggregate.COUNT && f.getAggregate() == Total.Aggregate.COUNT) ||
                    Strings.compare(total.getFieldName(), f.getFieldName(), true);
        }).findFirst().orElse(null);
        if(foundTotal != null) {
            foundTotal.setFact(total.getFact());
            foundTotal.setFieldType(total.getFact() != null ? total.getFact().getClass() : total.getFieldType());
        } else {
            totals.add(total);
        }
    }

    public static List<Total> calcTotalsRemote(
            final List<Total> totals,
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user) {
        context.execBatch((ctx) -> {
            SQLCursor c = ctx.createDynamicCursor();
            c.init(ctx.currentConnection(), cursor.getSelectSqlDef().getTotalsSql());
            ABean totalsBean = c.firstBean(params, user, ABean.class);
            for (Total total : totals) {
                _addTotal2Totals(totals, total);
                total.setFact(ABeans.extractAttrFromBean(totalsBean,
                        total.getAggregate() == Total.Aggregate.COUNT ? Total.TOTALCOUNT_FIELD_NAME : total.getFieldName(),
                        total.getFieldType(), null));
            }
        }, user);
        return totals;
    }

    /***
     * Выполняет запрос в текущей сессии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @param crudOptions
     * @param beanType
     * @return возвращает страницу
     *
     */
    public static <T> BeansPage<T> loadPage0(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final List<Total> totals,
            final SQLContext context,
            final SQLDefinition cursor,
            final CrudOptions crudOptions,
            final Class<T> beanType) {
        PreparePageParams prepareLoadPageResult = _prepareLoadPageParams(params, filter, sort, totals, context, cursor, crudOptions.isForceCalcCount());
        return readStoreData(prepareLoadPageResult, context, cursor, crudOptions, beanType);
    }

    /***
     * Выполняет запрос в новой сессиии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @param user
     * @param crudOptions
     * @param beanType
     * @return возвращает страницу
     *
     */
    public static <T> BeansPage<T> loadPage(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final List<Total> totals,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user,
            final CrudOptions crudOptions,
            final Class<T> beanType) {
        return context.execBatch((conn) -> {
            return loadPage0(params, filter, sort, totals, context, cursor, crudOptions, beanType);
        }, user);
    }

    private static <T> void _processCurrentRecordOnTotals(final List<Total> pageTotals, final T bean, final Total total) {
        if(total.getAggregate() != Total.Aggregate.COUNT) {
            String field = total.getFieldName();
            double value;
            if (bean instanceof HashMap)
                value = ABeans.extractAttrFromBean((Map) bean, field, double.class, null);
            else
                value = fieldValue(bean, field, double.class);
            Total rsTotal = pageTotals.stream().filter(t -> Strings.compare(t.getFieldName(), total.getFieldName(), true)).findFirst().orElse(null);
            if(rsTotal == null) {
                rsTotal = Total.builder()
                        .fieldName(total.getFieldName())
                        .aggrigate(total.getAggregate())
                        .fieldType(total.getFieldType())
                        .fact(Converter.toType(0D, total.getFieldType()))
                        .build();
            }
            double newValue = Converter.toType(rsTotal.getFact(), double.class) + value;
            rsTotal.setFact(Converter.toType(newValue, total.getFieldType()));
        } else {
            Total rsTotal = pageTotals.stream().filter(t -> t.getAggregate() == Total.Aggregate.COUNT).findFirst().orElse(null);
            if(rsTotal == null) {
                rsTotal = Total.builder().fieldName("*").aggrigate(Total.Aggregate.COUNT).fieldType(long.class).fact(0L).build();
                pageTotals.add(rsTotal);
            }
            rsTotal.setFact(Converter.toType(rsTotal.getFact(), long.class) + 1L);
        }
    }

    public static <T> List<Total> calcTotals(final List<T> pageData, final List<Total> totals) {
        PreparePageParams preparePageParams = new PreparePageParams();
        _initPreparedTotals(preparePageParams, totals);
        for (T bean : pageData){
            for(Total total : preparePageParams.preparedTotals)
                _processCurrentRecordOnTotals(preparePageParams.preparedTotals, bean, total);
        }
        return preparePageParams.preparedTotals;
    }
    private static <T> BeansPage<T> _calcTotals(final BeansPage<T> page, final List<Total> totals) {
        PreparePageParams preparePageParams = new PreparePageParams();
        preparePageParams.preparedTotals = page.getTotals();
        _initPreparedTotals(preparePageParams, totals);
        for (T bean : page.getRows()){
            for(Total total : preparePageParams.preparedTotals)
                _processCurrentRecordOnTotals(preparePageParams.preparedTotals, bean, total);
        }
        page.setTotals(preparePageParams.preparedTotals);
        Total couner = page.getTotals().stream().filter(t -> t.getAggregate() == Total.Aggregate.COUNT).findFirst().orElse(null);
        page.setTotalCount(couner != null ? Converter.toType(couner.getFact(), long.class) : 0L);
        return page;
    }

    /***
     * Выполняет запрос в текущей сессии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @return все записи
     *
     */
    public static <T> BeansPage<T> loadAll0(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final List<Total> totals,
            final SQLContext context,
            final SQLDefinition cursor,
            final Class<T> beanType) {
        PreparePageParams prepareLoadPageResult = _prepareLoadAllParams(filter, sort, totals, context, cursor);
        prepareLoadPageResult.preparedParams = params;
        return _calcTotals(readStoreData(prepareLoadPageResult, context, cursor, beanType), totals);
    }

    /***
     * Выпоняет запрос в новой сессии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @param user
     * @return все записи
     *
     */
    public static <T> BeansPage<T> loadAll(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final List<Total> totals,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        BeansPage result = context.execBatch((conn) -> {
            return loadAll0(params, filter, sort, totals, context, cursor, beanType);
        }, user);
        return result;
    }


    public static <T> BeansPage<T> loadRecord(final List<Param> params, final SQLContext context, final SQLDefinition cursor, final User user, final Class<T> beanType) {
        BeansPage result = context.execBatch((conn) -> {
            return loadRecord0(params, context, cursor, beanType);
        }, user);
        return result;
    }

    /***
     * Выполняет запрос в текущей сессии
     * @param params
     * @param context
     * @param cursor
     * @return первую запись
     *
     */
    public static <T> BeansPage<T> loadRecord0(final List<Param> params, final SQLContext context, final SQLDefinition cursor, final Class<T> beanType) {
        Connection connTest = context.currentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This method can be used only in SQLAction of execBatch!", cursor.getBioCode()));

        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioSQLException(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        preparePkParamValue(params, pkField);

        return readStoreData(new PreparePageParams(params), context, cursor, beanType);
    }

    /***
     * Выполняет запрос в текущей сессии
     * @param pkValue
     * @param context
     * @param cursor
     * @return первую запись
     *
     */
    public static <T> BeansPage<T> loadRecord0(final Object pkValue, final SQLContext context, final SQLDefinition cursor, final Class<T> beanType) {
        Connection connTest = context.currentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This method can be used only in SQLAction of execBatch!", cursor.getBioCode()));

        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        List<Param> params = preparePkParamValue(pkValue, pkField);

        return readStoreData(new PreparePageParams(params), context, cursor, beanType);
    }

    /***
     * Выполняет запрос в текущей сессии
     * @param params
     * @param context
     * @param cursor
     * @return первую запись
     *
     */
    public static <T> List<T> loadRecord0Ext(final List<Param> params, final SQLContext context, final SQLDefinition cursor, final Class<T> beanType) {
        Connection connTest = context.currentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This method can be used only in SQLAction of execBatch!", cursor.getBioCode()));

        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioSQLException(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        preparePkParamValue(params, pkField);

        return readStoreDataExt(params, context, cursor, beanType);
    }

    /***
     * Выполняет запрос в текущей сессии
     * @param pkValue
     * @param context
     * @param cursor
     * @return первую запись
     *
     */
    public static <T> List<T> loadRecord0Ext(final Object pkValue, final SQLContext context, final SQLDefinition cursor, final Class<T> beanType) {
        Connection connTest = context.currentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This method can be used only in SQLAction of execBatch!", cursor.getBioCode()));

        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        List<Param> params = preparePkParamValue(pkValue, pkField);

        return readStoreDataExt(params, context, cursor, beanType);
    }

    private static <T> List<T> readStoreDataExt(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursorDef,
            final CrudOptions crudOptions,
            final Class<T> beanType) {

        if (context.currentConnection() == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursorDef.getBioCode()));

        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        List<T> result = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        List<Param> prms = params;
        final LongSupplier limitRecordsToRead = () -> {
            if (crudOptions.getRecordsLimit() > 0)
                return crudOptions.getRecordsLimit();
            return MAX_RECORDS_FETCH_LIMIT;
        };

        //todo: сделать wrapper который накладывает лимит выбранных записей из CrudOptions.getRecordsLimit если нет пагинации
        context.createDynamicCursor()
                .init(context.currentConnection(), cursorDef.getSelectSqlDef())
                .fetch(prms, context.currentUser(), rs -> {
                    if (rs.isFirstRow()) {
                        long estimatedTime = System.currentTimeMillis() - startTime;
                        LOG.debug("Cursor \"{}\" opened in {} secs!!!", cursorDef.getBioCode(), Double.toString(estimatedTime / 1000));
                    }
                    T bean;
                    if(beanType == ABean.class)
                        bean = (T)DbUtils.createABeanFromReader(cursorDef.getFields(), rs);
                    else
                        bean = DbUtils.createBeanFromReader(cursorDef.getFields(), rs, beanType);
                    result.add(bean);
                    if (result.size() >= limitRecordsToRead.getAsLong())
                        return false;
                    return true;
                });
        LOG.debug("Cursor \"{}\" fetched! {} - records loaded.", cursorDef.getBioCode(), result.size());
        return result;
    }

    private static <T> List<T> readStoreDataExt(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursorDef,
            final Class<T> beanType) {
        return readStoreDataExt(params, context, cursorDef, CrudOptions.builder().build(), beanType);
    }

    private static class PreparePageParams {
        private List<Param> preparedParams;
        private List<Total> preparedTotals;

        public PreparePageParams() {

        }
        public PreparePageParams(List<Param> params, List<Total> totals) {
            preparedParams = params;
            preparedTotals = totals;
        }
        public PreparePageParams(List<Param> params) {
            preparedParams = params;
        }
    }

    private static void _initPreparedTotals(PreparePageParams preparePageParams, List<Total> totals) {
        if(preparePageParams.preparedTotals == null)
            preparePageParams.preparedTotals = new ArrayList<>();

        if(totals != null) {
            for (Total total : totals)
                _addTotal2Totals(preparePageParams.preparedTotals, total);
        }
    }

    private static void _wrapSqlDefBySorter(final List<Sort> sort, final SQLDefinition cursor, final SQLContext context) {
        List<Sort> localSort = sort != null ? sort : new ArrayList<>();
        if (localSort.size() == 0 && cursor.getSelectSqlDef() != null && cursor.getSelectSqlDef().getDefaultSort() != null)
            localSort.addAll(cursor.getSelectSqlDef().getDefaultSort());
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), localSort, cursor.getSelectSqlDef().getFields()));
    }

    private static PreparePageParams _prepareLoadPageParams(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final List<Total> totals,
            final SQLContext context,
            final SQLDefinition cursor,
            final boolean forceCalcCount) {
        Connection connTest = context.currentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursor.getBioCode()));

        PreparePageParams result = new PreparePageParams(Paramus.createParams(params));

        final Object location = Paramus.paramValue(result.preparedParams, Rest2sqlParamNames.LOCATE_PARAM_PKVAL, java.lang.Object.class, null);

        final long paginationPagesize = Paramus.paramValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, int.class, 50);
        if(Paramus.getParam(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT) == null)
            Paramus.setParamValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, paginationPagesize);
        final long actualPageSize = Paramus.paramValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, long.class, 0L);

        boolean gotoLastPage = Strings.compare(Paramus.paramValueAsString(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_PAGE), "last", true) ||
                Strings.compare(Paramus.paramValueAsString(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET), "last", true);
        if(gotoLastPage)
            Paramus.setParamValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET, Sqls.UNKNOWN_RECS_TOTAL + 1 - actualPageSize);

        final long paginationOffset = Paramus.paramValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET, int.class, 0);
        if(Paramus.getParam(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET) == null)
            Paramus.setParamValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET, paginationOffset);

        final String paginationTotalcountStr = Paramus.paramValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_TOTALCOUNT, String.class, null);
        final long paginationTotalcount = Strings.isNullOrEmpty(paginationTotalcountStr) ? Sqls.UNKNOWN_RECS_TOTAL : Converter.toType(paginationTotalcountStr, long.class);

        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter, cursor.getSelectSqlDef().getFields()));

        _initPreparedTotals(result, totals);

        boolean calcTotals = result.preparedTotals.stream().anyMatch(t -> t.getAggregate() != Total.Aggregate.UNDEFINED);
        if(!result.preparedTotals.stream().anyMatch(t -> t.getAggregate() == Total.Aggregate.COUNT))
            result.preparedTotals.add(Total.builder().fieldName("*").fieldType(long.class).aggrigate(Total.Aggregate.COUNT).fact(paginationTotalcount).build());
        cursor.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), result.preparedTotals, cursor.getSelectSqlDef().getFields()));

        _wrapSqlDefBySorter(sort, cursor, context);

        if (location != null) {
            Field pkField = cursor.getSelectSqlDef().findPk();
            if (pkField == null)
                throw new BioSQLException(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
            cursor.getSelectSqlDef().setLocateSql(context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
            preparePkParamValue(result.preparedParams, pkField);
        }
        if (paginationPagesize > 0)
            cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));

        long factOffset = paginationOffset;
        Total totalCount = result.preparedTotals.stream().filter(t -> t.getAggregate() == Total.Aggregate.COUNT).findFirst().orElse(Total.builder().fact(paginationTotalcount).build());
        gotoLastPage = paginationOffset == (Sqls.UNKNOWN_RECS_TOTAL - paginationPagesize + 1);
        if (calcTotals || forceCalcCount || gotoLastPage) {
            result.preparedTotals = calcTotalsRemote(result.preparedTotals, result.preparedParams, context, cursor, context.currentUser());
            totalCount = result.preparedTotals.stream().filter(t -> t.getAggregate() == Total.Aggregate.COUNT).findFirst().orElse(Total.builder().fact(paginationTotalcount).build());
            if(gotoLastPage) {
                factOffset = (long) Math.floor((long) totalCount.getFact() / paginationPagesize) * paginationPagesize;
                if(factOffset == (long)totalCount.getFact())
                    factOffset -= paginationPagesize;
            }
            LOG.debug("Count of records of cursor \"{}\" - {}!!!", cursor.getBioCode(), totalCount.getFact());
        }
        Paramus.setParamValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET, factOffset);
        Paramus.setParamValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_TOTALCOUNT, totalCount.getFact());
        long locFactOffset = Paramus.paramValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET, long.class, 0L);
        if (location != null) {
            LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cursor.getBioCode(), location);
            int locatedPos = context.createDynamicCursor()
                    .init(context.currentConnection(), cursor.getSelectSqlDef().getLocateSql(), cursor.getSelectSqlDef().getParamDeclaration())
                    .scalar(result.preparedParams, context.currentUser(), int.class, -1);
            if (locatedPos >= 0) {
                locFactOffset = calcOffset(locatedPos, paginationPagesize);
                LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cursor.getBioCode(), location, locatedPos, locFactOffset);
            } else {
                LOG.debug("Cursor \"{}\" failed location to [{}] record by pk!!!", cursor.getBioCode(), location);
            }
        }
        Paramus.setParamValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_OFFSET, locFactOffset);
        Paramus.setParamValue(result.preparedParams, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, paginationPagesize);
        if(params != null) {
            Paramus.applyParams(params, result.preparedParams, false, true);
            result.preparedParams = params;
        }
        return result;
    }

    private static PreparePageParams _prepareLoadAllParams(
            final Filter filter,
            final List<Sort> sort,
            final List<Total> totals,
            final SQLContext context,
            final SQLDefinition cursor) {
        Connection connTest = context.currentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursor.getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter, cursor.getSelectSqlDef().getFields()));

        _wrapSqlDefBySorter(sort, cursor, context);

        PreparePageParams result = new PreparePageParams();
        _initPreparedTotals(result, totals);
        if(!result.preparedTotals.stream().anyMatch(t -> t.getAggregate() == Total.Aggregate.COUNT))
            result.preparedTotals.add(Total.builder().fieldName("*").fieldType(long.class).aggrigate(Total.Aggregate.COUNT).fact(0L).build());
        return result;
    }

    /***
     * Выполняет запрос в текущей сессии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @param beanType
     * @param <T>
     * @return страницу
     *
     */
    public static <T> List<T> loadPage0Ext(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final SQLDefinition cursor,
            final Class<T> beanType) {
        PreparePageParams prepareLoadPageResult = _prepareLoadPageParams(params, filter, sort, null, context, cursor, false);
        return readStoreDataExt(prepareLoadPageResult.preparedParams, context, cursor, beanType);
    }

    /***
     * Выполняет запрос в новой сессии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @param user
     * @param beanType
     * @param <T>
     * @return страницу
     *
     */
    public static <T> List<T> loadPageExt(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        List<T> result = context.execBatch((conn) -> {
            return loadPage0Ext(params, filter, sort, context, cursor, beanType);
        }, user);
        return result;
    }

    /***
     * Выполняет запрос в текущей сессии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @param beanType
     * @param <T>
     * @return все записи
     *
     */
    public static <T> List<T> loadAll0Ext(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final SQLDefinition cursor,
            final Class<T> beanType) {
        _prepareLoadAllParams(filter, sort, null, context, cursor);
        return readStoreDataExt(params, context, cursor, beanType);
    }

    /***
     * Выполняет запрос в новой сессии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @param user
     * @param beanType
     * @param <T>
     * @return все записи
     *
     */
    public static <T> List<T> loadAllExt(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        List<T> result = context.execBatch((conn) -> {
            return loadAll0Ext(params, filter, sort, context, cursor, beanType);
        }, user);
        return result;
    }

    public static <T> List<T> loadRecordExt(
            final List<Param> params,
            final SQLContext context, final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        preparePkParamValue(params, pkField);
        List<T> result = context.execBatch((conn) -> {
            return readStoreDataExt(params, context, cursor, beanType);
        }, user);
        return result;
    }

    public static <T> List<T> loadRecordExt(
            final Object pkValue,
            final SQLContext context, final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        List<Param> params = preparePkParamValue(pkValue, pkField);
        List<T> result = context.execBatch((conn) -> {
            return readStoreDataExt(params, context, cursor, beanType);
        }, user);
        return result;
    }

    public static <T> T loadFirstRecordExt(
            final List<Param> params,
            final SQLContext context, final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        cursor.getSelectSqlDef().setPreparedSql(cursor.getSelectSqlDef().getSql());
        List<T> result = context.execBatch((conn) -> {
            return readStoreDataExt(params, context, cursor, CrudOptions.builder().recordsLimit(1).build(), beanType);
        }, user);
        return result.size() > 0 ? result.get(0) : null;
    }

    public static <T> T loadFirstRecordExt(
            final SQLContext context, final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        cursor.getSelectSqlDef().setPreparedSql(cursor.getSelectSqlDef().getSql());
        List<T> result = context.execBatch((conn) -> {
            return readStoreDataExt(null, context, cursor, CrudOptions.builder().recordsLimit(1).build(), beanType);
        }, user);
        return result.size() > 0 ? result.get(0) : null;
    }

    public static <T> T loadFirstRecordExt(
            final Filter filter,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        List<T> result = context.execBatch((conn) -> {
            return loadAll0Ext(null, filter, null, context, cursor, beanType);
        }, user);
        return result.stream().findFirst().orElse(null);
    }

    public static <T> T loadFirstRecord0Ext(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursor,
            final Class<T> beanType) {
        cursor.getSelectSqlDef().setPreparedSql(cursor.getSelectSqlDef().getSql());
        List<T> result = readStoreDataExt(params, context, cursor, CrudOptions.builder().recordsLimit(1).build(), beanType);
        return result.stream().findFirst().orElse(null);
    }

    public static <T> T loadFirstRecord0Ext(
            final SQLContext context,
            final SQLDefinition cursor,
            final Class<T> beanType) {
        cursor.getSelectSqlDef().setPreparedSql(cursor.getSelectSqlDef().getSql());
        List<T> result = readStoreDataExt(null, context, cursor, CrudOptions.builder().recordsLimit(1).build(), beanType);
        return result.stream().findFirst().orElse(null);
    }

    public static <T> T loadFirstRecord0Ext(
            final Filter filter,
            final SQLContext context,
            final SQLDefinition cursor,
            final Class<T> beanType) {
        List<T> result = loadAll0Ext(null, filter, null, context, cursor, beanType);
        return result.stream().findFirst().orElse(null);
    }

    public static StringBuilder loadJson(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user) {
        StringBuilder result = context.execBatch((ctx) -> {
            final StringBuilder r = new StringBuilder();

            ctx.createDynamicCursor()
                    .init(ctx.currentConnection(), cursor.getSelectSqlDef().getSql(), cursor.getSelectSqlDef().getParamDeclaration())
                    .fetch(params, user, rs -> {
                        List<Object> values = rs.getValues();
                        for (Object val : values)
                            r.append(val);
                        return true;
                    });
            return r;
        }, user);
        return result;
    }

    public static <T> T selectScalar0(
            final Object params,
            final SQLContext context,
            final SQLDefinition sqlDefinition,
            final String fieldName,
            final Class<T> clazz,
            final T defaultValue) {
        return DbUtils.processSelectScalar0(params, context, sqlDefinition, fieldName, clazz, defaultValue);
    }
    public static <T> T selectScalar0(
            final Object params,
            final SQLContext context,
            final SQLDefinition sqlDefinition,
            final Class<T> clazz,
            final T defaultValue) {
        return DbUtils.processSelectScalar0(params, context, sqlDefinition, clazz, defaultValue);
    }

    public static <T> T selectScalar(
            final Object params,
            final SQLContext context,
            final SQLDefinition sqlDefinition,
            final String fieldName,
            final Class<T> clazz,
            final T defaultValue,
            final User user) {
        return DbUtils.processSelectScalar(user, params, context, sqlDefinition, fieldName, clazz, defaultValue);
    }
    public static <T> T selectScalar(
            final Object params,
            final SQLContext context,
            final SQLDefinition sqlDefinition,
            final Class<T> clazz,
            final T defaultValue,
            final User user) {
        return DbUtils.processSelectScalar(user, params, context, sqlDefinition, clazz, defaultValue);
    }

    private static interface StoreDataReadPerformer {
        <T> List<T> readData(
                final List<Param> params,
                final SQLContext context,
                final SQLDefinition cursorDef,
                final Class<T> beanType,
                final int recordsLimit);
    }

}
