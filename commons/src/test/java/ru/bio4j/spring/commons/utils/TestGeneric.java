package ru.bio4j.spring.commons.utils;

/**
 * Created by ayrat on 04.05.14.
 */
public class TestGeneric<tt> {
    private tt var;
    public Class<tt> getparamType() {
        return null; //(Class<tt>)Utl.getTypeParams(this);
    }
}
