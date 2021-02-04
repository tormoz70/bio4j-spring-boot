package ru.bio4j.model.generator.lib;


import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.spy;

public class GeneratorTest {

    private static Generator generator;

    @BeforeClass
    public static void beforeClass() {
        generator = spy(Generator.class);
        generator.init(
                "src\\test\\resources\\model",
                "src\\test\\resources\\srcs",
                "test.dto.gen"
        );
    }

    @Test
    public void generateTest() {
        generator.generate();
    }

}