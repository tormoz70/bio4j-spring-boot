package ru.bio4j.spring.database.commons.wrappers;

import ru.bio4j.spring.commons.utils.Lists;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.SortingWrapper;
import ru.bio4j.spring.database.api.WrapperInterpreter;
import ru.bio4j.spring.database.commons.AbstractWrapper;
import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Sort;

import java.util.ArrayList;
import java.util.List;

public class SortingWrapperBaseImpl extends AbstractWrapper implements SortingWrapper {

    public static final String EXPRESSION = "sorting$expression";
    public static final String ORDER_BY_CLAUSE = "${ORDERBYCLAUSE_PLACEHOLDER}";

    private String queryPrefix;
    private String querySuffix;

    public SortingWrapperBaseImpl(String template, WrapperInterpreter wrapperInterpreter) {
        super(template, wrapperInterpreter);
    }

    @Override
    protected void parseTemplate(String template){
        //ищем место куда встявляется запрос
        int queryStart = template.indexOf(QUERY);
        int orderbyStart = template.indexOf(ORDER_BY_CLAUSE);
        if(queryStart < 0)
            throw new IllegalArgumentException("Query: \"" + template + "\" is not contain "+QUERY);
        if(orderbyStart < 0)
            throw new IllegalArgumentException("Query: \"" + template + "\" is not contain "+ORDER_BY_CLAUSE);

        int queryEnd = queryStart + QUERY.length();
        queryPrefix = template.substring(0, queryStart);
        querySuffix = template.substring(queryEnd, orderbyStart - 1);
    }

    public String wrap(String sql, List<Sort> sort, List<Field> fields) {
        if (sort != null && sort.size() > 0) {
            if(fields != null && fields.size() > 0) {
                List<Sort> notFound = new ArrayList<>();
                for (Sort s : sort) {
                    if (!Strings.isNullOrEmpty(s.getFieldName())) {
                        Field fldDef = Lists.first(fields, item -> Strings.compare(s.getFieldName(), item.getName(), true) || Strings.compare(s.getFieldName(), item.getAttrName(), true));
                        if (fldDef != null) {
                            if (!Strings.isNullOrEmpty(fldDef.getSorter()))
                                s.setFieldName(fldDef.getSorter());
                            else
                                s.setFieldName(fldDef.getName());
                            if (s.getNullsPosition() == Sort.NullsPosition.DEFAULT && fldDef.getNullsPosition() != Sort.NullsPosition.DEFAULT)
                                s.setNullsPosition(fldDef.getNullsPosition());
                            if (s.getTextLocality() == Sort.TextLocality.UNDEFINED && fldDef.getTextLocality() != Sort.TextLocality.UNDEFINED)
                                s.setTextLocality(fldDef.getTextLocality());
                        } else
                            notFound.add(s);
                    } else
                        notFound.add(s);
                }
                for (Sort s : notFound)
                    sort.remove(s);
            }

            String orderbySql = wrapperInterpreter.sortToSQL("srtng_wrpr", sort, fields);
            return queryPrefix + sql + querySuffix + (Strings.isNullOrEmpty(orderbySql) ? "" : " ORDER BY " + orderbySql);
        }
        return sql;
    }
}
