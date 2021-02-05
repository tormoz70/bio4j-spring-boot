package ru.bio4j.model.generator.lib;

import com.squareup.javapoet.*;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.database.commons.CursorParser;
import ru.bio4j.spring.model.transport.jstore.Field;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TypeGenerator {
    private static Logger LOG = LoggerFactory.getLogger(TypeGenerator.class);

    private TypeSpec createClass(String name, SQLDefinition cursor) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(name)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        List<MethodSpec> gettersetterSpecs = new ArrayList<>();
        if(cursor.getFields().size() > 0) {
            for(Field field : cursor.getFields()) {
                String fieldName = Utl.nvl(field.getAttrName(), field.getName());
                TypeName typeName = ParameterizedTypeName.get(MetaTypeConverter.write(field.getMetaType()));
                AnnotationSpec.Builder annotationSpecBuilder = AnnotationSpec.builder(ApiModelProperty.class)
                        .addMember("value", "$S", field.getTitle())
                        .addMember("required", "$L", field.isMandatory())
                        .addMember("hidden", "$L", field.isHidden())
                        .addMember("accessMode", "$T.$L", ApiModelProperty.AccessMode.class,
                                field.isReadonly() ? ApiModelProperty.AccessMode.READ_ONLY : ApiModelProperty.AccessMode.AUTO);
                fieldSpecs.add(FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE)
                        .addAnnotation(annotationSpecBuilder.build())
                        .build());
                gettersetterSpecs.add(MethodSpec.methodBuilder("set" + StringUtils.capitalize(fieldName))
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(typeName, "value")
                        .addStatement("this.$N = value", fieldName)
                        .build());
                gettersetterSpecs.add(MethodSpec.methodBuilder("get" + StringUtils.capitalize(fieldName))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return this.$N", fieldName)
                        .returns(typeName).build());
            }
        }
        return classBuilder.addFields(fieldSpecs)
                    .addMethods(gettersetterSpecs)
                    .build();
    }

    public void generate(String rootPath, String path2xml, String packageName, String outputPath) {
        String fullRootPath = new File(rootPath).getAbsolutePath();
        String xmlFileName = Utl.fileName(path2xml);
        String xmlFilePath = path2xml.substring(0, path2xml.length() - xmlFileName.length() - 1);
        String subPackage = xmlFilePath.substring(fullRootPath.length()).replace(File.separator, ".");
        if(!Strings.isNullOrEmpty(subPackage)) subPackage = subPackage.substring(1);
        String javaClassName = StringUtils.capitalize(Utl.fileNameWithoutExt(Utl.fileName(path2xml)));
        String bioCode = !Strings.isNullOrEmpty(subPackage) ? subPackage.concat(".").concat(javaClassName) : javaClassName;
        try (InputStream inputStream = Strings.openResourceAsStream(path2xml)) {
            SQLDefinition cursor = CursorParser.pars(inputStream, bioCode);
            if(cursor != null) {
                File outputFolder = new File(outputPath);
                String outputPkg = !Strings.isNullOrEmpty(subPackage) ? packageName.concat(".").concat(subPackage) : packageName;
                String outputFileName = outputPkg.replace(".", "/").concat("/").concat(javaClassName).concat(".java");
                File outputJavaFile = new File(outputFolder, outputFileName);
                Path dir2Cre = outputJavaFile.toPath().getParent();
                Files.createDirectories(dir2Cre);
                try (FileWriter writer = new FileWriter(outputJavaFile.getAbsolutePath())) {
                    TypeSpec typeSpec = createClass(javaClassName, cursor);
                    JavaFile.builder(outputPkg, typeSpec).build().writeTo(writer);
                }
            }
        } catch (IOException e) {
            LOG.error(e.toString());
        }

    }
}
