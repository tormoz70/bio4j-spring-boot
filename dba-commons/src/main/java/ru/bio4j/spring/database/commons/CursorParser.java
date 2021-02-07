package ru.bio4j.spring.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.*;
import ru.bio4j.spring.database.api.SQLDef;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.database.api.SQLType;
import ru.bio4j.spring.database.api.SelectSQLDef;
import ru.bio4j.spring.model.transport.errors.BioSQLException;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.jstore.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CursorParser {
    private static final Logger LOG = LoggerFactory.getLogger(CursorParser.class);

    /**
     * Экземпляр класса.
     */
    private static CursorParser instance;

    public static CursorParser getInstance() {
        if (instance == null)
            synchronized (CursorParser.class) {
                if (instance == null)
                    createCursorParser();
            }
        return instance;
    }

    private static void createCursorParser() {
        instance = new CursorParser();
    }

    private CursorParser() {
    }

    private static final String ATTRS_DELIMITER = ";";
    private static final String ATTRS_KEYVALUE_DELIMITER = ":";

    private static final String PARAM_PREFIX = "param.";
    private static final String REGEX_PARAMS = "(/\\*\\$\\{" + PARAM_PREFIX + ".*?\\}\\*/)";
    private static final String REGEX_ATTRS = "(?<=/\\*\\$\\{).*(?=\\}\\*/)";
    private static final String REGEX_PARAM_KILLDEBUG = "debug:\\*/.*/\\*";

    private CursorSqlResolver cursorSqlResolver;

    private Param parseParam(String paramDef) {
        String attrsList = Regexs.find(paramDef, REGEX_ATTRS, 0);
        attrsList = Regexs.replace(attrsList, REGEX_PARAM_KILLDEBUG, "", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        String[] attrs = Strings.split(attrsList, ATTRS_DELIMITER);
        String name = null;
        MetaType type = null;
        String dir = "IN";
        for (String attr : attrs) {
            String[] pair = Strings.split(attr.trim(), ATTRS_KEYVALUE_DELIMITER);
            if (pair.length == 1 && pair[0].startsWith(PARAM_PREFIX))
                name = pair[0].substring(PARAM_PREFIX.length()).trim().toLowerCase();
            if (pair.length == 2) {
                if (pair[0].equals("type"))
                    type = MetaType.decode(pair[1].trim());
                if (pair[0].equals("dir"))
                    dir = pair[1].trim().toUpperCase();
            }
        }
        return Param.builder()
                .name(name)
                .type(type)
                .direction(Param.Direction.valueOf(dir))
                .build();
    }

    private static final String COL_PREFIX = "col.";
    private static final String REGEX_COLS = "(/\\*\\$\\{" + COL_PREFIX + ".*?\\}\\*/)";
    private static final String REGEX_COLS_TITLE = "title:\".*?\"";
    private static final String REGEX_COLS_FORMAT = "format:\".*?\"";
    private static final String REGEX_COLS_NAME = "\\bcol.\\w+\\b";
    private static final String REGEX_QUOTES_REPLACER = "\\\\\"";
    private static final String QUOTES_PLACEHOLDER = "\\$quote\\$";

    private void parseCol(List<Field> cols, String colDef) {
        String attrsList = Regexs.find(colDef, REGEX_ATTRS, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);
        // Заменяем все внутренние(экранированные) ковычки на QUOTES_PLACEHOLDER
        attrsList = Regexs.replace(attrsList, REGEX_QUOTES_REPLACER, QUOTES_PLACEHOLDER, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        // Вытаскиваем имя колонки
        String name = Regexs.find(attrsList, REGEX_COLS_NAME, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        if (Strings.isNullOrEmpty(name))
            throw new IllegalArgumentException("Attribute \"col.name\" not found in descriptor!");
        name = Strings.split(name, ".")[1].trim().toLowerCase();

        Field col = findField(name, cols);
        if (col == null) {
            col = new Field();
            cols.add(col);
            col.setName(name);
            col.setId(cols.size());
        }


        attrsList = Regexs.replace(attrsList, REGEX_COLS_NAME, "", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        // Вытаскиваем title
        String title = Regexs.find(attrsList, REGEX_COLS_TITLE, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        if (!Strings.isNullOrEmpty(title)) {
            // Удаляем title из атрибутов
            attrsList = Regexs.replace(attrsList, REGEX_COLS_TITLE, "", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
            // Вытаскиваем значение
            title = Strings.split(title, ATTRS_KEYVALUE_DELIMITER)[1].trim();
            // Удаляем ковычки заголовка
            title = Strings.trim(title, "\"");
            // Возвращаем назад внутренние ковычки
            title = Regexs.replace(title, QUOTES_PLACEHOLDER, "\"", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        }
        // Вытаскиваем format
        String format = Regexs.find(attrsList, REGEX_COLS_FORMAT, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        if (!Strings.isNullOrEmpty(format)) {
            // Удаляем format из атрибутов
            attrsList = Regexs.replace(attrsList, REGEX_COLS_FORMAT, "", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
            // Вытаскиваем значение format
            format = Strings.split(format, ATTRS_KEYVALUE_DELIMITER)[1].trim();
            // Удаляем ковычки формата
            format = Strings.trim(format, "\"");
        }
        col.setName(name);
        col.setTitle(title);
        col.setFormat(format);

        String[] attrs = Strings.split(attrsList, ATTRS_DELIMITER);
        for (String attr : attrs) {
            String[] pair = Strings.split(attr.trim(), ATTRS_KEYVALUE_DELIMITER);
            if (pair.length == 2) {
                if (pair[0].equals("type"))
                    col.setMetaType(MetaType.decode(pair[1].trim()));
                if (pair[0].equals("pk"))
                    col.setPk(Boolean.parseBoolean(pair[1].trim()));
                if (pair[0].equals("mandatory"))
                    col.setMandatory(Boolean.parseBoolean(pair[1].trim()));
                if (pair[0].equals("align"))
                    col.setAlign(Alignment.valueOf(pair[1].trim().toUpperCase()));
                if (pair[0].equals("width"))
                    col.setWidth(pair[1].trim());
                if (pair[0].equals("hidden"))
                    col.setHidden(Boolean.parseBoolean(pair[1].trim()));
                if (pair[0].equals("filter"))
                    col.setFilter(Boolean.parseBoolean(pair[1].trim()));
                if (pair[0].equals("showTooltip"))
                    col.setShowTooltip(Boolean.parseBoolean(pair[1].trim()));
                if (pair[0].equals("readonly"))
                    col.setReadonly(Boolean.parseBoolean(pair[1].trim()));
            }
        }
    }

    private void addParamsFromXml(final SQLDef sqlDef, final Element sqlElem) {
        NodeList paramNodes = sqlElem.getElementsByTagName("param");
        try (Paramus p = Paramus.set(sqlDef.getParamDeclaration());) {
            for (int i = 0; i < paramNodes.getLength(); i++) {
                Element paramElem = (Element) paramNodes.item(i);
                String paramName = Doms.getAttribute(paramElem, "name", "", String.class);
                MetaType paramType = Converter.toType(Doms.getAttribute(paramElem, "type", "string", String.class), MetaType.class);
                Param.Direction paramDir = Converter.toType(Doms.getAttribute(paramElem, "direction", "IN", String.class), Param.Direction.class);
                Boolean override = Doms.getAttribute(paramElem, "override", true, Boolean.class);
                String format = Doms.getAttribute(paramElem, "format", null, String.class);
                Object defaultValue = Doms.getAttribute(paramElem, "defaultValue", null, MetaTypeConverter.write(paramType));
                Param param = p.getParam(paramName, true);
                if (param == null) {
                    param = Param.builder()
                            .name(paramName)
                            .type(paramType)
                            .direction(paramDir)
                            .override(override)
                            .format(format)
                            .value(defaultValue)
                            .build();
                    p.add(param);
                } else {
                    param.setType(paramType);
                    param.setDirection(paramDir);
                }
            }
        }
    }

    private void addParamsFromSQLBody(final SQLDef sqlDef) {
        final String sql = sqlDef.getSql();
        final StringBuffer out = new StringBuffer(sql.length());
        final List<String> paramsNames = Sqls.extractParamNamesFromSQL(sql);
        try (Paramus p = Paramus.set(sqlDef.getParamDeclaration());) {
            for (String paramActual : paramsNames) {
                Param param = Param.builder()
                        .name(paramActual)
                        .type(MetaType.STRING)
                        .direction(Param.Direction.IN)
                        .build();
                p.add(param);
            }
        }
    }

    private static Field findField(final String name, final List<Field> cols) {
        return Lists.first(cols, new Predicate<Field>() {
            @Override
            public boolean test(Field item) {
                return Strings.compare(name, item.getName(), true);
            }
        });
    }

    private static void addFieldsFromXml(final SQLDefinitionImpl cursor, final Document document) {
        Element sqlElem = Doms.findElem(document, "/cursor/fields");
        NodeList fieldNodes = sqlElem.getElementsByTagName("field");
        List<Field> fields = cursor.getFields();
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Element fieldElem = (Element) fieldNodes.item(i);
            boolean generate = Converter.toType(Doms.getAttribute(fieldElem, "generate", "true", String.class), boolean.class);
            if (generate) {
                String fieldName = Doms.getAttribute(fieldElem, "name", "", String.class);
                String attrName = Doms.getAttribute(fieldElem, "attrName", null, String.class);
                Field field = findField(fieldName, fields);
                if (field == null) {
                    field = new Field();
                    fields.add(field);
                    field.setName(fieldName);
                    field.setAttrName(attrName);
                }
                field.setId(i + 1);
                field.setFormat(Doms.getAttribute(fieldElem, "format", null, String.class));
                String dtoDoc = fieldElem.getTextContent();
                String header = Doms.getAttribute(fieldElem, "header", null, String.class);
                if (Strings.isNullOrEmpty(header) && !Strings.isNullOrEmpty(dtoDoc)) {
                    header = dtoDoc;
                } else if (!Strings.isNullOrEmpty(header) && Strings.isNullOrEmpty(dtoDoc)) {
                    dtoDoc = header;
                } else if (Strings.isNullOrEmpty(header) && Strings.isNullOrEmpty(dtoDoc)) {
                    dtoDoc = fieldName;
                    header = fieldName;
                }
                field.setTitle(header);
                field.setDtoDocumentation(dtoDoc);
                field.setMetaType(Converter.toType(Doms.getAttribute(fieldElem, "type", "string", String.class), MetaType.class));
                field.setAlign(Converter.toType(Doms.getAttribute(fieldElem, "align", "left", String.class), Alignment.class));
                field.setHidden(Converter.toType(Doms.getAttribute(fieldElem, "hidden", "false", String.class), boolean.class));
                field.setDtoSkip(Converter.toType(Doms.getAttribute(fieldElem, "dtoSkip", "false", String.class), boolean.class));
                field.setFilter(Converter.toType(Doms.getAttribute(fieldElem, "filter", "false", String.class), boolean.class));
                field.setShowTooltip(Converter.toType(Doms.getAttribute(fieldElem, "showTooltip", "false", String.class), boolean.class));
                field.setDefaultVal(Doms.getAttribute(fieldElem, "defaultVal", null, String.class));
                field.setPk(Converter.toType(Doms.getAttribute(fieldElem, "pk", "false", String.class), boolean.class));
                field.setUseNull(Converter.toType(Doms.getAttribute(fieldElem, "useNull", "true", String.class), boolean.class));
                field.setReadonly(Converter.toType(Doms.getAttribute(fieldElem, "readOnly", "true", String.class), boolean.class));
                field.setWidth(Doms.getAttribute(fieldElem, "width", null, String.class));
                field.setExpEnabled(Converter.toType(Doms.getAttribute(fieldElem, "expEnabled", "true", String.class), boolean.class));
                field.setExpFormat(Doms.getAttribute(fieldElem, "expFormat", null, String.class));
                field.setExpWidth(Doms.getAttribute(fieldElem, "expWidth", null, String.class));
                field.setSort(Converter.toType(Doms.getAttribute(fieldElem, "sort", "true", String.class), boolean.class));
                field.setSorter(Doms.getAttribute(fieldElem, "sorter", null, String.class));
                field.setNullsPosition(Converter.toType(Doms.getAttribute(fieldElem, "nullsPosition", "DEFAULT", String.class), Sort.NullsPosition.class));
                field.setTextLocality(Converter.toType(Doms.getAttribute(fieldElem, "textLocality", "UNDEFINED", String.class), Sort.TextLocality.class));
                field.setTooltip(Doms.getAttribute(fieldElem, "tooltip", null, String.class));
                field.setMandatory(Converter.toType(Doms.getAttribute(fieldElem, "mandatory", "false", String.class), boolean.class));
                field.setAggregate(Converter.toType(Doms.getAttribute(fieldElem, "aggregate", "UNDEFINED", String.class), Total.Aggregate.class));
                field.setLooCaption(Converter.toType(Doms.getAttribute(fieldElem, "looCaption", "false", String.class), boolean.class));
                String editMaxLength = Doms.getAttribute(fieldElem, "editMaxLength", null, String.class);
                field.setEditMaxLength(Strings.isNullOrEmpty(editMaxLength) ? Converter.toType(editMaxLength, Integer.class) : null);
                field.setEditor(Doms.getAttribute(fieldElem, "editor", null, Boolean.class));
                field.setLooReference(Doms.getAttribute(fieldElem, "looReference", null, String.class));
                field.setFixed(Converter.toType(Doms.getAttribute(fieldElem, "fixed", "NONE", String.class), Fixed.class));
            }
        }

    }

    private static void addDefaultSortFromXml(final SelectSQLDef selectSQLDef, final Document document) {
        Element sqlElem = Doms.findElem(document, "/cursor/defaultSort");
        if (sqlElem != null) {
            NodeList fieldNodes = sqlElem.getElementsByTagName("field");
            if (fieldNodes != null && fieldNodes.getLength() > 0) {
                if (selectSQLDef.getDefaultSort() == null)
                    selectSQLDef.setDefaultSort(new ArrayList<>());
                for (int i = 0; i < fieldNodes.getLength(); i++) {
                    Element paramElem = (Element) fieldNodes.item(i);
                    String fieldName = Doms.getAttribute(paramElem, "name", "", String.class);
                    Sort.Direction sortDirection = Converter.toType(Doms.getAttribute(paramElem, "direction", "ASC", String.class), Sort.Direction.class);
                    Sort sort = new Sort();
                    sort.setFieldName(fieldName);
                    sort.setDirection(sortDirection);
                    selectSQLDef.getDefaultSort().add(sort);
                }
            }
        }
    }

    private static Document loadXmlDocumentFromInputStream(final InputStream inputStream) {
        return Utl.loadXmlDocument(inputStream);
    }

    private static Document loadXmlDocumentFromRes(final String bioCode) {
        String path = Utl.extractBioPath(bioCode) + ".xml";
        LOG.debug("Loading cursor spec from \"{}\"", path);
        try (InputStream inputStream = Utl.openFile(path)) {
            return Utl.loadXmlDocument(inputStream);
        } catch(IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    private static String buildPath(String path, String bioCode, String extension) {
        return path + File.separator + bioCode.replace(".", File.separator) + extension;
    }

    private static Document loadXmlDocumentFromPath(final String contentRootPath, final String bioCode) {
        String path = buildPath(contentRootPath, bioCode, ".xml");
        return Utl.loadXmlDocument(path);
    }

    public SQLDefinition pars(final Document document, final String bioCode) {
        if(!document.getDocumentElement().getNodeName().equals("cursor"))
            return null;
        SQLDefinitionImpl cursor = new SQLDefinitionImpl(bioCode);
        cursor.setDtoName(Doms.getAttribute(document.getDocumentElement(), "dtoName", null, String.class));
        cursor.setDtoSkip(Doms.getAttribute(document.getDocumentElement(), "dtoSkip", false, Boolean.class));
        Element dtoDocElem = Doms.findElem(document.getDocumentElement(), "/cursor/dtoDocumentation");
        if(dtoDocElem != null)
            cursor.setDtoDocumentation(dtoDocElem.getTextContent());
        Element exportTitleElem = Doms.findElem(document.getDocumentElement(), "/cursor/exportTitle");
        Boolean readOnly = Doms.getAttribute(document.getDocumentElement(), "readOnly", true, Boolean.class);
        cursor.setReadOnly(readOnly);
        Boolean multiSelection = Doms.getAttribute(document.getDocumentElement(), "multiselection", false, Boolean.class);
        cursor.setMultiSelection(multiSelection);

        if (exportTitleElem != null)
            cursor.setExportTitle(exportTitleElem.getTextContent());
        addFieldsFromXml(cursor, document); // добавляем колонки из XML
        List<Element> sqlTextElems = Doms.findElems(document.getDocumentElement(), "/cursor/SQL");
        for (Element sqlElem : sqlTextElems) {
            SQLType curType = Doms.getAttribute(sqlElem, "action", SQLType.SELECT, SQLType.class);
            String sql = sqlElem.getTextContent().trim();
            sql = getCursorSqlResolver().tryLoadSQL(bioCode, sql);
            SQLDef sqlDef;
            if (curType == SQLType.SELECT) {
                sqlDef = new SQLDefinitionImpl.SelectSQLDefImpl(sql);
                addDefaultSortFromXml((SelectSQLDef) sqlDef, document);
            } else
                sqlDef = new SQLDefinitionImpl.UpdelexSQLDefImpl(sql);
            cursor.setSqlDef(curType, sqlDef);

            addParamsFromSQLBody(sqlDef); // добавляем переменные из SQL
            addParamsFromXml(sqlDef, sqlElem); // добавляем переменные из XML
        }
        //LOG.debug("BioSQLDefinitionImpl parsed: \n{}", Utl.buildBeanStateInfo(cursor, "Cursor", "  "));
        return cursor;
    }

    public SQLDefinition pars(final String bioCode) {
        Document document = loadXmlDocumentFromRes(bioCode);
        if (document == null)
            throw new BioSQLException(String.format("Ошибка при загрузке информационного объекта %s!", bioCode));
        return pars(document, bioCode);
    }

    public SQLDefinition pars(final InputStream stream, final String bioCode) {
        Document document = loadXmlDocumentFromInputStream(stream);
        if (document == null)
            throw new BioSQLException(String.format("Ошибка при загрузке информационного объекта %s!", bioCode));
        return pars(document, bioCode);
    }

    public SQLDefinition pars(final String contentRootPath, final String bioCode) {
        Document document = loadXmlDocumentFromPath(contentRootPath, bioCode);
        if (document == null)
            throw new BioSQLException(String.format("Ошибка при загрузке информационного объекта %s!", bioCode));
        return pars(document, bioCode);
    }

    private CursorSqlResolver getCursorSqlResolver() {
        if(cursorSqlResolver == null)
            cursorSqlResolver = new DefaultCursorSqlResolver();
        return cursorSqlResolver;
    }

    public void setCursorSqlResolver(CursorSqlResolver cursorSqlResolver) {
        this.cursorSqlResolver = cursorSqlResolver;
    }
}
