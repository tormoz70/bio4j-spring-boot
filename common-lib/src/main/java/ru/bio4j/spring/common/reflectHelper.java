package ru.bio4j.spring.common;


import ru.bio4j.spring.common.converter.Converter;
import ru.bio4j.spring.common.errors.ApplyValuesToBeanException;
import ru.bio4j.spring.common.errors.BioError;
import ru.bio4j.spring.common.model.Mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class reflectHelper {
    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw BioError.wrap(e);
        }
    }

    public static boolean typesIsAssignable(Class<?> clazz1, Class<?> clazz2) {
        if ((clazz1 == null) && (clazz2 == null)) return true;
        if (((clazz1 != null) && (clazz2 == null)) || ((clazz1 == null) && (clazz2 != null))) return false;
        return (clazz1 == clazz2) || clazz1.isAssignableFrom(clazz2) || clazz2.isAssignableFrom(clazz1);
    }

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

    private static Field findFieldOfBean(Class<?> type, String fieldName) {
        for (Field fld : getAllObjectFields(type)) {
            Mapper p = findAnnotation(Mapper.class, fld);
            String annotatedFieldName = null;
            if (p != null)
                annotatedFieldName = p.name();
            if (stringHelper.compare(fld.getName(), fieldName, true) ||
                    (!stringHelper.isNullOrEmpty(annotatedFieldName) && stringHelper.compare(annotatedFieldName, fieldName, true)))
                return fld;
        }
        return null;
    }

    public static Object arrayCopyOf(Object original) {
        if (original != null && original.getClass().isArray()) {
            int l = ((Object[]) original).length;
            Class<?> originalType = original.getClass();
            Object rslt = Array.newInstance(originalType.getComponentType(), l);
            System.arraycopy(original, 0, rslt, 0, l);
            return rslt;
        }
        return null;
    }

    public static boolean applyValuesToBeanFromBean(Object srcBean, Object bean) {
        boolean result = false;
        if (srcBean == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if (bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> srcType = srcBean.getClass();
        Class<?> type = bean.getClass();
        for (Field fld : getAllObjectFields(type)) {
            String fldName = fld.getName();
            Field srcFld = findFieldOfBean(srcType, fldName);
            if (srcFld == null)
                continue;
            try {
                srcFld.setAccessible(true);
                Object valObj = srcFld.get(srcBean);
                if (valObj != null) {
                    Object val;
                    if (valObj.getClass().isArray()) {
                        val = arrayCopyOf(valObj);
                    } else {
                        val = (fld.getType().equals(Object.class) || fld.getType().equals(valObj.getClass())) ? valObj : Converter.toType(valObj, fld.getType());
                    }
                    fld.setAccessible(true);
                    fld.set(bean, val);
                    result = true;
                }
            } catch (Exception e) {
                String msg = String.format("Can't set value to field. Msg: %s", e.getMessage());
                throw new ApplyValuesToBeanException(fldName, msg, e);
            }
        }
        return result;
    }

    public static Object cloneBean(Object bean) {
        if (bean != null && !bean.getClass().isPrimitive()) {
            Class<?> type = bean.getClass();
            Object newBean = newInstance(type);
            applyValuesToBeanFromBean(bean, newBean);
            return newBean;
        }
        return null;
    }

    public static <T> T cloneBean1(T bean, Class<T> clazz) {
        if (bean != null && !clazz.isPrimitive()) {
            T newBean = newInstance(clazz);
            applyValuesToBeanFromBean(bean, newBean);
            return newBean;
        }
        return null;
    }

    public static Object getFieldValue(Field fld, Object bean) {
        try {
            return fld.get(bean);
        } catch (IllegalAccessException e) {
            throw BioError.wrap(e);
        }
    }

    public static void setFieldValue(Field fld, Object bean, Object value) {
        try {
            fld.set(bean, value);
        } catch (IllegalAccessException e) {
            throw BioError.wrap(e);
        }
    }

}
