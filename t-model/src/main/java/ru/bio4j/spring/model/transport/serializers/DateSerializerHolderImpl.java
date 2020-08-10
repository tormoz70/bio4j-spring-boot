package ru.bio4j.spring.model.transport.serializers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.bio4j.spring.model.transport.serializers.DateSerializer;
import ru.bio4j.spring.model.transport.serializers.DateSerializerHolder;

public class DateSerializerHolderImpl implements DateSerializerHolder {

    @Autowired
    @Qualifier("default")
    private DateSerializer dateSerializerDefault;

    @Autowired(required = false)
    @Qualifier("override")
    private DateSerializer dateSerializerOverride;


    @Override
    public DateSerializer dateSerializer() {
        return dateSerializerOverride != null ? dateSerializerOverride : dateSerializerDefault;
    }
}
