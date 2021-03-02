package ru.bio4j.spring.commons.converter.hanlers;

import ru.bio4j.spring.commons.converter.LocalDateParser;
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

public class LocalDateHandler extends TypeHandlerBase implements TypeHandler<LocalDate> {

    @Override
    public LocalDate read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null)
            return null;
        value = Types.wrapPrimitive(value);
        Class<?> valType = (value == null) ? null : value.getClass();

        if (Types.typeIsDate(valType))
            return Types.date2Date((Date)value, LocalDate.class);
        else if (valType == LocalDate.class)
            return (LocalDate) value;
        else if (valType == LocalDateTime.class)
            return Types.date2Date((LocalDateTime)value, LocalDate.class);
        else if (Types.typeIsNumber(valType))
            return Instant.ofEpochMilli(Types.number2Number((Number) value, long.class)).atZone(ZoneId.systemDefault()).toLocalDate();
        else if (valType == String.class)
            return LocalDateParser.getInstance().parse((String) value);
        else
            Types.nop();
        throw new ConvertValueException(value, valType, genericType);
    }

    private static final String DEFAULT_DATETIME_FORMAT = "dd.MM.yyyy";

    @Override
    public <T> T write(LocalDate value, Class<T> targetType, String format) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (Types.typeIsDate(targetTypeWrapped) || Types.typeIsLocalDate(targetTypeWrapped) || Types.typeIsLocalDateTime(targetTypeWrapped))
            return (T) Types.date2Date(value, targetTypeWrapped);
        else if (targetTypeWrapped == Boolean.class)
            Types.nop();
        else if (Types.typeIsNumber(targetTypeWrapped))
            return (T) Types.number2Number(value.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), targetTypeWrapped);
        else if (targetTypeWrapped == String.class) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(Strings.isNullOrEmpty(format) ? DEFAULT_DATETIME_FORMAT : format);
            return (T) value.format(df);
        } else if (targetTypeWrapped == byte[].class)
            Types.nop();
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    @Override
    public <T> T write(LocalDate value, Class<T> targetType) throws ConvertValueException {
        return write(value, targetType, DEFAULT_DATETIME_FORMAT);
    }

}
