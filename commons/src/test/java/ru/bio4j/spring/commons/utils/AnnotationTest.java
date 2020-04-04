package ru.bio4j.spring.commons.utils;

import java.lang.annotation.*;

@Target(value=ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface AnnotationTest {
     String path();
}