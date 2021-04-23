package ru.bio4j.spring.commons.serializers;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Атрибут устанавливает точность (количество знаков после запятой), до которой будет произведено
 * округление значения при сериализации в JSON.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Precision {
    /**
     * Точность округления. Не может быть меньше нуля.
     * @return количество знаков после запятой.
     */
    @AliasFor("precision")
    int value() default 2;

    /**
     * Точность округления. Не может быть меньше нуля.
     * @return количество знаков после запятой.
     */
    @AliasFor("value")
    int precision() default 2;

    /**
     * Минимальное значение для точности округления. Если точность фактического значения будет меньше,
     * то результат будет дополнен нулями справа. Если задано свойство {@link #exact}, то это своёство игнорируется.
     * Не может быть меньше нуля и больше {@link #precision}.
     * <p>Если значения {@link #precision} и {@link #minPrecision} равны, то это аналогично <code>{@link #exact}=true</code>.</p>
     * @return количество знаков после запятой.
     */
    int minPrecision() default 0;

    /**
     * Если количество знаков после запятой в фактическом значении меньше чем задано в {@link #precision},
     * то недостающие позиции будут заполнены нулями справа. Если задано это свойство, то {@link #minPrecision} игнорируется.
     * <p>Пример: <code>@Precision(precision=2, exact=true)</code> 2.2 -> 2.20</p>
     */
    boolean exact() default false;
}