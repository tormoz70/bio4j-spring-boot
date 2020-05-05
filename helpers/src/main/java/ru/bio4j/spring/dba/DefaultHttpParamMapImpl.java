package ru.bio4j.spring.dba;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.bio4j.spring.commons.types.HttpParamMap;

public class DefaultHttpParamMapImpl implements HttpParamMap {

    @Autowired(required = false)
    @Qualifier("override")
    private HttpParamMap overrideHttpParamMap;

    @Override
    public HttpParamMap getOverride() {
        return overrideHttpParamMap;
    }
}
