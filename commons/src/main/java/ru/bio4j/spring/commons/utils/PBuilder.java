package ru.bio4j.spring.commons.utils;

import ru.bio4j.spring.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

public class PBuilder {
    private List<Param> params;
    private PBuilder() {
        params = new ArrayList<>();
    }
    public static PBuilder init() {
        return new PBuilder();
    }


}
