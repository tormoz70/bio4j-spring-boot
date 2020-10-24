package ru.bio4j.spring.commons.utils;

import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.model.transport.Prop;
import ru.bio4j.spring.model.transport.errors.AccessToBeanFieldException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Reflex {

    public static Field findFieldOfBean(Class<?> type, String fieldName) {
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
            Prop p = findAnnotation(Prop.class, fld);
            String annotatedFieldName = null;
            if (p != null)
                annotatedFieldName = p.name();
            if (Strings.compare(fld.getName(), fieldName, true) ||
                    (!Strings.isNullOrEmpty(annotatedFieldName) && Strings.compare(annotatedFieldName, fieldName, true)))
                return fld;
        }
        return null;
    }

    private static void extractAllObjectFields(List<Field> fields, Class<?> type) {
        for (Field field : type.getDeclaredFields())
            fields.add(field);
        if (type.getSuperclass() != null)
            extractAllObjectFields(fields, type.getSuperclass());
    }

    public static List<Field> getAllObjectFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        extractAllObjectFields(fields, type);
        return fields;
    }

    public static String fieldValueAsString(Object bean, Field field) {
        Object val;
        try {
            field.setAccessible(true);
            val = field.get(bean);
        } catch (IllegalAccessException ex) {
            throw new AccessToBeanFieldException(ex);
        }
        String valStr = (val instanceof String) ? ((String) val).trim() : null;
        if (!Strings.isNullOrEmpty(valStr) && valStr.indexOf("\n") >= 0) {
            return valStr.substring(0, valStr.indexOf("\n")) + "...";
        } else
            return "" + val;
    }

    public static Object fieldValueAsObject(Object bean, Field field) {
        Object val;
        try {
            field.setAccessible(true);
            val = field.get(bean);
        } catch (IllegalAccessException ex) {
            throw new AccessToBeanFieldException(ex);
        }
        return val;
    }

    public static Object fieldValueAsObject(Object bean, String fieldName) {
        Object val = null;
        if (bean != null) {
            java.lang.reflect.Field field = findFieldOfBean(bean.getClass(), fieldName);
            if (field != null) {
                try {
                    field.setAccessible(true);
                    val = field.get(bean);
                } catch (IllegalAccessException ex) {
                    throw new AccessToBeanFieldException(ex);
                }
            }
        }
        return val;
    }

    public static <T> T fieldValue(Object bean, String fieldName, Class<T> clazz) {
        T val = null;
        if (bean != null) {
            java.lang.reflect.Field field = findFieldOfBean(bean.getClass(), fieldName);
            if (field != null) {
                try {
                    field.setAccessible(true);
                    val = Converter.toType(field.get(bean), clazz);
                } catch (IllegalAccessException ex) {
                    throw new AccessToBeanFieldException(ex);
                }
            }
        }
        return val;
    }

    public static boolean typeHasInterface(Class<?> clazz, Class<?> serviceInterface) {
        Class[] interfaces = clazz.getInterfaces();
        for (Class i : interfaces) {
            if (i.toString().equals(serviceInterface.toString()))
                return true;
        }
        return false;
    }

    /**
     * @param annotationType - Type of annotation to find
     * @param clazz          - Annotated type
     * @return - Annotation object
     */
    public static <T extends Annotation> T findAnnotation(Class<T> annotationType, Class<?> clazz) {
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            Class<?> atype = annotation.annotationType();
            if (typesIsAssignable(atype, annotationType))
                return (T) annotation;
        }
        return null;
    }

    public static <T extends Annotation> T findAnnotation(Class<T> annotationType, Field field) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            Class<?> atype = annotation.annotationType();
            if (typesIsAssignable(atype, annotationType))
                return (T) annotation;
        }
        return null;
    }

    /**
     * Checks two classes is assignable
     *
     * @param clazz1
     * @param clazz2
     * @return boolean
     * @throws
     */
    public static boolean typesIsAssignable(Class<?> clazz1, Class<?> clazz2) {
        if ((clazz1 == null) && (clazz2 == null)) return true;
        if (((clazz1 != null) && (clazz2 == null)) || ((clazz1 == null) && (clazz2 != null))) return false;
        return (clazz1 == clazz2) || clazz1.isAssignableFrom(clazz2) || clazz2.isAssignableFrom(clazz1);
    }

    /**
     * Checks two classes is the same
     *
     * @param clazz1
     * @param clazz2
     * @return boolean
     * @throws
     */
    public static boolean typesIsSame(Class<?> clazz1, Class<?> clazz2) {
        if ((clazz1 == null) || (clazz2 == null)) return true;
        if (((clazz1 != null) && (clazz2 == null)) || ((clazz1 == null) && (clazz2 != null))) return false;
        return clazz1.getName().equals(clazz2.getName());
    }

}
