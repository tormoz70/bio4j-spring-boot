package ru.bio4j.model.generator.lib;


import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.spy;

public class GeneratorTest {

    private static DtoGenerator dtoGenerator;

    @BeforeClass
    public static void beforeClass() {
        dtoGenerator = spy(DtoGenerator.class);
        dtoGenerator.init(
                "src\\test\\resources",
                "src\\test\\resources\\srcs",
                "test.dto.gen"
        );
    }

    @Test
    public void generateTest() {
        dtoGenerator.generate();
    }

}