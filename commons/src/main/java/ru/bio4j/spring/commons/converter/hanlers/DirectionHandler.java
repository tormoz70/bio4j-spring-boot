package ru.bio4j.spring.commons.converter.hanlers;

import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.ConvertValueException;
import ru.bio4j.spring.commons.converter.TypeHandler;
import ru.bio4j.spring.commons.converter.TypeHandlerBase;
import ru.bio4j.spring.commons.converter.Types;

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
