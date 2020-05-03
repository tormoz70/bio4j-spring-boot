package ru.bio4j.spring.commons.utils;

import ru.bio4j.spring.commons.converter.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
