package ru.bio4j.model.generator.lib.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GeneratorMojo extends AbstractMojo {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorMojo.class);

    @Parameter(required = true)
    private String modelSourceDir;
    @Parameter(required = true)
    private String modelOutputDir;
    @Parameter(required = true)
    private String modelOutputPackage;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(String.format("modelSourceDir: {}", modelSourceDir));
        getLog().info(String.format("modelOutputDir: {}", modelOutputDir));
        getLog().info(String.format("modelOutputPackage: {}", modelOutputPackage));

    }
}
