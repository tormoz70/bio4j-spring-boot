package ru.bio4j.spring.model.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Date;

public class DateDeserializerOpt extends StdDeserializer<Date> {

    private DateSerializerHolder dateSerializerHolder;

    public DateDeserializerOpt() {
        this(null);
    }


    public DateDeserializerOpt(Class t) {
        super(t);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        if(dateSerializerHolder == null)
            dateSerializerHolder = BeanExplorer.getBean(DateSerializerHolder.class);
        return dateSerializerHolder.dateSerializer().deserialize(jsonParser, deserializationContext);
    }

}
