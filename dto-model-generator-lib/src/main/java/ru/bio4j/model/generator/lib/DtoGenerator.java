package ru.bio4j.model.generator.lib;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DtoGenerator {

    private String modelSourceDir;
    private String modelOutputDir;
    private String modelOutputPackage;
    private TypeGenerator typeGenerator = new TypeGenerator();

    public void init(String modelSourceDir, String modelOutputDir, String modelOutputPackage) {
        this.modelSourceDir = modelSourceDir;
        this.modelOutputDir = modelOutputDir;
        this.modelOutputPackage = modelOutputPackage;
    }

    private static List<String> loadAllXmls(final String path) {
        File file = new File(path);
        return Arrays.stream(file.listFiles((current, name) -> new File(current, name).isFile() && name.toLowerCase().endsWith(".xml")))
                .map(f -> f.getAbsolutePath()).collect(Collectors.toList());
    }
    private static List<String> loadPaths(final String path) {
        File file = new File(path);
        return Arrays.stream(file.listFiles((current, name) -> new File(current, name).isDirectory() && !name.toLowerCase().equals(".") && !name.toLowerCase().equals("..")))
                .map(f -> f.getAbsolutePath()).collect(Collectors.toList());
    }

    private void _generate(String fromPath) {
        List<String> xmls = loadAllXmls(fromPath);
        for(String path2xml : xmls) {
            typeGenerator.generate(modelSourceDir, path2xml, modelOutputPackage, modelOutputDir);
        }
        List<String> paths = loadPaths(fromPath);
        for(String path : paths) {
            _generate(path);
        }
    }

    public void generate() {
        _generate(modelSourceDir);
    }

}
