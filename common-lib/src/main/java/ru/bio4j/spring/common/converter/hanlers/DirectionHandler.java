package ru.bio4j.spring.common.converter.hanlers;

import ru.bio4j.spring.common.errors.ConvertValueException;
import ru.bio4j.spring.common.converter.Types;
import ru.bio4j.spring.common.model.Param;

public class DirectionHandler extends TypeHandlerBase implements TypeHandler<Param.Direction> {

    @Override
    public Param.Direction read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null)
            value = 0;
        Class<?> valType = (value == null) ? null : value.getClass();

        if (valType == Param.Direction.class)
            return (Param.Direction)value;
        else if (Types.typeIsNumber(valType))
            return Param.Direction.values()[Types.number2Number((Number)value, int.class)];
        else if (valType == String.class)
            return Param.Direction.decode((String)value);
        throw new ConvertValueException(value, valType, genericType);
    }



    @Override
    public <T> T write(Param.Direction value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (targetTypeWrapped == Param.Direction.class)
            return (T) value;
        else if (targetTypeWrapped == Integer.class)
            return (T) value;
        else if (targetTypeWrapped == String.class)
            return (T) value.toString();
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(Param.Direction value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
