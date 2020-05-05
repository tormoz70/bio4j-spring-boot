package ru.bio4j.spring.database.commons.wrappers;

import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.utils.Lists;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.TotalsWrapper;
import ru.bio4j.spring.database.api.WrapperInterpreter;
import ru.bio4j.spring.database.commons.AbstractWrapper;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Total;

import java.util.ArrayList;
import java.util.List;

public class TotalsWrapperBaseImpl extends AbstractWrapper implements TotalsWrapper {

    private String template;

    public TotalsWrapperBaseImpl(String template, WrapperInterpreter wrapperInterpreter) {
        super(template, wrapperInterpreter);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public String wrap(String sql, List<Total> totals, List<Field> fields) {
        if (totals != null && totals.size() > 0) {
            if(fields != null && fields.size() > 0) {
                List<Total> notFound = new ArrayList<>();
                for (Total t : totals) {
                    if(t.getAggregate() == Total.Aggregate.COUNT) {
                        t.setFieldName("*");
                        t.setFieldType(long.class);
                        continue;
                    }
                    if (!Strings.isNullOrEmpty(t.getFieldName())) {
                        Field fldDef = Lists.first(fields, item -> Strings.compare(t.getFieldName(), item.getName(), true) || Strings.compare(t.getFieldName(), item.getAttrName(), true));
                        if (fldDef != null) {
                            t.setFieldName(fldDef.getName());
                            if(t.getFieldType() == null)
                                t.setFieldType(fldDef.getMetaType() != MetaType.UNDEFINED ? MetaTypeConverter.write(fldDef.getMetaType()) : double.class);
                            if (t.getAggregate() == Total.Aggregate.UNDEFINED && fldDef.getAggregate() != Total.Aggregate.UNDEFINED)
                                t.setAggregate(fldDef.getAggregate());
                        } else
                            notFound.add(t);
                    } else
                        notFound.add(t);
                }
                for (Total t : notFound)
                    totals.remove(t);
                for(Field field : fields){
                    if(Utl.nvl(field.getAggregate(), Total.Aggregate.UNDEFINED) != Total.Aggregate.UNDEFINED) {
                        Total exists = totals.stream().filter(f -> Strings.compare(f.getFieldName(), field.getName(), true) || Strings.compare(f.getFieldName(), field.getAttrName(), true)).findFirst().orElse(null);
                        if(exists == null)
                            totals.add(Total.builder().fieldName(field.getName()).aggrigate(field.getAggregate()).build());
                    }
                }
            }

            String totalsSql = wrapperInterpreter.totalsToSQL("ttls_wrpr", totals, fields);
            return template.replace(TOTALS_FIELDS_PLACEHOLDER, totalsSql).replace(QUERY_PLACEHOLDER, sql);
        }
        return sql;
    }
}
