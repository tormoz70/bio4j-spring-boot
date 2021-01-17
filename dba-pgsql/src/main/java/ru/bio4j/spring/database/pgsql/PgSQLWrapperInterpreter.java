package ru.bio4j.spring.database.pgsql;


import ru.bio4j.spring.commons.converter.DateTimeParser;
import ru.bio4j.spring.commons.converter.hanlers.DateTimePatterns;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.WrapperInterpreter;
import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.Total;
import ru.bio4j.spring.model.transport.jstore.filter.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PgSQLWrapperInterpreter implements WrapperInterpreter {

    private static final Map<Class<?>, String> compareTemplates = new HashMap<Class<?>, String>() {{
        put(Eq.class, "%s = %s");
        put(Gt.class, "%s > %s");
        put(Ge.class, "%s >= %s");
        put(Lt.class, "%s < %s");
        put(Le.class, "%s <= %s");
        put(Bgn.class, "%s %s %s");
        put(End.class, "%s %s %s");
        put(Contains.class, "%s %s %s");
    }};

    private static Object detectDateTime(Object value, Expression e) {
        if(value instanceof String) {
            String strValue = (String) value;
            Date detectedValue = null;
            String fmtDetected = DateTimePatterns.detectFormat(strValue);
            if (!Strings.isNullOrEmpty(fmtDetected)) {
                if (fmtDetected.equalsIgnoreCase("yyyy-MM-dd")) {
                    if (e instanceof Le)
                        detectedValue = DateTimeParser.getInstance().parse(strValue + "T23:59:59", "yyyy-MM-dd'T'HH:mm:ss");
                    else
                        detectedValue = DateTimeParser.getInstance().parse(strValue, "yyyy-MM-dd");
                } else {
                    detectedValue = DateTimeParser.getInstance().parse(strValue, fmtDetected);
                }
            }
            if (detectedValue != null) {
                SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss");
                return String.format("to_timestamp('%s', 'YYYY-MM-DD\"T\"HH24MISS')", dt1.format(detectedValue));
            }
        }
        return value;
    }

    private static String decodeCompare(String alias, Expression e) {
        String column = appendAlias(alias, e.getColumn());
        Object value = detectDateTime(e.getValue(), e);
        String templ = compareTemplates.get(e.getClass());
        if (e instanceof Bgn)
            value = value + "%";
        if (e instanceof End)
            value = "%" + value;
        if (e instanceof Contains)
            value = "%" + value + "%";
        if (Strings.isString(value)) {
            String strValue = (String)value;
            if(!strValue.startsWith("to_timestamp"))
                value = "'" + value + "'";
        }
        String slike = e.ignoreCase() ? "ilike" : "like";

        if (value != null && value instanceof Date)
            value = "to_date('" + new SimpleDateFormat("YYYYMMdd").format(value) + "', 'YYYYMMDD')";
        String rslt;
        if(e.getClass() == Bgn.class || e.getClass() == End.class || e.getClass() == Contains.class)
            rslt = "("+String.format(templ, column, slike, value)+")";
        else
            rslt = "("+String.format(templ, column, value)+")";
        return rslt;
    }

    private static String appendAlias(String alias, String column){
        return (Strings.isNullOrEmpty(alias) ? ""  : alias+".")+column;
    }

    private String _filterToSQL(String alias, Expression e) {
        if(e instanceof Logical){
            String logicalOp = (e instanceof And) ? " and " : (
                    (e instanceof Or) ? " or " : " unknown-logical "
            );
            StringBuilder rslt = new StringBuilder();
            for(Object chld : e.getChildren()){
                rslt.append(((rslt.length() == 0) ? "" : logicalOp) + this._filterToSQL(alias, (Expression) chld));
            }
            return rslt.length() > 0 ? "("+rslt.toString()+")" : null;
        }

        if(e instanceof Compare){
            return decodeCompare(alias, e);
        }
        if(e instanceof IsNull){
            return "("+appendAlias(alias, e.getColumn()) + " is null)";
        }
        if(e instanceof Not){
            return "not "+this._filterToSQL(alias, (Expression) e.getChildren().get(0))+"";
        }
        return null;
    }

    @Override
    public String filterToSQL(String alias, Filter filter, List<Field> fields) {
        if(filter != null && !filter.getChildren().isEmpty()) {
            Expression e = filter.getChildren().get(0);
            return _filterToSQL(alias, e);
        }
        return null;
    }

    @Override
    public String sortToSQL(String alias, List<Sort> sort, List<Field> fields) {
        if(sort != null) {
            StringBuilder result = new StringBuilder();
            char comma; String fieldName; Sort.Direction direction;
            for (Sort s : sort){
                comma = (result.length() == 0) ? ' ' : ',';
                fieldName = s.getFieldName();
                direction = s.getDirection();
                if(!Strings.isNullOrEmpty(fieldName))
                    result.append(String.format("%s %s.%s %s", comma, alias, fieldName.toUpperCase(), direction.toString()));
            }
            return result.toString();
        }
        return null;
    }

    @Override
    public String totalsToSQL(String alias, List<Total> totals, List<Field> fields) {
        if(totals != null) {
            StringBuilder result = new StringBuilder();
            String comma;
            Total.Aggregate aggregate;
            for (Total t : totals){
                comma = (result.length() == 0) ? " " : ", ";
                final String fieldName = t.getFieldName();
                aggregate = t.getAggregate();
                if(aggregate != Total.Aggregate.UNDEFINED) {
                    if(aggregate == Total.Aggregate.COUNT)
                        result.append(String.format("%sCOUNT(1) as %s", comma, Total.TOTALCOUNT_FIELD_NAME));
                    else
                        result.append(String.format("%s%s(%s.%s) AS %s_%s", comma, aggregate.name(), alias, fieldName, fieldName.toUpperCase(), aggregate.name()));
                }
            }
            return result.toString();
        }
        return null;
    }

}
