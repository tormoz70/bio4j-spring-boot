package ru.bio4j.spring.commons.converter.hanlers;

import ru.bio4j.spring.model.transport.errors.ConvertValueException;
import ru.bio4j.spring.commons.converter.TypeHandler;
import ru.bio4j.spring.commons.converter.TypeHandlerBase;
import ru.bio4j.spring.commons.converter.Types;
import ru.bio4j.spring.commons.utils.Strings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DateHandler extends TypeHandlerBase implements TypeHandler<Date> {

    @Override
    public Date read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null)
            return null;
        value = Types.wrapPrimitive(value);
        Class<?> valType = (value == null) ? null : value.getClass();

        if (Types.typeIsDate(valType))
            return (Date) value;
        else if (valType == LocalDate.class)
            return Types.date2Date((LocalDate)value, Date.class);
        else if (valType == LocalDateTime.class)
            return Types.date2Date((LocalDateTime) value, Date.class);
        else if (Types.typeIsNumber(valType))
            return new java.util.Date(Types.number2Number((Number) value, long.class));
        else if (valType == String.class)
            return Types.parseDate((String) value);
        else
            Types.nop();
        throw new ConvertValueException(value, valType, genericType);
    }

    private static final String DEFAULT_DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

    @Override
    public <T> T write(Date value, Class<T> targetType, String format) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (Types.typeIsDate(targetTypeWrapped))
            return (T) Types.date2Date(value, targetTypeWrapped);
        else if (targetTypeWrapped == LocalDate.class)
            return (T) Types.date2Date(value, LocalDate.class);
        else if (targetTypeWrapped == LocalDateTime.class)
            return (T) Types.date2Date( value, LocalDateTime.class);
        else if (targetTypeWrapped == Boolean.class)
            Types.nop();
        else if (Types.typeIsNumber(targetTypeWrapped))
            return (T) Types.number2Number(value.getTime(), targetTypeWrapped);
        else if (targetTypeWrapped == String.class) {
            DateFormat df = new SimpleDateFormat(Strings.isNullOrEmpty(format) ? DEFAULT_DATETIME_FORMAT : format);
            return (T) df.format(value);
        } else if (targetTypeWrapped == byte[].class)
            Types.nop();
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    @Override
    public <T> T write(Date value, Class<T> targetType) throws ConvertValueException {
        return write(value, targetType, DEFAULT_DATETIME_FORMAT);
    }

}
