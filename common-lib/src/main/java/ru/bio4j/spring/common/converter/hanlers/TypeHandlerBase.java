package ru.bio4j.spring.common.converter.hanlers;


import ru.bio4j.spring.common.converter.Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeHandlerBase {

    protected Class<?> getGenericIfcsType() {
        Type[] genericInterfaces = this.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type rowType = ((ParameterizedType) genericInterface).getRawType();
                if(Types.getClass(rowType) == TypeHandler.class){
                    Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                    if(genericTypes.length > 0)
                        return Types.getClass(genericTypes[0]);
                }
            }
        }
        return null;
    }

    protected Class<?> genericType = getGenericIfcsType();

    public boolean isHandler(Class<?> type) {
        type = Types.wrapPrimitiveType(type);
        return type == genericType ||
                type.getSuperclass() == genericType ||
                genericType.isAssignableFrom(type);
    }
}
