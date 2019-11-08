package ru.bio4j.spring.common;

public class tools {
    public static <T> T nvl(T a, T b) {
        return (a == null) ? b : a;
    }


}
