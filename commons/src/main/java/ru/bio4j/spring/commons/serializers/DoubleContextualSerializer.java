package ru.bio4j.spring.commons.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Класс сериализации значений свойств типа {@link Double} в JSON с указанной точностью.
 * Точность задаётся атрибутом {@link Precision}.
 * <p>Пример: <code>@JsonSerialize(using = DoubleContextualSerializer.class)</code></p>
 */
public class DoubleContextualSerializer extends JsonSerializer<Double> implements ContextualSerializer {
    private int precision = 0;
    private int minPrecision = 0;
    private boolean exact = false;

    public DoubleContextualSerializer() {
        this(0, false);
    }

    public DoubleContextualSerializer(int precision) {
        this(precision, false);
    }

    public DoubleContextualSerializer(int precision, boolean exact) {
        this.precision = Math.max(precision, 0);
        this.exact = exact;
    }

    public DoubleContextualSerializer(int precision, int minPrecision) {
        this.precision = Math.max(precision, 0);
        this.minPrecision = Math.min(precision, minPrecision);
    }

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.isNaN())
            gen.writeNull();
        else {
            BigDecimal bd = new BigDecimal(value);
            int scale = exact ? precision : Math.max(bd.stripTrailingZeros().scale(), minPrecision);
            gen.writeNumber(bd.setScale(Math.min(precision, scale), RoundingMode.HALF_UP));
        }
    }
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        Precision precisionAnno = property != null ? AnnotatedElementUtils.getMergedAnnotation(property.getMember().getAnnotated(), Precision.class) : null;
        return precisionAnno != null
                ? (precisionAnno.exact()
                        ? new DoubleContextualSerializer(precisionAnno.precision(), true)
                        : new DoubleContextualSerializer(precisionAnno.precision(), precisionAnno.minPrecision()))
                : this;
    }
}
