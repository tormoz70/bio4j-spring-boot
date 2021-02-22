package ru.bio4j.spring.commons.converter.hanlers;

import ru.bio4j.spring.commons.converter.LocalDateTimeParser;
import ru.bio4j.spring.commons.converter.TypeHandler;
import ru.bio4j.spring.commons.converter.TypeHandlerBase;
import ru.bio4j.spring.commons.converter.Types;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.model.transport.errors.ConvertValueException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateTimeHandler extends TypeHandlerBase implements TypeHandler<LocalDateTime> {

    @Override
    public LocalDateTime read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null)
            return null;
        value = Types.wrapPrimitive(value);
        Class<?> valType = (value == null) ? null : value.getClass();

        if (Types.typeIsDate(valType))
            return Types.date2Date((Date)value, LocalDateTime.class);
        else if (valType == LocalDate.class)
            return Types.date2Date((LocalDate)value, LocalDateTime.class);
        else if (valType == LocalDateTime.class)
            return (LocalDateTime)value;
        else if (Types.typeIsNumber(valType))
            return Instant.ofEpochMilli(Types.number2Number((Number) value, long.class)).atZone(ZoneId.systemDefault()).toLocalDateTime();
        else if (valType == String.class)
            return LocalDateTimeParser.getInstance().parse((String) value);
        else
            Types.nop();
        throw new ConvertValueException(value, valType, genericType);
    }

    private static final String DEFAULT_DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

    @Override
    public <T> T write(LocalDateTime value, Class<T> targetType, String format) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (Types.typeIsDate(targetTypeWrapped) || Types.typeIsLocalDate(targetTypeWrapped) || Types.typeIsLocalDateTime(targetTypeWrapped))
            return (T) Types.date2Date(value, targetTypeWrapped);
        else if (targetTypeWrapped == Boolean.class)
            Types.nop();
        else if (Types.typeIsNumber(targetTypeWrapped))
            return (T) Types.number2Number(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), targetTypeWrapped);
        else if (targetTypeWrapped == String.class) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(Strings.isNullOrEmpty(format) ? DEFAULT_DATETIME_FORMAT : format);
            return (T) value.format(df);
        } else if (targetTypeWrapped == byte[].class)
            Types.nop();
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    @Override
    public <T> T write(LocalDateTime value, Class<T> targetType) throws ConvertValueException {
        return write(value, targetType, DEFAULT_DATETIME_FORMAT);
    }

}
