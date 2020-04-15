package ru.bio4j.spring.commons.utils;

import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

public class ParamsBuilder {
    private List<Param> params;
    private ParamsBuilder() {
        params = new ArrayList<>();
    }
    private ParamsBuilder(List<Param> params) {
        this.params = params;
    }
    public static ParamsBuilder init() {
        return new ParamsBuilder();
    }
    public static ParamsBuilder init(List<Param> params) {
        return new ParamsBuilder(params);
    }

    public ParamsBuilder add(String paramName, Object value) {
        Paramus.setParamValue(params, paramName, value);
        return this;
    }

    public ParamsBuilder add(String paramName, Object value, MetaType forceType) {
        Paramus.setParamValue(params, paramName, value, forceType);
        return this;
    }

    public ParamsBuilder add(String paramName, Object value, MetaType forceType, Param.Direction direction) {
        Paramus.setParamValue(params, paramName, value, forceType, direction);
        return this;
    }

    public ParamsBuilder add(String paramName, Object value, Param.Direction direction) {
        Paramus.setParamValue(params, paramName, value, MetaType.UNDEFINED, direction);
        return this;
    }

    public List<Param> end() {
        return params;
    }

}
