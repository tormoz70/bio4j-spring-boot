package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.DelegateCheck;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.database.api.BioSQLException;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Alignment;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.database.api.SQLDef;
import ru.bio4j.ng.database.api.SQLType;
import ru.bio4j.ng.database.api.SelectSQLDef;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CursorParser {
    private static final Logger LOG = LoggerFactory.getLogger(CursorParser.class);


    private static final String ATTRS_DELIMITER = ";";
    private static final String ATTRS_KEYVALUE_DELIMITER = ":";

    private static final String PARAM_PREFIX = "param.";
    private static final String REGEX_PARAMS = "(/\\*\\$\\{" + PARAM_PREFIX + ".*?\\}\\*/)";
    private static final String REGEX_ATTRS = "(?<=/\\*\\$\\{).*(?=\\}\\*/)";
    private static final String REGEX_PARAM_KILLDEBUG = "debug:\\*/.*/\\*";

    private static Param parseParam(String paramDef) {
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

    private static void parseCol(List<Field> cols, String colDef) {
        String attrsList = Regexs.find(colDef, REGEX_ATTRS, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);
        // Заменяем все внутренние(экранированные) ковычки на QUOTES_PLACEHOLDER
        attrsList = Regexs.replace(attrsList, REGEX_QUOTES_REPLACER, QUOTES_PLACEHOLDER, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        // Вытаскиваем имя колонки
        String name = Regexs.find(attrsList, REGEX_COLS_NAME, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);
        if (Strings.isNullOrEmpty(name))
            throw new IllegalArgumentException("Attribute \"col.name\" not found in descriptor!");
        name = Strings.split(name, ".")[1].trim().toLowerCase();

        Field col = findCol(name, cols);
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

    private static void addParamsFromXml(final SQLDef sqlDef, final Element sqlElem) {
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

    private static void addParamsFromSQLBody(final SQLDef sqlDef) {
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

    private static Field findCol(final String name, final List<Field> cols) {
        return Lists.first(cols, new DelegateCheck<Field>() {
            @Override
            public Boolean callback(Field item) {
                return Strings.compare(name, item.getName(), true);
            }
        });
    }

    private static void addColsFromXml(final SQLDefinitionImpl cursor, final Document document) {
        Element sqlElem = Doms.findElem(document, "/cursor/fields");
        NodeList fieldNodes = sqlElem.getElementsByTagName("field");
        List<Field> fields = cursor.getFields();
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Element paramElem = (Element) fieldNodes.item(i);
            boolean generate = Converter.toType(Doms.getAttribute(paramElem, "generate", "true", String.class), boolean.class);
            if (generate) {
                String fieldName = Doms.getAttribute(paramElem, "name", "", String.class);
                String attrName = Doms.getAttribute(paramElem, "attrName", null, String.class);
                Field col = findCol(fieldName, fields);
                if (col == null) {
                    col = new Field();
                    fields.add(col);
                    col.setName(fieldName);
                    col.setAttrName(attrName);
                }
                col.setId(i + 1);
                col.setFormat(Doms.getAttribute(paramElem, "format", null, String.class));
                String header = Doms.getAttribute(paramElem, "header", null, String.class);
                if (Strings.isNullOrEmpty(header))
                    header = paramElem.getTextContent();
                if (Strings.isNullOrEmpty(header))
                    header = fieldName;
                col.setTitle(header);
                col.setMetaType(Converter.toType(Doms.getAttribute(paramElem, "type", "string", String.class), MetaType.class));
                col.setAlign(Converter.toType(Doms.getAttribute(paramElem, "align", "left", String.class), Alignment.class));
                col.setHidden(Converter.toType(Doms.getAttribute(paramElem, "hidden", "false", String.class), boolean.class));
                col.setFilter(Converter.toType(Doms.getAttribute(paramElem, "filter", "false", String.class), boolean.class));
                col.setShowTooltip(Converter.toType(Doms.getAttribute(paramElem, "showTooltip", "false", String.class), boolean.class));
                col.setDefaultVal(Doms.getAttribute(paramElem, "defaultVal", null, String.class));
                col.setPk(Converter.toType(Doms.getAttribute(paramElem, "pk", "false", String.class), boolean.class));
                col.setUseNull(Converter.toType(Doms.getAttribute(paramElem, "useNull", "true", String.class), boolean.class));
                col.setReadonly(Converter.toType(Doms.getAttribute(paramElem, "readOnly", "true", String.class), boolean.class));
                col.setWidth(Doms.getAttribute(paramElem, "width", null, String.class));
                col.setExpEnabled(Converter.toType(Doms.getAttribute(paramElem, "expEnabled", "true", String.class), boolean.class));
                col.setExpFormat(Doms.getAttribute(paramElem, "expFormat", null, String.class));
                col.setExpWidth(Doms.getAttribute(paramElem, "expWidth", null, String.class));
                col.setSorter(Doms.getAttribute(paramElem, "sorter", null, String.class));
                col.setNullsPosition(Converter.toType(Doms.getAttribute(paramElem, "nullsPosition", "DEFAULT", String.class), Sort.NullsPosition.class));
                col.setTextLocality(Converter.toType(Doms.getAttribute(paramElem, "textLocality", "UNDEFINED", String.class), Sort.TextLocality.class));
                col.setTooltip(Doms.getAttribute(paramElem, "tooltip", null, String.class));
                col.setMandatory(Converter.toType(Doms.getAttribute(paramElem, "mandatory", "false", String.class), boolean.class));
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

    private static String PATTERN_EXTRACT_FILE_NAME = "(?<=\\{text-file:)(\\w|-)+\\.sql(?=\\})";

    private static String tryLoadSQL(final String bioCode, String sqlText) {
        Matcher m = Regexs.match(sqlText, PATTERN_EXTRACT_FILE_NAME, Pattern.CASE_INSENSITIVE);
        if (m.find()) {
            String sqlFileName = Utl.extractBioParentPath(bioCode) + Utl.DEFAULT_BIO_PATH_SEPARATOR + m.group();
            try {
                sqlText = Strings.loadResourceAsString(sqlFileName);
            } catch (IOException e) {
                throw Utl.wrapErrorAsRuntimeException(String.format("Файл %s, на который ссылается объект %s не наден в ресурсах!", sqlFileName, bioCode));
            }
        }
        return sqlText;
    }

    private static String tryLoadSQL(final String contentPath, final String bioCode, String sqlText) {
        Matcher m = Regexs.match(sqlText, PATTERN_EXTRACT_FILE_NAME, Pattern.CASE_INSENSITIVE);
        if (m.find()) {
            String sqlFileName = Utl.extractBioParentPath(bioCode) + Utl.DEFAULT_BIO_PATH_SEPARATOR + m.group();
            Path p = Paths.get(sqlFileName);
            try {
                if (Files.exists(p))
                    try (InputStream is = Utl.openFile(sqlFileName)) {
                        sqlText = Utl.readStream(is);
                    }
                else
                    throw new IOException(String.format("Файл %s, на который ссылается объект %s не наден в ресурсах!", sqlFileName, bioCode));
            } catch(IOException e) {
                throw Utl.wrapErrorAsRuntimeException(e);
            }
        }
        return sqlText;
    }

    private static Document loadXmlDocumentFromInputStream(final InputStream inputStream) {
        return Utl.loadXmlDocument(inputStream);
    }

    private static Document loadXmlDocumentFromRes(final String bioCode) {
        String path = Utl.extractBioPath(bioCode);
        URL url = Strings.findResource(path + ".xml");
        if (url != null) {
            LOG.debug("Loading cursor spec from \"{}\"", path + ".xml");
            try {
                try (InputStream inputStream = url.openStream()) {
                    Document document = Utl.loadXmlDocument(inputStream);
                    return document;
                }
            } catch(IOException e) {
                throw Utl.wrapErrorAsRuntimeException(e);
            }
        }
        return null;
    }

    public static SQLDefinitionImpl pars(final Document document, final String bioCode) {
        SQLDefinitionImpl cursor = new SQLDefinitionImpl(bioCode);
        Element exportTitleElem = Doms.findElem(document.getDocumentElement(), "/cursor/exportTitle");
        Boolean readOnly = Doms.getAttribute(document.getDocumentElement(), "readOnly", true, Boolean.class);
        cursor.setReadOnly(readOnly);
        Boolean multiSelection = Doms.getAttribute(document.getDocumentElement(), "multiselection", false, Boolean.class);
        cursor.setMultiSelection(multiSelection);

        if (exportTitleElem != null)
            cursor.setExportTitle(exportTitleElem.getTextContent());
        addColsFromXml(cursor, document); // добавляем колонки из XML
        List<Element> sqlTextElems = Doms.findElems(document.getDocumentElement(), "/cursor/SQL");
        for (Element sqlElem : sqlTextElems) {
            SQLType curType = Doms.getAttribute(sqlElem, "action", SQLType.SELECT, SQLType.class);
            String sql = sqlElem.getTextContent().trim();
            sql = tryLoadSQL(bioCode, sql);
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

    public static SQLDefinitionImpl pars(final String bioCode) {
        Document document = loadXmlDocumentFromRes(bioCode);
        if (document == null)
            throw new BioSQLException(String.format("Описание информационного объекта %s не найдено в системе!", bioCode));
        return pars(document, bioCode);
    }

    public static SQLDefinitionImpl pars(final InputStream stream, final String bioCode) {
        Document document = loadXmlDocumentFromInputStream(stream);
        if (document == null)
            throw new BioSQLException(String.format("Ошибка при загрузке информационного объекта %s!", bioCode));
        return pars(document, bioCode);
    }

    private static String buildPath(String path, String bioCode, String extension) {
        return path + File.separator + bioCode.replace(".", File.separator) + extension;
    }

    private static Document loadXmlDocumentFromPath(final String contentRootPath, final String bioCode) {
        String path = buildPath(contentRootPath, bioCode, ".xml");
        return Utl.loadXmlDocument(path);
    }

    public static SQLDefinitionImpl pars(final String contentRootPath, final String bioCode) {
        Document document = loadXmlDocumentFromPath(contentRootPath, bioCode);
        SQLDefinitionImpl cursor = new SQLDefinitionImpl(bioCode);
        Element exportTitleElem = Doms.findElem(document.getDocumentElement(), "/cursor/exportTitle");
        if (exportTitleElem != null)
            cursor.setExportTitle(exportTitleElem.getTextContent());
        addColsFromXml(cursor, document); // добавляем колонки из XML
        List<Element> sqlTextElems = Doms.findElems(document.getDocumentElement(), "/cursor/SQL");
        for (Element sqlElem : sqlTextElems) {
            SQLType curType = Doms.getAttribute(sqlElem, "action", SQLType.SELECT, SQLType.class);
            String sql = tryLoadSQL(contentRootPath, bioCode, sqlElem.getTextContent().trim());
            SQLDef sqlDef;
            if (curType == SQLType.SELECT)
                sqlDef = new SQLDefinitionImpl.SelectSQLDefImpl(sql);
            else
                sqlDef = new SQLDefinitionImpl.UpdelexSQLDefImpl(sql);
            cursor.setSqlDef(curType, sqlDef);

            addParamsFromSQLBody(sqlDef); // добавляем переменные из SQL
            addParamsFromXml(sqlDef, sqlElem); // добавляем переменные из XML
        }
        //LOG.debug("BioSQLDefinitionImpl parsed: \n{}", Utl.buildBeanStateInfo(cursor, "Cursor", "  "));
        return cursor;
    }
}
