package ru.bio4j.spring.model.serializers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import ru.bio4j.spring.model.serializers.DateSerializer;
import ru.bio4j.spring.model.serializers.DateSerializerHolder;

public class DateSerializerHolderImpl implements DateSerializerHolder {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("default")
    private DateSerializer dateSerializerDefault;

    @Autowired(required = false)
    @Qualifier("override")
    private DateSerializer dateSerializer;

    @Override
    public DateSerializer dateSerializer() {
        return dateSerializer != null ? dateSerializer : dateSerializerDefault;
    }
}
