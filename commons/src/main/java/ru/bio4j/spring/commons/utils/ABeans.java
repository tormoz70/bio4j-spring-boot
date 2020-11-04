package ru.bio4j.spring.commons.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.ReflectionUtils;
import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.DateTimeParser;
import ru.bio4j.spring.model.transport.ABean;
import ru.bio4j.spring.model.transport.Prop;
import ru.bio4j.spring.model.transport.errors.ApplyValuesToBeanException;

import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static ru.bio4j.spring.commons.utils.Lists.arrayCopyOf;
import static ru.bio4j.spring.commons.utils.Reflex.*;
import static ru.bio4j.spring.commons.utils.Reflex.findAnnotation;

public class ABeans {

    public static Map<String, Object> extractBean(final Map<String, Object> bean, final String attrName) {
        if(bean != null) {
            for (String key : bean.keySet()) {
                if (Strings.compare(key, attrName, true)) {
                    if(bean.get(key) instanceof Map)
                    return (Map<String, Object>) bean.get(key);
                }
            }
        }
        return null;
    }

    public static String getNextPathItem(String path) {
        if(!Strings.isNullOrEmpty(path))
            return path.indexOf("/") >= 0 ? path.substring(0, path.indexOf("/")) : path;
        return null;
    }

    public static String cutNextPathItem(String path) {
        if(!Strings.isNullOrEmpty(path) && path.indexOf("/") >= 0)
            return path.substring(path.indexOf("/")+1);
        return null;
    }

    public static <T> T extractAttrFromBean(final Map<String, Object> bean, final String path, Class<T> clazz, T defauldValue) {
        if(bean != null) {
            if(Strings.isNullOrEmpty(path))
                return defauldValue;
            if(path.indexOf("/") >= 0){
                String nextLevelAttr = getNextPathItem(path);
                Map<String, Object> hextBean = extractBean(bean, nextLevelAttr);
                return extractAttrFromBean(hextBean, cutNextPathItem(path), clazz, defauldValue);
            } else {
                for (String key : bean.keySet()) {
                    if (Strings.compare(key, path, true))
                        return Converter.toType(bean.get(key), clazz);
                }
            }
        }
        return defauldValue;
    }

