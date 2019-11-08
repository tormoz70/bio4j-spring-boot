package ru.bio4j.spring.common.converter.hanlers;


import ru.bio4j.spring.common.converter.Converter;
import ru.bio4j.spring.common.converter.Types;
import ru.bio4j.spring.common.errors.ConvertValueException;
import ru.bio4j.spring.common.stringHelper;

import java.util.ArrayList;
import java.util.List;

public class StringArrayHandler extends TypeHandlerBase implements TypeHandler<String[]> {

    @Override
    public boolean isHandler(Class<?> type) {
        return type.isArray() && type.getComponentType() == String.class;
    }

    @Override
    public String[] read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null)
            return null;
        Class<?> valType = (value == null) ? null : value.getClass();

        if (valType.isArray()) {
            if(valType.getComponentType() == String.class)
                return (String[])value;
            List<String> r = new ArrayList<>();
            for (Object e : ((Object[])value)) {
                r.add(Converter.toType(e, String.class));
            }
            return (String[])r.toArray();
        } else if (Types.typeIsDate(valType))
            return new String[] {value.toString()};
        else if (Types.typeIsNumber(valType))
            return new String[] {value.toString()};
        else if (valType == String.class) {
            List<String> r = new ArrayList<>();
            String[] elems = stringHelper.split((String)value, new String[] {",", ";", " "});
            for(String e : elems)
                r.add(e);
            return (String[])r.toArray();
        } else if (valType == Character.class)
            return new String[] {(String)value};
        throw new ConvertValueException(value, valType, genericType, "");
    }

    @Override
    public <T> T write(String[] value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if(targetType.isArray()) {
            Class<?> targetArrayType = targetType.getComponentType();
            if(targetArrayType == String.class)
                return (T)value;
        } else {
            if (targetTypeWrapped == String.class)
                return (T) String.join(",", value);
        }
        throw new ConvertValueException(value, genericType, targetTypeWrapped, "");
    }

    public <T> T write(String[] value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
