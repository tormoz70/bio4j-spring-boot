package ru.bio4j.spring.common;

import ru.bio4j.spring.common.model.ABean;
import ru.bio4j.spring.common.model.Mapper;
import ru.bio4j.spring.common.model.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mapHelper {

    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> result = new HashMap<>();
        if (bean == null)
            return result;
        Class<?> srcType = bean.getClass();
        for (java.lang.reflect.Field fld : reflectHelper.getAllObjectFields(srcType)) {
            String paramName = fld.getName();
            if (paramName.equals("this$1")) continue;
            if (!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            fld.setAccessible(true);
            Object valObj = reflectHelper.getFieldValue(fld, bean);
            Mapper prp = fld.getAnnotation(Mapper.class);
            if (prp != null) {
                if (!stringHelper.isNullOrEmpty(prp.name()))
                    paramName = prp.name().toLowerCase();
            }
            result.put(paramName, valObj);
        }
        return result;
    }

    public static Map<String, Object> abeanToMap(ABean bean) {
        Map<String, Object> result = new HashMap<>();
        if (bean == null)
            return result;
        for (String key : bean.keySet()) {
            String paramName = key;
            if (paramName.equals("this$1")) continue;
            if (!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            Object valObj = bean.get(key);
            result.put(paramName, valObj);
        }
        return result;
    }

    public static Map<String, Object> hashmapToMap(HashMap<String, Object> bean) {
        Map<String, Object> result = new HashMap<>();
        if (bean == null)
            return result;
        for (String key : bean.keySet()) {
            String paramName = key;
            if (paramName.equals("this$1")) continue;
            if (!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            Object valObj = bean.get(key);
            result.put(paramName, valObj);
        }
        return result;
    }

    public static Map<String, Object> anjsonToMap(String anjson) {
        Map<String, Object> result = new HashMap<>();
        if (stringHelper.isNullOrEmpty(anjson))
            return result;

        ABean bioParamsContainer = jecksonHelper.getInstance().decodeABean(anjson);
        if (bioParamsContainer.containsKey("bioParams")) {
            List<HashMap<String, Object>> bioParamsArray = (List) bioParamsContainer.get("bioParams");
            for (HashMap<String, Object> prm : bioParamsArray) {
                String paramName = (String) prm.get("name");
                if (!paramName.toLowerCase().startsWith("p_"))
                    paramName = "p_" + paramName.toLowerCase();
                Object valObj = prm.get("value");
                result.put(paramName, valObj);
            }
        }
        return result;
    }

    public static Map<String, Object> decodeParams(Object params) {
        Map<String, Object> rslt = new HashMap<>();
        if(params != null){
            if(params instanceof List<?>)
                rslt = Paramus.toMap((List<Param>) params);
            else if(params instanceof Map)
                rslt = (Map<String, Object>)params;
            else if(params instanceof ABean)
                rslt = mapHelper.abeanToMap((ABean) params);
            else
                rslt = mapHelper.beanToMap(params);
        }
        return rslt;
    }

}
