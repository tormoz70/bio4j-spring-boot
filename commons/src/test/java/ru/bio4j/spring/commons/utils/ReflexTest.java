package ru.bio4j.spring.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

public class ReflexTest {

    @Test
    public void testFindFieldOfBean() {
        Field field = Reflex.findFieldOfBean(TApple.class, "wheight");
        Assert.assertTrue(field != null);
    }

    @Test
    public void testGetAllObjectFields() {
        List<Field> fields = Reflex.getAllObjectFields(TApple.class);
        Assert.assertTrue(fields.size() > 0);
    }

    @Test
    public void testFieldValueAsString() {
    }

    @Test
    public void testFieldValueAsObject() {
    }

    @Test
    public void testTestFieldValueAsObject() {
    }

    @Test
    public void testFieldValue() {
    }

    @Test
    public void testTypeHasInterface() {
    }

    @Test
    public void testFindAnnotation() {
    }

    @Test
    public void testTestFindAnnotation() {
    }

    @Test
    public void testTypesIsAssignable() {
    }

    @Test
    public void testTypesIsSame() {
    }
}