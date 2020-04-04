package ru.bio4j.spring.model.transport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {
    String name() default "";
    Param.Direction direction() default Param.Direction.IN;
    MetaType metaType() default MetaType.UNDEFINED;
}