    public static Object getPropertyValue(Object bean, String propName) {
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName()) && propName.equals(pd.getName())) {
                    return pd.getReadMethod().invoke(bean);
                }
            }
            return null;
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Map<String, Object> extractBeanFromBean(final Map<String, Object> bean, final String path) throws Exception {
        if(bean != null) {
            if(Strings.isNullOrEmpty(path))
                return null;
            if(path.indexOf("/") >= 0){
                String nextLevelAttr = getNextPathItem(path);
                Map<String, Object> hextBean = extractBean(bean, nextLevelAttr);
                return extractBeanFromBean(hextBean, cutNextPathItem(path));
            } else {
                for (String key : bean.keySet()) {
                    if (Strings.compare(key, path, true))
                        return (Map<String, Object>)bean.get(key);
                }
            }
        }
        return null;
    }

    public static List<Map<String, Object>> extractBeansFromBean(final Map<String, Object> bean, final String path) throws Exception {
        if(bean != null) {
            if(Strings.isNullOrEmpty(path))
                return null;
            if(path.indexOf("/") >= 0){
                String nextLevelAttr = getNextPathItem(path);
                Map<String, Object> hextBean = extractBean(bean, nextLevelAttr);
                return extractBeansFromBean(hextBean, cutNextPathItem(path));
            } else {
                for (String key : bean.keySet()) {
                    if (Strings.compare(key, path, true))
                        return (List<Map<String, Object>>)bean.get(key);
                }
            }
        }
        return null;
    }

    public static void renameAttr(final Map<String, Object> bean, final String oldAttrName, final String newAttrName) throws Exception {
        Object val = null;
        String foundKey = null;
        for (String key : bean.keySet()) {
            if (Strings.compare(key, oldAttrName, true)){
                val = bean.get(key);
                foundKey = key;
            }
        }
        if(foundKey != null){
            bean.remove(foundKey);
            bean.put(newAttrName, val);
        }
    }

    public static Method getMethodOfBean(Object bean, String methodName) {
        try {
            for (MethodDescriptor md : Introspector.getBeanInfo(bean.getClass()).getMethodDescriptors()) {
                if(md.getName().equalsIgnoreCase(methodName))
                    return md.getMethod();
            }
            return null;
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void setPropertyValue(Object bean, String propName, Object value, String dateTimeFormat) {
        if(bean == null)
            throw new IllegalArgumentException("Parameter \"bean\" cannot be null!");
        if(Strings.isNullOrEmpty(propName))
            throw new IllegalArgumentException("Parameter \"propName\" cannot be null or empty!");
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
                if(pd.getName().equalsIgnoreCase(propName)) {
                    Method method = pd.getWriteMethod();
                    if(method == null)
                        method = getMethodOfBean(bean, "set"+propName);
                    if (method != null) {
                        try {
                            Object valueLocal;
                            if (value instanceof Date && pd.getPropertyType() == String.class)
                                valueLocal = DateFormatUtils.format((Date) value, dateTimeFormat);
                            else if (value instanceof String && pd.getPropertyType() == Date.class)
                                valueLocal = DateTimeParser.getInstance().pars((String) value, dateTimeFormat);
                            else
                                valueLocal = Converter.toType(value, pd.getPropertyType());
                            method.invoke(bean, valueLocal);
                            return;
                        } catch (Exception e) {
                            throw Utl.wrapErrorAsRuntimeException(String.format("Error on set \"%s\" to prop \"%s\" of bean!", value, propName), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void applyBeanProps2BeanProps(Object srcBean, Object destBean, boolean skipNulls) {
        if(srcBean == null)
            throw new IllegalArgumentException("Parameter \"srcBean\" cannot be null!");
        if(destBean == null)
            throw new IllegalArgumentException("Parameter \"destBean\" cannot be null!");
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(srcBean.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    Object value = pd.getReadMethod().invoke(srcBean);
                    if(value == null && skipNulls)
                        continue;
                    setPropertyValue(destBean, pd.getName(), value, "yyyy-MM-dd'T'hh:mm:ss");
                }
            }
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static boolean applyValuesToBeanFromBean(Object srcBean, Object bean) {
        boolean result = false;
        if (srcBean == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if (bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> srcType = srcBean.getClass();
        Class<?> type = bean.getClass();
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
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

    public static void applyValuesToABeanFromJSONObject(JSONObject jsonObject, Object dstBean) {
        if (jsonObject == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if (dstBean == null)
            throw new IllegalArgumentException("Argument \"dstBean\" cannot be null!");
        Map<String, Object> values = new HashMap<>();
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            try {
                values.put(key, jsonObject.get(key));
            } catch(JSONException e) {
                throw Utl.wrapErrorAsRuntimeException(e);
            }
        }
        if(dstBean instanceof ABean)
            applyValuesToABeanFromMap(values, (ABean) dstBean, true);
        else
            applyValuesToBeanFromMap(values, dstBean);
    }

    public static <T> T createBeanFromJSONObject(JSONObject jsonObject, Class<T> beanType) {
        try {
            T instance = beanType.newInstance();
            applyValuesToABeanFromJSONObject(jsonObject, instance);
            return instance;
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }


    private static boolean applyValueToField(Object valObj, Field field, Object bean) {
        boolean result = false;
        if (valObj != null) {
            try {
                Object val = (field.getType() == Object.class) ? valObj : Converter.toType(valObj, field.getType());
                field.setAccessible(true);
                field.set(bean, val);
                if (!result) result = true;
            } catch (Exception e) {
                throw new ApplyValuesToBeanException(field.getName(), String.format("Can't set value %s to field. Msg: %s", valObj, e.getMessage()));
            }
        }
        return result;
    }

    public static boolean applyValuesToBeanFromDict(Dictionary vals, Object bean) {
        boolean result = false;
        if (vals == null)
            throw new IllegalArgumentException("Argument \"vals\" cannot be null!");
        if (bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> type = bean.getClass();
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
            String fldName = fld.getName();
            Prop p = findAnnotation(Prop.class, fld);
            if (p != null)
                fldName = p.name();
            Object valObj = vals.get(fldName);
            result = applyValueToField(valObj, fld, bean);
        }
        return result;
    }

    public static boolean applyValuesToBeanFromMap(Map vals, Object bean, String inclideAttrs, String excludeAttrs) {
        boolean result = false;
        if (vals == null)
            throw new IllegalArgumentException("Argument \"vals\" cannot be null!");
        if (bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> type = bean.getClass();
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
            String fldName = fld.getName();
            Prop p = findAnnotation(Prop.class, fld);
            if (p != null)
                fldName = p.name();

            boolean skip = !vals.containsKey(fldName) ||
                    (!Strings.isNullOrEmpty(inclideAttrs) && !Strings.containsIgnoreCase(inclideAttrs, ",", fldName)) ||
                    (!Strings.isNullOrEmpty(excludeAttrs) && Strings.containsIgnoreCase(excludeAttrs, ",", fldName));
            if (skip) continue;

            Object valObj = vals.get(fldName);
            result = applyValueToField(valObj, fld, bean);

        }
        return result;
    }

    public static boolean applyValuesToBeanFromMap(Map vals, Object bean) {
        return applyValuesToBeanFromMap(vals, bean, null, null);
    }

    public static boolean applyValuesToBeanFromABean(ABean vals, Object bean) {
        return applyValuesToBeanFromMap(vals, bean);
    }

    public static void applyValuesToABeanFromABean(ABean srcBean, ABean dstBean, boolean addIfNotExists) {
        boolean result = false;
        if (srcBean == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if (dstBean == null)
            throw new IllegalArgumentException("Argument \"dstBean\" cannot be null!");
        for (String key : srcBean.keySet()) {
            if (dstBean.containsKey(key))
                dstBean.put(key, srcBean.get(key));
            else {
                if (addIfNotExists)
                    dstBean.put(key, srcBean.get(key));
            }
        }
    }

    public static void applyValuesToABeanFromMap(Map vals, ABean dstBean, boolean addIfNotExists) {
        boolean result = false;
        if (vals == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if (dstBean == null)
            throw new IllegalArgumentException("Argument \"dstBean\" cannot be null!");
        for (Object key : vals.keySet()) {
            if (dstBean.containsKey(key))
                dstBean.put(key.toString(), vals.get(key));
            else {
                if (addIfNotExists)
                    dstBean.put(key.toString(), vals.get(key));
            }
        }
    }

    public static  <T, R> R convertBeanType(T bean, Class<R> resultType) {
        try {
            R rslt = (R) resultType.newInstance();
            ABeans.applyBeanProps2BeanProps(bean, rslt, true);
            return rslt;
        } catch (InstantiationException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static  <T, R> List<R> convertListType(List<T> list, Class<R> resultType) {
        return list.stream().map(r -> convertBeanType(r, resultType)).collect(Collectors.toList());
    }

}
