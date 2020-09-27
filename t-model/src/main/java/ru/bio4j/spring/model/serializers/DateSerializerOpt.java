package ru.bio4j.spring.model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Date;

public class DateSerializerOpt extends StdSerializer<Date> {

    private DateSerializerHolder dateSerializerHolder;

    public DateSerializerOpt() {
        this(null);
    }

    public DateSerializerOpt(Class t) {
        super(t);
    }

    @Override
    public void serialize (Date value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        if(dateSerializerHolder == null)
            dateSerializerHolder = BeanExplorer.getBean(DateSerializerHolder.class);
        dateSerializerHolder.dateSerializer().serialize(value, generator, provider);
    }
}
