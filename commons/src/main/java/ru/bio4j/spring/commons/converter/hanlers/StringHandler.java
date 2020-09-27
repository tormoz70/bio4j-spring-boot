package ru.bio4j.spring.commons.converter.hanlers;

import ru.bio4j.spring.commons.converter.*;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.model.transport.errors.ConvertValueException;

import java.lang.reflect.Array;

public class StringHandler extends TypeHandlerBase implements TypeHandler<String> {

    @Override
    public <T> T write(String value, Class<T> targetType) throws ConvertValueException {
        Object result = null;
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (Types.typeIsDate(targetTypeWrapped))
            result = Types.date2Date(Types.parsDate(value), targetTypeWrapped);
        else if (targetTypeWrapped == Boolean.class)
            result = Types.parsBoolean(value);
        else if (Types.typeIsNumber(targetTypeWrapped)) {
            if(Strings.isNullOrEmpty(value))
                result = null;
            else
                result = Types.string2Number(value, targetTypeWrapped);
        } else if (targetTypeWrapped == String.class)
            result = value;
        else if (targetTypeWrapped == Character.class)
            result = value;
        else if (targetTypeWrapped == byte[].class)
            result = value.getBytes();
        else if (targetTypeWrapped.isEnum())
            result = Types.parsEnum(value, targetTypeWrapped);
        else if (targetTypeWrapped.isArray() && Types.isPrimitiveOrWrapper(targetTypeWrapped.getComponentType())){
            String[] valStrs = Strings.split(value, ",");
            result = Array.newInstance(targetTypeWrapped.getComponentType(), valStrs.length);
            for (int i=0; i<valStrs.length; i++)
                Array.set(result, i, Converter.toType(valStrs[i], targetTypeWrapped.getComponentType()));
        }
        if (result != null)
            return (T) result;
        else {
            if (!targetType.isPrimitive())
                return (T) result;
        }
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(String value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
