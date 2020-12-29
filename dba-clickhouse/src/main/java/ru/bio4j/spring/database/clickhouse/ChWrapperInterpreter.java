package ru.bio4j.spring.database.clickhouse;


import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.WrapperInterpreter;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.Total;
import ru.bio4j.spring.model.transport.jstore.filter.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChWrapperInterpreter implements WrapperInterpreter {

    private static final Map<Class<?>, String> compareTemplates = new HashMap<Class<?>, String>() {{
        put(Eq.class, "%s = %s");
        put(Gt.class, "%s > %s");
        put(Ge.class, "%s >= %s");
        put(Lt.class, "%s < %s");
        put(Le.class, "%s <= %s");
        put(Bgn.class, "%s like %s");
        put(End.class, "%s like %s");
        put(Contains.class, "%s like %s");
    }};

    private static String decodeCompare(String alias, Expression e) {
        String column = appendAlias(alias, e.getColumn());
        Object value = e.getValue();
        String templ = compareTemplates.get(e.getClass());
        if(e instanceof Bgn)
            value = value+"%";
        if(e instanceof End)
            value = "%"+value;
        if(e instanceof Contains)
            value = "%"+value+"%";
        if (Strings.isString(value))
            value = "'"+value+"'";
        if (e.ignoreCase()) {
            value = "upper("+value+")";
            column = "upper("+column+")";
        }

        if (value != null && value instanceof Date) {
            String valueFrom = "to_date('" + new SimpleDateFormat("YYYYMMdd").format(value) + "-00:00:00', 'YYYYMMDD-HH24:MI:SS')";
            String valueTo = "to_date('" + new SimpleDateFormat("YYYYMMdd").format(value) + "-23:59:59', 'YYYYMMDD-HH24:MI:SS')";
            return String.format("%s between %s and %s", column, valueFrom, valueTo);
        }
        return "("+String.format(templ, column, value)+")";
    }

    private static String appendAlias(String alias, String column){
        return (Strings.isNullOrEmpty(alias) ? ""  : alias+".")+column;
    }

    private String _filterToSQL(String alias, Expression e) {
        if (e instanceof Logical) {
            String logicalOp = (e instanceof And) ? " and " : (
                    (e instanceof Or) ? " or " : " unknown-logical "
            );
            StringBuilder rslt = new StringBuilder();
            for (Object chld : e.getChildren()) {
                rslt.append(((rslt.length() == 0) ? "" : logicalOp) + this._filterToSQL(alias, (Expression) chld));
            }
            return "(" + rslt.toString() + ")";
        }

        if (e instanceof Compare) {
            return decodeCompare(alias, e);
        }
        if (e instanceof IsNull) {
            return "(" + appendAlias(alias, e.getColumn()) + " is null)";
        }
        if (e instanceof Not) {
            return "not " + this._filterToSQL(alias, (Expression) e.getChildren().get(0)) + "";
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
            char comma; Sort.Direction direction; String nullsPos = null;
            for (Sort s : sort){
                comma = (result.length() == 0) ? ' ' : ',';
                final String fieldName = s.getFieldName();
                direction = s.getDirection();
                nullsPos = "NULLS LAST";
                if(s.getNullsPosition() == Sort.NullsPosition.DEFAULT)
                    nullsPos = "";
                if(s.getNullsPosition() == Sort.NullsPosition.NULLFIRST)
                    nullsPos = "NULLS FIRST";
                if(!Strings.isNullOrEmpty(fieldName)) {
                    Sort.TextLocality textLocality = s.getTextLocality();
                    if(textLocality == Sort.TextLocality.UNDEFINED) {
                        Field field = null;
                        if (fields != null) {
                            field = fields.stream().filter(f -> (Strings.compare(f.getName(), fieldName, true) || Strings.compare(f.getSorter(), fieldName, true))).findAny().orElse(null);
                            if(field != null && field.getMetaType() == MetaType.STRING)
                                textLocality = Sort.TextLocality.RUSSIAN;
                        }
                    }
                    if(textLocality == Sort.TextLocality.RUSSIAN)
                        result.append(String.format("%s NLSSORT(%s.%s, 'NLS_SORT=RUSSIAN') %s %s", comma, alias, fieldName, direction.toString(), nullsPos));
                    else
                        result.append(String.format("%s %s.%s %s %s", comma, alias, fieldName, direction.toString(), nullsPos));
                }
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
                comma = (result.length() == 0) ? "" : ", ";
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
