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
 * Класс сериализации значений свойств типа {@link BigDecimal} в JSON с указанной точностью.
 * Точность задаётся атрибутом {@link Precision}.
 * <p>Пример: <code>@JsonSerialize(using = BigDecimalContextualSerializer.class)</code></p>
 */
public class BigDecimalContextualSerializer extends JsonSerializer<BigDecimal> implements ContextualSerializer {
    private int precision = 0;
    private int minPrecision = 0;
    private boolean exact = false;

    public BigDecimalContextualSerializer() {
        this(0, false);
    }

    public BigDecimalContextualSerializer(int precision) {
        this(precision, false);
    }

    public BigDecimalContextualSerializer(int precision, boolean exact) {
        this.precision = Math.max(precision, 0);
        this.exact = exact;
    }

    public BigDecimalContextualSerializer(int precision, int minPrecision) {
        this.precision = Math.max(precision, 0);
        this.minPrecision = Math.min(precision, minPrecision);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null)
            gen.writeNull();
        else {
            int scale = exact ? precision : Math.max(value.stripTrailingZeros().scale(), minPrecision);
            gen.writeNumber(value.setScale(Math.min(precision, scale), RoundingMode.HALF_UP));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        Precision precisionAnno = property != null ? AnnotatedElementUtils.getMergedAnnotation(property.getMember().getAnnotated(), Precision.class) : null;
        return precisionAnno != null
                ? (precisionAnno.exact()
                        ? new BigDecimalContextualSerializer(precisionAnno.precision(), true)
                        : new BigDecimalContextualSerializer(precisionAnno.precision(), precisionAnno.minPrecision()))
                : this;
    }
}
