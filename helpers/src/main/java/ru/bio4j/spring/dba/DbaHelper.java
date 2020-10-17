package ru.bio4j.spring.dba;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.types.WrappedRequest;
import ru.bio4j.spring.commons.utils.Jecksons;
import ru.bio4j.spring.commons.utils.SrvcUtils;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.database.commons.CrudReaderApi;
import ru.bio4j.spring.database.commons.CrudWriterApi;
import ru.bio4j.spring.database.commons.CursorParser;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.errors.BioError;
import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.StoreMetadata;
import ru.bio4j.spring.model.transport.jstore.Total;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//import ru.bio4j.ng.commons.utils.Jsons;

/**
 * Адаптер для доступа к базе данных bio4j
 */
public class DbaHelper {
    private static final LogWrapper LOG = LogWrapper.getLogger(DbaHelper.class);

    private final SQLContext sqlContext;
    private final ExcelBuilder excelBuilder;
    private final CacheService cacheService;

    public DbaHelper(SQLContext sqlContext, ExcelBuilder excelBuilder, CacheService cacheService) {
        this.sqlContext = sqlContext;
        this.excelBuilder = excelBuilder;
        this.cacheService = cacheService;
    }

    private static List<Param> _extractBioParams(final BioQueryParams queryParams) {
        Paramus.setQueryParamsToBioParams(queryParams);
        return queryParams.bioParams;
    }

