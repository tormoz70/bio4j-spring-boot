package ru.bio4j.model.generator.lib;

import org.apache.commons.lang3.Streams;
import org.w3c.dom.Document;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.commons.CursorParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Generator {

    private String modelSourceDir;
    private String modelOutputDir;
    private DtoGenerator dtoGenerator;

    public void init(String modelSourceDir, String modelOutputDir) {
        this.modelSourceDir = modelSourceDir;
        this.modelOutputDir = modelOutputDir;
        this.dtoGenerator = new DtoGenerator();
    }

    private static List<String> loadAllXmls(final String path) {
        File file = new File(path);
        return Arrays.stream(file.listFiles((current, name) -> new File(current, name).isFile() && name.toLowerCase().endsWith(".xml")))
                .map(f -> f.getAbsolutePath()).collect(Collectors.toList());
    }

    public void generate() {
        List<String> xmls = loadAllXmls(modelSourceDir);
        for(String xml : xmls) {
            dtoGenerator.generate(xml);
        }
    }

}
