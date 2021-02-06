package ru.bio4j.model.generator.lib.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.model.generator.lib.DtoGenerator;

@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GeneratorMojo extends AbstractMojo {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorMojo.class);

    @Parameter(property = "modelSourceDir", required = true)
    private String modelSourceDir;
    @Parameter(property = "modelOutputDir", required = true)
    private String modelOutputDir;
    @Parameter(property = "modelOutputPackage", required = true)
    private String modelOutputPackage;

    private final DtoGenerator dtoGenerator = new DtoGenerator();


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(String.format("modelSourceDir: %s", modelSourceDir));
        getLog().info(String.format("modelOutputDir: %s", modelOutputDir));
        getLog().info(String.format("modelOutputPackage: %s", modelOutputPackage));
        dtoGenerator.init(modelSourceDir, modelOutputDir, modelOutputPackage);
        dtoGenerator.generate();
    }
}
