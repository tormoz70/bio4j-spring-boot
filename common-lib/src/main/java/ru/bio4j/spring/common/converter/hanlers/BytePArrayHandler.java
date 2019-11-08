package ru.bio4j.spring.common.converter.hanlers;

import ru.bio4j.spring.common.converter.Converter;
import ru.bio4j.spring.common.converter.Types;
import ru.bio4j.spring.common.errors.ConvertValueException;
import ru.bio4j.spring.common.stringHelper;

public class BytePArrayHandler extends TypeHandlerBase implements TypeHandler<byte[]> {

    @Override
    public boolean isHandler(Class<?> type) {
        return type.isArray() && (type.getComponentType() == byte.class);
    }

    @Override
    public byte[] read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null)
            return null;
        Class<?> valType = (value == null) ? null : value.getClass();

        if (valType.isArray()) {
            if(valType.getComponentType() == String.class) {
                String[] strs = (String[])value;
                byte[] r = new byte[strs.length];
                for(int i=0; i<strs.length; i++)
                    r[i] = Byte.parseByte(strs[i]);
                return r;
            }
            if(valType.getComponentType() == Byte.class)
                return Types.toPrimitives((Byte[]) value);
            if(valType.getComponentType() == byte.class)
                return (byte[])value;

            Object[] objs = (Object[])value;
            byte[] r = new byte[objs.length];
            for(int i=0; i<objs.length; i++)
                r[i] = Converter.toType(objs[i], byte.class);
            return r;
        } else if (Types.typeIsDate(valType))
            return value.toString().getBytes();
        else if (Types.typeIsNumber(valType))
            return new byte[] {((Number)value).byteValue()};
        else if (valType == String.class) {
            String[] strs = stringHelper.split((String)value, new String[] {",", ";", " "});
            byte[] r = new byte[strs.length];
            for(int i=0; i<strs.length; i++)
                r[i] = Byte.parseByte(strs[i]);
            return r;
        } else if (valType == Character.class)
            return new byte[] {(byte)value};
        throw new ConvertValueException(value, valType, genericType);
    }

    @Override
    public <T> T write(byte[] value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (targetType.isArray()) {
            Class<?> targetArrayType = targetType.getComponentType();
            if (targetArrayType == Byte.class) {
                return (T) Types.toObjects(value);
            } else if (targetArrayType == byte.class) {
                return (T) value;
            }
        } else {
            if (targetTypeWrapped == String.class)
                return (T) stringHelper.combineArray(value, ",");
        }
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(byte[] value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
