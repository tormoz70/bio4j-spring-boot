package ru.bio4j.model.generator.lib.mojo;


import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.spring.commons.utils.Reflex;
import ru.bio4j.test.dto.gen.model.nsi.Film;

import java.lang.reflect.Field;
import java.util.List;

public class GeneratorMojoTest {

    @Test
    public void test0() {
        Film film = Film.builder().colornot("colored").build();
        Assert.assertTrue(film.getColornot().equals("colored"));
    }

    @Test
    public void test1() {
        Field field = Reflex.findFieldOfBean(Film.class, "editableList");
        Assert.assertTrue(field != null);
        Assert.assertTrue(field.getType() == List.class);
    }

}