    private static FilterAndSorter _createFilterAndSorter(final BioQueryParams queryParams) {
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData)) {
            try {
                fs = Jecksons.getInstance().decodeFilterAndSorter(queryParams.jsonData);
            } catch (Exception e) {
                LOG.debug(String.format("Ошибка при восстановлении объекта %s. Json: %s", FilterAndSorter.class.getSimpleName(), queryParams.jsonData), e);
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

    public WrappedRequest wrappedRequest(final HttpServletRequest request) {
        return SrvcUtils.wrappedRequest(request);
    }

    private class  RequestParamsPack {
        private BioQueryParams queryParams;
        private List<Param> params;
        private SQLContext context;
        private SQLDefinition sqlDefinition;
        private FilterAndSorter filterAndSorter;
        private User user;
    }
    private RequestParamsPack _parsRequestPack(final String bioCode, final HttpServletRequest request) {
        RequestParamsPack result = new RequestParamsPack();
        result.queryParams = wrappedRequest(request).getBioQueryParams();
        result.params = _extractBioParams(result.queryParams);
        result.context = getSqlContext();
        result.sqlDefinition = CursorParser.pars(bioCode);
        result.filterAndSorter = _createFilterAndSorter(result.queryParams);
        result.user = wrappedRequest(request).getUser();
        return result;
    }
    
    private void prepareSQL(SQLDefinition sqlDefinition) {
        SQLContext context = getSqlContext();
        context.execBatch((conn) -> {
            UpdelexSQLDef def = sqlDefinition.getUpdateSqlDef();
            if (def != null) {
                StoredProgMetadata sp = context.prepareStoredProc(def.getPreparedSql(), conn, def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
            def = sqlDefinition.getDeleteSqlDef();
            if (def != null) {
                StoredProgMetadata sp = context.prepareStoredProc(def.getPreparedSql(), conn, def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
            def = sqlDefinition.getExecSqlDef();
            if (def != null) {
                StoredProgMetadata sp = context.prepareStoredProc(def.getPreparedSql(), conn, def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
        }, null);
    }

    /**
     * Возвращает текущий контекст соединения с базой данных
     */
    public SQLContext getSqlContext() {
        return sqlContext;
    }

    /**
     * Возвращает оопределение запроса по коду
     */
    public SQLDefinition getSQLDefinition(String bioCode) {
        SQLDefinition cursor = CursorParser.pars(bioCode);
        if(cursor == null)
            throw Utl.wrapErrorAsRuntimeException(String.format("Cursor \"%s\" not found in service \"%s\"!", bioCode, this.getClass().getName()));
        prepareSQL(cursor);
        return cursor;
    }

    /**
     * Выполняет запрос по коду и возвращает структуру @BeansPage
     * Набор содержит запрошенную страницу.
     * Параметры пагинации определены в @HttpParamMap
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param params параметры запроса
     * @param user пользователь, для которого будет выполнен запрос (его атрибуты будут переданы в запрос в параметрах p_sys...)
     * @param filterAndSorter фильтер и сортер
     * @param totals описание агригатов, которые необходимо вычислить
     * @param crudOptions доп параметры
     * @param beanType bean, который описывает запись
     *
     */
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
        int pageSize = Paramus.paramValue(prms, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, int.class, 0);
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
    public <T> BeansPage<T> loadPage(
            final String bioCode,
            final Object params,
            final User user,
            final List<Total> totals,
            final Class<T> beanType) {
        return loadPage(bioCode, params, user, null, totals, CrudOptions.builder().build(), beanType);
    }
    public <T> BeansPage<T> loadPage(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) {
        return loadPage(bioCode, params, user, null, null, CrudOptions.builder().build(), beanType);
    }

    /**
     * Выполняет запрос по коду и возвращает структуру @BeansPage
     * Набор содержит все записи.
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param params параметры запроса
     * @param user пользователь, для которого будет выполнен запрос (его атрибуты будут переданы в запрос в параметрах p_sys...)
     * @param filterAndSorter фильтер и сортер
     * @param totals описание агригатов, которые необходимо вычислить
     * @param beanType bean, который описывает запись
     */
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
    public <T> BeansPage<T> loadAll(
            final String bioCode,
            final Object params,
            final User user,
            final FilterAndSorter filterAndSorter,
            final Class<T> beanType
    ) {
        return loadAll(bioCode, params, user, filterAndSorter,null, beanType);
    }
    public <T> BeansPage<T> loadAll(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) {
        return loadAll(bioCode, params, user, null, null, beanType);
    }


    /**
     * Выполняет запрос по коду и возвращает структуру @BeansPage
     * Набор содержит все записи.
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request запрос
     * @param totals описание агригатов, которые необходимо вычислить
     * @param beanType bean, который описывает запись
     */
    public <T> BeansPage<T> requestAll(
            final String bioCode,
            final HttpServletRequest request,
            final List<Total> totals,
            final Class<T> beanType) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        return loadAll(bioCode, pax.params, pax.user, pax.filterAndSorter, totals, beanType);
    }
    public <T> BeansPage<T> requestAll(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> beanType) {
        return requestAll(bioCode, request, null, beanType);
    }
    public <T> List<T> requestAllExt(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> beanType) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        return loadAllExt(bioCode, pax.params, pax.user, beanType, pax.filterAndSorter);
    }


    /**
     * Выполняет запрос по коду и возвращает структуру @BeansPage
     * Набор содержит запрошенную страницу
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request запрос
     * @param totals описание агригатов, которые необходимо вычислить
     * @param beanType bean, который описывает запись
     */
    public <T> BeansPage<T> requestPage(
            final String bioCode,
            final HttpServletRequest request,
            final List<Total> totals,
            final Class<T> beanType) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        boolean forceCalcCount = Converter.toType(pax.queryParams.gcount, boolean.class);
        return loadPage(bioCode, pax.params, pax.user, pax.filterAndSorter, totals, CrudOptions.builder().forceCalcCount(forceCalcCount).build(), beanType);
    }
    public <T> BeansPage<T> requestPage(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> beanType) {
        return requestPage(bioCode, request, null, beanType);
    }

    /**
     * Выполняет запрос по коду
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request запрос
     * @param beanType тип записи
     * @param <T>      тип записи
     * @return один bean типа T
     */
    public <T> T requestFirstBean(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> beanType) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        return loadFirstBean(bioCode, pax.params, pax.user, beanType);
    }

    /**
     * Подсчитывает общее кол-во записей в наборе данных описанном в запросе bioCode
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request запрос
     * @return
     */
    public ABean calcTotalCount(
            final String bioCode,
            final HttpServletRequest request) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        ABean rslt = new ABean();
        Filter filter = pax.filterAndSorter != null ? pax.filterAndSorter.getFilter() : null;
        pax.sqlDefinition.getSelectSqlDef().setPreparedSql(pax.context.getWrappers().getFilteringWrapper().wrap(pax.sqlDefinition.getSelectSqlDef().getPreparedSql(), filter, pax.sqlDefinition.getSelectSqlDef().getFields()));
        Total countDef = Total.builder().fieldName("*").fieldType(long.class).aggrigate(Total.Aggregate.COUNT).fact(0L).build();
        pax.sqlDefinition.getSelectSqlDef().setTotalsSql(pax.context.getWrappers().getTotalsWrapper().wrap(pax.sqlDefinition.getSelectSqlDef().getPreparedSql(), Arrays.asList(countDef), pax.sqlDefinition.getSelectSqlDef().getFields()));
        List<Total> countFact = CrudReaderApi.calcTotalsRemote(Arrays.asList(countDef), pax.params, pax.context, pax.sqlDefinition, pax.user);
        rslt.put("totalCount", countFact.stream().findFirst().get().getFact());
        return rslt;
    }

    /**
     * Возвращает список бинов
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param params параметры SQL-запроса
     * @param user пользователь
     * @param beanType тип бина
     * @param filterAndSorter фильтр и сортер
     * @return Набор данных содержит запрошенную страницу
     */
    public <T> List<T> loadPageExt(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        int pageSize = Paramus.paramValue(prms, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
        else
            return CrudReaderApi.loadPageExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
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
    public <T> List<T> loadPageExt(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) {
        return loadPageExt(bioCode, params, user, beanType, null);
    }
    public <T> List<T> loadPageExt(
            final String bioCode,
            final Object params,
            final Class<T> beanType) {
        return loadPageExt(bioCode, params, beanType, null);
    }

    /**
     * Возвращает список бинов
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param params
     * @param user
     * @param beanType
     * @param filterAndSorter
     * @param <T>
     * @return набор содержит все записи
     */
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
    public <T> List<T> loadAllExt(
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) {
        return loadAllExt(bioCode, params, user, beanType, null);
    }
    public <T> List<T> loadAllExt(
            final String bioCode,
            final Object params,
            final Class<T> beanType) {
        return loadAllExt(bioCode, params, beanType, null);
    }

    /**
     * Возвращает список бинов
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request
     * @param beanType
     * @param <T>
     * @return набор содержит запрошенную страницу
     */
    public <T> List<T> requestPageExt(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> beanType) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        return loadPageExt(bioCode, pax.params, pax.user, beanType, pax.filterAndSorter);
    }

    /**
     * Возвращает один бин
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param params
     * @param usr
     * @param beanType
     * @param <T>
     * @return первый бин, который вернул запрос к БД
     */
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

    /**
     * Возвращает бин по ID
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request
     * @param id
     * @return один бин
     */
    public <T> T requestBean(
            final String bioCode,
            final HttpServletRequest request,
            final Object id,
            final Class<T> beanType) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        if(id != null) {
            Paramus.setParamValue(pax.params, Rest2sqlParamNames.GETROW_PARAM_PKVAL, id, MetaTypeConverter.read(id.getClass()));
            List<T> rslt = CrudReaderApi.loadRecordExt(pax.params, pax.context, pax.sqlDefinition, pax.user, beanType);
            if (rslt.size() > 0)
                return rslt.get(0);
        }
        return null;
    }

    /**
     * Возвращает все записи по ID
     * @param bioCode
     * @param id
     * @param user
     * @param beanType
     * @param <T>
     * @return
     */
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

    /**
     * Выполняет запрос к Бд и экспортирует данные к MSExcel
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request
     * @return
     */
    public HSSFWorkbook requestExcel(
            final String bioCode,
            final HttpServletRequest request) {
        if(excelBuilder == null)
            throw new IllegalArgumentException("excelBuilder not defined!");
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        BeansPage<ABean> beansPage = requestAll(bioCode, request, ABean.class);
        return pax.context.execBatch((ctx) -> {
            return excelBuilder.toExcel(beansPage.getRows(), bioCode);
        }, pax.user);
    }


    /**
     * Возвращает метаданные
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @return
     */
    public static ABean getMetadata(final String bioCode) {
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

    /**
     * Возвращает строку их БД (если json формируется в запросе)
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request
     * @return
     */
    public StringBuilder requestJson(
            final String bioCode,
            final HttpServletRequest request) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        StringBuilder rslt = CrudReaderApi.loadJson(pax.params, pax.context, pax.sqlDefinition, pax.user);
        return rslt;
    }

    /**
     * Сохраняет данные в БД (вызывает процедуру описанную в bioCode для каждой записи)
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request
     * @param rows
     * @return
     */
    public List<ABean> storeBeans(
            final String bioCode,
            final HttpServletRequest request,
            final List<ABean> rows) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        List<ABean> rslt = CrudWriterApi.saveRecords(pax.params, rows, pax.context, pax.sqlDefinition, pax.user);
        return rslt;
    }

    /**
     * Удалает записи по списку идентификаторов
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request
     * @param ids
     * @return
     */
    public ABean deleteBeans(
            final String bioCode,
            final HttpServletRequest request,
            final List<Object> ids) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        int affected = CrudWriterApi.deleteRecords(pax.params, ids, pax.context, pax.sqlDefinition, pax.user);
        ABean rslt = new ABean();
        rslt.put("deleted", affected);
        return rslt;
    }

    /**
     * Выполняет процедуру
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request
     */
    public void exec(
            final String bioCode,
            final HttpServletRequest request) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        CrudWriterApi.execSQL(pax.params, pax.context, pax.sqlDefinition, pax.user);
    }
    public void exec(
            final String bioCode,
            final Object params,
            final User user) {
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    /**
     * Выполняет скалярный запрос к БД
     * @param bioCode код запроса к базе данных (путь к xml-описанию запроса)
     * @param request
     * @param clazz
     * @param defaultValue
     * @param <T>
     * @return значние типа T
     */
    public <T> T requestScalar(
            final String bioCode,
            final HttpServletRequest request,
            final Class<T> clazz,
            final T defaultValue) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        return CrudReaderApi.selectScalar(pax.params, pax.context, pax.sqlDefinition, clazz, defaultValue, pax.user);
    }
    public <T> T requestScalar(
            final String bioCode,
            final HttpServletRequest request,
            final String fieldName,
            final Class<T> clazz,
            final T defaultValue) {
        final RequestParamsPack pax = _parsRequestPack(bioCode, request);
        return CrudReaderApi.selectScalar(pax.params, pax.context, pax.sqlDefinition, fieldName, clazz, defaultValue, pax.user);
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
    public <T> T selectScalar(
            final String bioCode,
            final Object params,
            final String fieldName,
            final Class<T> clazz,
            final T defaultValue,
            final User user) {
        final SQLContext context = getSqlContext();
        final SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        return CrudReaderApi.selectScalar(params, context, sqlDefinition, fieldName, clazz, defaultValue, user);
    }

    /**
     * Выполняет batch
     * @param context
     * @param action
     * @param user
     */
    public static void execBatch(final SQLContext context, final SQLActionVoid0 action, final User user) {
        context.execBatch(action, user);
    }
    public static <T> T execBatch(final SQLContext context, final SQLActionScalar0<T> action, final User user) {
        return context.execBatch(action, user);
    }
    public static <P, T> T execBatch(final SQLContext context, final SQLActionScalar1<P, T> action, P param, final User user) {
        return context.execBatch(action, param, user);
    }

    /**
     * Выполняет запрос в текущей транзакции
     * @param sqlDefinition
     * @param params
     * @param context
     */
    public static void execLocal(
            final SQLDefinition sqlDefinition,
            final Object params,
            final SQLContext context) {
        CrudWriterApi.execSQL0(params, context, sqlDefinition);
    }

    /**
     * Загружает страницу в текущей транзакции
     * @param sqlDefinition
     * @param context
     * @param params
     * @param beanType
     * @param filterAndSorter
     * @param <T>
     * @return
     */
    public static <T> List<T> loadPage0Ext(
            final SQLDefinition sqlDefinition,
            final SQLContext context,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        int pageSize = Paramus.paramValue(prms, Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAll0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
        else
            return CrudReaderApi.loadPage0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
    }
    /**
     * Загружает все записи в текущей транзакции
     * @param sqlDefinition
     * @param context
     * @param params
     * @param beanType
     * @param filterAndSorter
     * @param <T>
     * @return
     */
    public static <T> List<T> loadAll0Ext(
            final SQLDefinition sqlDefinition,
            final SQLContext context,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) {
        final List<Param> prms = DbUtils.decodeParams(params);
        return CrudReaderApi.loadAll0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
    }

    /**
     * Устанавливает значение параметра в запрос
     * @param paramName
     * @param paramValue
     * @param request
     */
    public void setBioParamToRequest(String paramName, Object paramValue, HttpServletRequest request) {
        final BioQueryParams queryParams = wrappedRequest(request).getBioQueryParams();
        Class<?> forceType = paramValue != null ? paramValue.getClass() : null;
        if(forceType != null) {
            MetaType forceMetaType = MetaTypeConverter.read(forceType);
            Paramus.setParamValue(queryParams.bioParams, paramName, paramValue, forceMetaType);
        } else
            Paramus.setParamValue(queryParams.bioParams, paramName, paramValue);
    }

    /**
     * Устанавливает параметр в запрос
     * @param param
     * @param request
     */
    public void setBioParamToRequest(Param param, HttpServletRequest request) {
        final BioQueryParams queryParams = wrappedRequest(request).getBioQueryParams();
        if(queryParams.bioParams == null)
            queryParams.bioParams = new ArrayList<>();
        Paramus.applyParams(queryParams.bioParams, Arrays.asList(param), false, true);
    }

    /**
     * Устанавливает параметры в запрос
     * @param params
     * @param request
     */
    public void setBioParamsToRequest(List<Param> params, HttpServletRequest request) {
        for(Param param : params)
            setBioParamToRequest(param, request);
    }

    /**
     * Устанавливает сортер в запрос
     * @param fieldName имя поля по которому сортировать
     * @param direction напрвление сортировки
     * @param request
     */
    public void setSorterToRequest(String fieldName, Sort.Direction direction, HttpServletRequest request) {
        final BioQueryParams queryParams = wrappedRequest(request).getBioQueryParams();
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

    /**
     * Засовывает значения атрибутов бина в параметры
     * @param bean
     * @param params
     * @return
     */
    public List<Param> setBeanToBioParams(ABean bean, List<Param> params) {
        if(params == null)
            params = new ArrayList<>();
        if(bean == null)
            return params;
        for(String key : bean.keySet())
            Paramus.setParamValue(params, key, bean.get(key));
        return params;
    }

    /**
     * Засовывает значения атрибутов бина в параметры запроса
     * @param bean
     * @param request
     * @return
     */
    public void setBeanToRequest(ABean bean, HttpServletRequest request) {
        final BioQueryParams queryParams = wrappedRequest(request).getBioQueryParams();
        setBeanToBioParams(bean, queryParams.bioParams);
    }

    /**
     * Проверяет существует ли параметр в запросе
     * @param paramName
     * @param request
     * @return
     */
    public boolean bioParamExistsInRequest(String paramName, HttpServletRequest request) {
        return wrappedRequest(request).bioQueryParamExists(paramName);
    }

    /**
     * Вытаскивает значение параметра из запроса
     * @param paramName
     * @param request
     * @param paramType
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType, T defaultValue) {
        return wrappedRequest(request).getBioQueryParam(paramName, paramType, defaultValue);
    }
    public <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType) {
        return  getBioParamFromRequest(paramName, request, paramType, null);
    }

    private List<Param> getBioParams(HttpServletRequest request) {
        List<Param> prms = wrappedRequest(request).getBioQueryParams().bioParams;
        if(prms == null){
            prms = new ArrayList<>();
            wrappedRequest(request).getBioQueryParams().bioParams = prms;
        }
        return prms;
    }

    /**
     * Заменяет имя параметра
     * @param request
     * @param newParamName
     * @param oldParamName
     */
    public void replaceBioParamName(HttpServletRequest request, String oldParamName, String newParamName) {
        List<Param> prms = getBioParams(request);
        Paramus.setParamValue(prms, newParamName, getBioParamFromRequest(oldParamName, request, String.class));
        prms.remove(oldParamName);
    }

    /**
     * Заменяет имя и значение параметра
     * @param request
     * @param newParamName
     * @param oldParamName
     */
    public void replaceBioParam(HttpServletRequest request, String oldParamName, String newParamName, Object newParamValue) {
        List<Param> prms = getBioParams(request);
        Paramus.setParamValue(prms, newParamName, newParamValue);
        prms.remove(oldParamName);
    }

    /**
     * Запрс к базе данных с помощью драйвера  https://github.com/blynkkk/clickhouse4j
     * при условии что запрос выполняется в режиме ... FORMAT JSON
     * @param bioCode
     * @param request
     * @param beanType
     * @param <T>
     * @return
     */
    public <T> BeansPage<T> requestByClickhouse4j(String bioCode, HttpServletRequest request, Class<T> beanType) {
        try {
            String json = requestScalar(bioCode, request, "json", String.class, null);
            if(Strings.isNullOrEmpty(json))
                throw new BioError("Запрос вернул пустую строку!");
            BeansPage<T> rslt = new BeansPage<>();
            rslt.setRows(new ArrayList<>());
            rslt.setTotals(new ArrayList<>());
            JSONObject jsonObj = new JSONObject(json);
            JSONArray data = jsonObj.has("data") ? jsonObj.getJSONArray("data") : null;
            JSONArray meta = jsonObj.has("meta") ? jsonObj.getJSONArray("meta") : null;
            JSONObject totals = jsonObj.has("totals") ? jsonObj.getJSONObject("totals") : null;
            if(data != null && meta != null) {
                for(int i=0; i<data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    T bean = Utl.createBeanFromJSONObject(obj, beanType);
                    rslt.getRows().add(bean);
                }
                if(totals != null) {
                    Iterator keys = totals.keys();
                    while (keys.hasNext()) {
                        String key = (String)keys.next();
                        rslt.getTotals().add(Total.builder().fieldName(key).fact(totals.get(key)).aggrigate(Total.Aggregate.UNDEFINED).build());
                    }
                }
            }
            return rslt;
        } catch(Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public String getRequestHash(HttpServletRequest request){
        return request.getRequestURI() + "?" + request.getQueryString();
    }

    public <T extends Serializable> T getObjectFromCache(String cacheName, HttpServletRequest request) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        String requestHash = getRequestHash(request);
        return cacheService.get(cacheName, requestHash);
    }

    public <T extends Serializable> void putObjectToCache(String cacheName, HttpServletRequest request, T value) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        String key = getRequestHash(request);
        cacheService.put(cacheName, key, value);
    }

    private static final String CACHE_CONTENT_HOLDER = "cached_content_holder";
    public <T extends Serializable> List<T> getListFromCache(String cacheName, HttpServletRequest request) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        String key = getRequestHash(request);
        ABean contaiter = cacheService.get(cacheName, key);
        Object content = contaiter != null ? contaiter.get(CACHE_CONTENT_HOLDER) : null;
        return content != null ? (List<T>)contaiter.get(CACHE_CONTENT_HOLDER) : null;
    }

    public <T extends Serializable> void putListToCache(String cacheName, HttpServletRequest request, List<T> value) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        ABean container = new ABean();
        container.put(CACHE_CONTENT_HOLDER, value);
        String key = getRequestHash(request);
        cacheService.put(cacheName, key, container);
    }

    public <T extends Serializable> BeansPage<T> getBeansPageFromCache(String cacheName, HttpServletRequest request) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        String key = getRequestHash(request);
        ABean contaiter = cacheService.get(cacheName, key);
        Object content = contaiter != null ? contaiter.get(CACHE_CONTENT_HOLDER) : null;
        return content != null ? (BeansPage<T>)contaiter.get(CACHE_CONTENT_HOLDER) : null;
    }

    public <T extends Serializable> void putBeansPageToCache(String cacheName, HttpServletRequest request, BeansPage<T> value) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        ABean container = new ABean();
        container.put(CACHE_CONTENT_HOLDER, value);
        String key = getRequestHash(request);
        cacheService.put(cacheName, key, container);
    }

}
