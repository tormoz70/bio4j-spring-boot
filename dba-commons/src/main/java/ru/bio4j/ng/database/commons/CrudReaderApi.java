package ru.bio4j.ng.database.commons;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.ABeans;
import ru.bio4j.ng.commons.utils.Sqls;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.model.transport.RestParamNames;

import java.sql.Connection;
import java.util.*;
import java.util.function.LongSupplier;

public class CrudReaderApi {
    protected final static Logger LOG = LoggerFactory.getLogger(CrudReaderApi.class);

    //TODO Перенести в настройки
    private static final int MAX_RECORDS_FETCH_LIMIT = 250000;

    private static void preparePkParamValue(final List<Param> params, final Field pkField) {
        Param pkParam = Paramus.getParam(params, RestParamNames.GETROW_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setType(pkField.getMetaType());
            pkParam.setValue(newValue);
        }
        pkParam = Paramus.getParam(params, RestParamNames.LOCATE_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setValue(newValue);
        }
        pkParam = Paramus.getParam(params, RestParamNames.DELETE_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setValue(newValue);
        }
    }

    private static ABeanPage readStoreData(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursorDef,
            final User usr) {
        if(LOG.isDebugEnabled())
            LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        ABeanPage result = new ABeanPage();
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        result.setTotalCount(Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, int.class, 0));
        result.setPaginationOffset(Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0));
        result.setRows(new ArrayList<>());

        long startTime = System.currentTimeMillis();
        result.setMetadata(cursorDef.getFields());

        List<Param> prms = params;
        context.createDynamicCursor()
                .init(context.getCurrentConnection(), cursorDef.getSelectSqlDef())
                .fetch(prms, usr, rs -> {
                    if(rs.isFirstRow()) {
                        long estimatedTime = System.currentTimeMillis() - startTime;
                        if(LOG.isDebugEnabled())
                            LOG.debug("Cursor \"{}\" opened in {} secs!!!", cursorDef.getBioCode(), Double.toString(estimatedTime / 1000));
                    }
                    ABean bean = DbUtils.createABeanFromReader(result.getMetadata(), rs);
                    result.getRows().add(bean);
                    if(result.getRows().size() >= MAX_RECORDS_FETCH_LIMIT)
                        return false;
                    return true;
                });
        if(LOG.isDebugEnabled())
            LOG.debug("Cursor \"{}\" fetched! {} - records loaded.", cursorDef.getBioCode(), result.getRows().size());
        result.setPaginationCount(result.getRows().size());
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        result.setPaginationPage(pageSize > 0 ? (int)Math.floor(result.getPaginationOffset() / pageSize) + 1 : 0);
        if(result.getRows().size() < paginationPagesize) {
            result.setTotalCount(result.getPaginationOffset() + result.getRows().size());
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, result.getTotalCount());
        }
        return result;
    }

    private static long calcOffset(int locatedPos, int pageSize){
        long pg = ((long)((double)(locatedPos - 1) / (double)pageSize) + 1);
        return (pg - 1) * pageSize;
    }

    public static long calcTotalCount(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user) {
        long result = context.execBatch((ctx) -> {
            SQLCursor c = ctx.createDynamicCursor();
            c.init(ctx.getCurrentConnection(), cursor.getSelectSqlDef().getTotalsSql());
            return c.scalar(params, user, long.class, 0L);
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
     * @param forceCalcCount
     * @return возвращает страницу
     *
     */
    public static ABeanPage loadPage0(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final SQLDefinition cursor,
            final boolean forceCalcCount) {

        Connection connTest = context.getCurrentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursor.getBioCode()));

        final Object location = Paramus.paramValue(params, RestParamNames.LOCATE_PARAM_PKVAL, java.lang.Object.class, null);
        final int paginationOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0);
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);

        final String paginationTotalcountStr = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, String.class, null);
        final int paginationTotalcount = Strings.isNullOrEmpty(paginationTotalcountStr) ? Sqls.UNKNOWN_RECS_TOTAL : Converter.toType(paginationTotalcountStr, int.class);

        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter, cursor.getSelectSqlDef().getFields()));
        cursor.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));
        List<Sort> localSort = sort != null ? sort : new ArrayList<>();
        if(localSort.size() == 0 && cursor.getSelectSqlDef() != null && cursor.getSelectSqlDef().getDefaultSort() != null)
            localSort.addAll(cursor.getSelectSqlDef().getDefaultSort());
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), localSort, cursor.getSelectSqlDef().getFields()));
        if(location != null) {
            Field pkField = cursor.getSelectSqlDef().findPk();
            if(pkField == null)
                throw new BioSQLException(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
            cursor.getSelectSqlDef().setLocateSql(context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), pkField.getName()));
            preparePkParamValue(params, pkField);
        }
        if(paginationPagesize > 0)
            cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));

        long factOffset = paginationOffset;
        long totalCount = paginationTotalcount;
        if(forceCalcCount || paginationOffset == (Sqls.UNKNOWN_RECS_TOTAL - paginationPagesize + 1))
            totalCount = calcTotalCount(params, context, cursor, context.getCurrentUser());
        if(paginationOffset == (Sqls.UNKNOWN_RECS_TOTAL - paginationPagesize + 1)) {
            factOffset = DbUtils.calcfactOffset(totalCount, paginationPagesize);

            if(LOG.isDebugEnabled())
                LOG.debug("Count of records of cursor \"{}\" - {}!!!", cursor.getBioCode(), totalCount);
        }
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, factOffset);
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, totalCount);
        long locFactOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, long.class, 0L);
        if(location != null) {
            if(LOG.isDebugEnabled())
                LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cursor.getBioCode(), location);
            int locatedPos = context.createDynamicCursor()
                    .init(context.getCurrentConnection(), cursor.getSelectSqlDef())
                    .scalar(params, context.getCurrentUser(), int.class, -1);
            if(locatedPos >= 0){
                locFactOffset = calcOffset(locatedPos, paginationPagesize);
                if(LOG.isDebugEnabled())
                    LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cursor.getBioCode(), location, locatedPos, locFactOffset);
            } else {
                if(LOG.isDebugEnabled())
                    LOG.debug("Cursor \"{}\" failed location to [{}] record by pk!!!", cursor.getBioCode(), location);
            }
        }
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, locFactOffset);
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_LIMIT, paginationPagesize);
        return readStoreData(params, context, cursor, context.getCurrentUser());
    }

    /***
     * Выполняет запрос в новой сессиии
     * @param params
     * @param filter
     * @param sort
     * @param context
     * @param cursor
     * @param forceCalcCount
     * @param user
     * @return возвращает страницу
     *
     */
    public static ABeanPage loadPage(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final SQLDefinition cursor,
            final boolean forceCalcCount,
            final User user) {
        final ABeanPage result = context.execBatch((ctx) -> {
            return loadPage0(params, filter, sort, ctx, cursor, forceCalcCount);
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
     * @return все записи
     *
     */
    public static ABeanPage loadAll0(final List<Param> params, final Filter filter, final List<Sort> sort, final SQLContext context, final SQLDefinition cursor) {
        Connection connTest = context.getCurrentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursor.getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter, cursor.getSelectSqlDef().getFields()));
        List<Sort> localSort = sort != null ? sort : new ArrayList<>();
        if(localSort.size() == 0 && cursor.getSelectSqlDef() != null && cursor.getSelectSqlDef().getDefaultSort() != null)
            localSort.addAll(cursor.getSelectSqlDef().getDefaultSort());
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), localSort, cursor.getSelectSqlDef().getFields()));
        return readStoreData(params, context, cursor, context.getCurrentUser());
    }

    private static HSSFCellStyle createHeaderStyle(HSSFWorkbook wb) {
        HSSFCellStyle rslt = wb.createCellStyle();
        HSSFFont headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short)12);
        headerFont.setBold(true);
        rslt.setFont(headerFont);
        rslt.setAlignment(HorizontalAlignment.CENTER);
        rslt.setVerticalAlignment(VerticalAlignment.CENTER);
        rslt.setBorderBottom(BorderStyle.THIN);
        rslt.setBorderLeft(BorderStyle.THIN);
        rslt.setBorderRight(BorderStyle.THIN);
        rslt.setBorderTop(BorderStyle.THIN);
        rslt.setLocked(true);
        rslt.setWrapText(true);
        return rslt;
    }

    private static Map<String, HSSFCellStyle> createRowStyle(HSSFWorkbook wb, SelectSQLDef sqlDef) {
        Map<String, HSSFCellStyle> rslt = new HashMap<>();
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short) 10);
        style.setFont(cellFont);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        rslt.put("RNUM", style);
        for (Field fld : sqlDef.getFields()) {
            style = wb.createCellStyle();
            style.setFont(cellFont);
            style.setAlignment(HorizontalAlignment.CENTER);
            HorizontalAlignment ha = Enum.valueOf(HorizontalAlignment.class, fld.getAlign().name().toUpperCase());
            if(ha != null)
                style.setAlignment(ha);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setWrapText(true);
            rslt.put(fld.getName(), style);
        }
        return rslt;
    }

    private static void addHeader(HSSFSheet ws, SelectSQLDef sqlDef) {
        if(sqlDef != null && sqlDef.getFields()!= null && sqlDef.getFields().size() > 0) {
            HSSFRow r = ws.createRow(0);
            int celNum = 0;
            HSSFCell cellRNUM = r.createCell(celNum);
            cellRNUM.setCellValue("№ пп");
            HSSFCellStyle headerStyle = createHeaderStyle(ws.getWorkbook());
            cellRNUM.setCellStyle(headerStyle);
            celNum++;
            for (Field fld : sqlDef.getFields()) {
                if(fld.getExpEnabled()){
                    HSSFCell c = r.createCell(celNum);
                    int colWidth = Converter.toType(fld.getExpWidth(), int.class);
                    if(colWidth == 0) colWidth = 4700;
                    ws.setColumnWidth(celNum, colWidth);
                    c.setCellStyle(headerStyle);
                    c.setCellValue(Utl.nvl(fld.getTitle(), Utl.nvl(fld.getAttrName(), fld.getName())));
                    celNum++;
                }
            }
        }
    }

    private static void addRow(HSSFSheet ws, Map<String, HSSFCellStyle> rowStyles, SelectSQLDef sqlDef, int rowNum, ABean rowData) {
        HSSFRow r = ws.createRow(rowNum);
        int celNum = 0;
        HSSFCell cellRNUM = r.createCell(celNum);
        cellRNUM.setCellValue(rowNum);
        cellRNUM.setCellStyle(rowStyles.get("RNUM"));
        celNum++;
        for (Field fld : sqlDef.getFields()) {
            if (fld.getExpEnabled()) {
                HSSFCell c = r.createCell(celNum);
                c.setCellStyle(rowStyles.get(fld.getName()));
                c.setCellValue(ABeans.extractAttrFromBean(rowData, Utl.nvl(fld.getAttrName(), fld.getName()), String.class, null));
                celNum++;
            }
        }
    }

    public static HSSFWorkbook toExcel(final List<Param> params, final Filter filter, final List<Sort> sort, final SQLContext context, final SQLDefinition cursor) {
        Connection connTest = context.getCurrentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This method can be used only in SQLAction of execBatch!", cursor.getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter, cursor.getSelectSqlDef().getFields()));
        List<Sort> localSort = sort != null ? sort : new ArrayList<>();
        if(localSort.size() == 0 && cursor.getSelectSqlDef() != null && cursor.getSelectSqlDef().getDefaultSort() != null)
            localSort.addAll(cursor.getSelectSqlDef().getDefaultSort());
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), localSort, cursor.getSelectSqlDef().getFields()));
        ABeanPage page = readStoreData(params, context, cursor, context.getCurrentUser());

        HSSFWorkbook wb = null;
        if(page != null && page.getRows() != null && page.getRows().size() > 0) {
            wb = new HSSFWorkbook();
            HSSFSheet ws = wb.createSheet();
            addHeader(ws, cursor.getSelectSqlDef());
            Map<String, HSSFCellStyle> rowStyles = createRowStyle(wb, cursor.getSelectSqlDef());
            int rowNum = 1;
            for (ABean bean : page.getRows()) {
                addRow(ws, rowStyles, cursor.getSelectSqlDef(), rowNum, bean);
                rowNum++;
            }
        }
        return wb;
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
    public static ABeanPage loadAll(final List<Param> params, final Filter filter, final List<Sort> sort, final SQLContext context, final SQLDefinition cursor, User user) {
        ABeanPage result = context.execBatch((ctx) -> {
            return loadAll0(params, filter, sort, ctx, cursor);
        }, user);
        return result;
    }


    public static ABeanPage loadRecord(final List<Param> params, final SQLContext context, final SQLDefinition cursor, final User user) {
        ABeanPage result = context.execBatch((ctx) -> {
            return loadRecord0(params, ctx, cursor);
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
    public static ABeanPage loadRecord0(final List<Param> params, final SQLContext context, final SQLDefinition cursor) {
        Connection connTest = context.getCurrentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This method can be used only in SQLAction of execBatch!", cursor.getBioCode()));

        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioSQLException(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        preparePkParamValue(params, pkField);
        return readStoreData(params, context, cursor, context.getCurrentUser());
    }

    private static <T> List<T> readStoreDataExt(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursorDef,
            final Class<T> beanType,
            final int recordsLimit) {

        if (context.getCurrentConnection() == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursorDef.getBioCode()));

        if(LOG.isDebugEnabled())
            LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        List<T> result = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        List<Param> prms = params;
        final LongSupplier limitRecordsToRead = () -> {
            if (recordsLimit > 0)
                return recordsLimit;
            return MAX_RECORDS_FETCH_LIMIT;
        };

        context.createDynamicCursor()
                .init(context.getCurrentConnection(), cursorDef.getSelectSqlDef())
                .fetch(prms, context.getCurrentUser(), rs -> {
                    if (rs.isFirstRow()) {
                        long estimatedTime = System.currentTimeMillis() - startTime;
                        if(LOG.isDebugEnabled())
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
        if(LOG.isDebugEnabled())
            LOG.debug("Cursor \"{}\" fetched! {} - records loaded.", cursorDef.getBioCode(), result.size());
        return result;
    }

    private static <T> List<T> readStoreDataExt(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursorDef,
            final Class<T> beanType) {
        return readStoreDataExt(params, context, cursorDef, beanType, 0);
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
        Connection connTest = context.getCurrentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursor.getBioCode()));

        final Object location = Paramus.paramValue(params, RestParamNames.LOCATE_PARAM_PKVAL, java.lang.Object.class, null);
        final int paginationOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0);
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);

        final String paginationTotalcountStr = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, String.class, null);
        final int paginationTotalcount = Strings.isNullOrEmpty(paginationTotalcountStr) ? Sqls.UNKNOWN_RECS_TOTAL : Converter.toType(paginationTotalcountStr, int.class);

        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter, cursor.getSelectSqlDef().getFields()));
        cursor.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));
        List<Sort> localSort = sort != null ? sort : new ArrayList<>();
        if(localSort.size() == 0 && cursor.getSelectSqlDef() != null && cursor.getSelectSqlDef().getDefaultSort() != null)
            localSort.addAll(cursor.getSelectSqlDef().getDefaultSort());
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), localSort, cursor.getSelectSqlDef().getFields()));
        if (location != null) {
            Field pkField = cursor.getSelectSqlDef().findPk();
            if (pkField == null)
                throw new BioSQLException(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
            cursor.getSelectSqlDef().setLocateSql(context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
            preparePkParamValue(params, pkField);
        }
        if (paginationPagesize > 0)
            cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));

        long factOffset = paginationOffset;
        long totalCount = paginationTotalcount;
        if (paginationOffset == (Sqls.UNKNOWN_RECS_TOTAL - paginationPagesize + 1)) {
            totalCount = calcTotalCount(params, context, cursor, context.getCurrentUser());
            factOffset = (int) Math.floor(totalCount / paginationPagesize) * paginationPagesize;
            if(LOG.isDebugEnabled())
                LOG.debug("Count of records of cursor \"{}\" - {}!!!", cursor.getBioCode(), totalCount);
        }
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, factOffset);
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, totalCount);
        long locFactOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, long.class, 0L);
        if (location != null) {
            if(LOG.isDebugEnabled())
                LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cursor.getBioCode(), location);
            int locatedPos = context.createDynamicCursor()
                    .init(context.getCurrentConnection(), cursor.getSelectSqlDef().getLocateSql(), cursor.getSelectSqlDef().getParamDeclaration())
                    .scalar(params, context.getCurrentUser(), int.class, -1);
            if (locatedPos >= 0) {
                locFactOffset = calcOffset(locatedPos, paginationPagesize);
                if(LOG.isDebugEnabled())
                    LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cursor.getBioCode(), location, locatedPos, locFactOffset);
            } else {
                if(LOG.isDebugEnabled())
                    LOG.debug("Cursor \"{}\" failed location to [{}] record by pk!!!", cursor.getBioCode(), location);
            }
        }
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, locFactOffset);
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_LIMIT, paginationPagesize);
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
        List<T> result = context.execBatch((ctx) -> {
            return loadPage0Ext(params, filter, sort, ctx, cursor, beanType);
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
        Connection connTest = context.getCurrentConnection();
        if (connTest == null)
            throw new BioSQLException(String.format("This methon can be useded only in SQLAction of execBatch!", cursor.getBioCode()));

        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter, cursor.getSelectSqlDef().getFields()));
        List<Sort> localSort = sort != null ? sort : new ArrayList<>();
        if(localSort.size() == 0 && cursor.getSelectSqlDef() != null && cursor.getSelectSqlDef().getDefaultSort() != null)
            localSort.addAll(cursor.getSelectSqlDef().getDefaultSort());
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), localSort, cursor.getSelectSqlDef().getFields()));
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
        List<T> result = context.execBatch((ctx) -> {
            return loadAll0Ext(params, filter, sort, ctx, cursor, beanType);
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
        List<T> result = context.execBatch((ctx) -> {
            return readStoreDataExt(params, ctx, cursor, beanType);
        }, user);
        return result;
    }

    public static <T> T loadFirstRecordExt(
            final List<Param> params,
            final SQLContext context, final SQLDefinition cursor,
            final User user,
            final Class<T> beanType) {
        cursor.getSelectSqlDef().setPreparedSql(cursor.getSelectSqlDef().getSql());
        List<T> result = context.execBatch((ctx) -> {
            return readStoreDataExt(params, ctx, cursor, beanType, 1);
        }, user);
        return result.size() > 0 ? result.get(0) : null;
    }

    public static StringBuilder loadJson(
            final List<Param> params,
            final SQLContext context,
            final SQLDefinition cursor,
            final User user) {
        StringBuilder result = context.execBatch((ctx) -> {
            final StringBuilder r = new StringBuilder();

            ctx.createDynamicCursor()
                    .init(ctx.getCurrentConnection(), cursor.getSelectSqlDef().getSql(), cursor.getSelectSqlDef().getParamDeclaration())
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
            final Class<T> clazz,
            final T defaultValue) {
        return DbUtils.processSelectScalar0(params, context, sqlDefinition, clazz, defaultValue);
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

}
