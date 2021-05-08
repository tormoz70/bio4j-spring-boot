package ru.bio4j.model.generator.lib;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.squareup.javapoet.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.commons.serializers.BigDecimalContextualSerializer;
import ru.bio4j.spring.commons.serializers.Precision;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.database.commons.CursorParser;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.jstore.Field;
import springfox.documentation.spring.web.json.JsonSerializer;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TypeGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(TypeGenerator.class);

    private static final String BUILDER_CLASS_NAME = "Builder";

    private TypeSpec createBuilder(String parentName, SQLDefinition cursor) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(BUILDER_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        List<MethodSpec> setterSpecs = new ArrayList<>();
        MethodSpec.Builder buildMethodNuilder = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addStatement(String.format("%s result = new %s()", parentName, parentName));
        if(cursor.getFields().size() > 0) {
            for(Field field : cursor.getFields()) {
                if(field.isDtoSkip())
                    continue;
                String fieldName = Utl.nvl(field.getAttrName(), field.getName());
                TypeName typeName;
                if(field.isDtoAsList())
                    typeName = ParameterizedTypeName.get(List.class, MetaTypeConverter.write(field.getMetaType()));
                else {
                    // TODO: Этот костыль надо убрать, когда фреймворк полностью перейдёт на java.time
                    Class<?> clazz = MetaTypeConverter.write(field.getMetaType());
                    typeName = ParameterizedTypeName.get(clazz == Date.class ? LocalDateTime.class : clazz);
                }
                fieldSpecs.add(FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE).build());

                setterSpecs.add(MethodSpec.methodBuilder(fieldName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(typeName, "value")
                        .addStatement("this.$N = value", fieldName)
                        .addStatement("return this")
                        .returns(ClassName.get("", BUILDER_CLASS_NAME))
                        .build());
                buildMethodNuilder.addStatement("result.$N = this.$N", fieldName, fieldName);
            }
            buildMethodNuilder.addStatement("return result");
            buildMethodNuilder.returns(ClassName.get("", parentName));
        }
        TypeSpec.Builder resultBuilder = classBuilder.addFields(fieldSpecs)
                .addMethods(setterSpecs)
                .addMethod(buildMethodNuilder.build());


        return resultBuilder.build();

    }

    private TypeSpec createClass(String name, SQLDefinition cursor) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(name)
            .addModifiers(Modifier.PUBLIC);
        if (!cursor.getDtoInheritable())
            classBuilder.addModifiers(Modifier.FINAL);
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        List<MethodSpec> gettersetterSpecs = new ArrayList<>();
        if(cursor.getFields().size() > 0) {
            int pos = 0;
            for(Field field : cursor.getFields()) {
                if(field.isDtoSkip())
                    continue;
                String fieldName = Utl.nvl(field.getAttrName(), field.getName());
                TypeName typeName;
                if(field.isDtoAsList())
                    typeName = ParameterizedTypeName.get(List.class, MetaTypeConverter.write(field.getMetaType()));
                else {
                    // TODO: Этот костыль надо убрать, когда фреймворк полностью перейдёт на java.time
                    Class<?> clazz = MetaTypeConverter.write(field.getMetaType());
                    typeName = ParameterizedTypeName.get(clazz == Date.class ? LocalDateTime.class : clazz);
                }
                AnnotationSpec.Builder fieldAnnotationBuilder = AnnotationSpec.builder(ApiModelProperty.class)
                        .addMember("value", "$S", field.getDtoDocumentation())
                        .addMember("required", "$L", field.isMandatory())
                        .addMember("hidden", "$L", field.isDtoApiHidden())
                        .addMember("accessMode", "$T.$L", ApiModelProperty.AccessMode.class,
                                field.isReadonly() ? ApiModelProperty.AccessMode.READ_ONLY : ApiModelProperty.AccessMode.AUTO)
                        .addMember("position", "$L", pos++);
                FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE)
                        .addAnnotation(fieldAnnotationBuilder.build());
                if(field.isDtoJsonIgnore())
                    fieldSpecBuilder.addAnnotation(AnnotationSpec.builder(JsonIgnore.class).build());
                fieldSpecs.add(fieldSpecBuilder.build());

                MethodSpec.Builder getterSpecBuilder = MethodSpec.methodBuilder("get" + StringUtils.capitalize(fieldName));
                if (field.getMetaType() != null && field.getMetaType().equals(MetaType.DECIMAL) && !Strings.isNullOrEmpty(field.getFormat())) {
                    String format = field.getFormat();
                    if (!format.matches("^[0#,.]*$"))
                        throw new NumberFormatException("Illegal characters found in format specified for field '" + fieldName + "'.");
                    int precision = 0, minPrecision = 0;
                    if (format.contains(".")) {
                        for (int i = format.length() - 1; i >= 0 && format.charAt(i) != '.'; i--) {
                            if (format.charAt(i) == '#' && minPrecision == 0)
                                precision++;
                            else if (format.charAt(i) == '0')
                                minPrecision++;
                            else
                                throw new NumberFormatException(String.format("Illegal character found in format specified for field '%s' in position %d.", fieldName, i));
                        }
                        precision += minPrecision;
                    }
                    getterSpecBuilder.addAnnotation(AnnotationSpec.builder(JsonSerialize.class)
                            .addMember("using", "$T.class", BigDecimalContextualSerializer.class)
                            .build());
                    getterSpecBuilder.addAnnotation(AnnotationSpec.builder(Precision.class)
                            .addMember("precision", "$L", precision)
                            .addMember("minPrecision", "$L", minPrecision)
                            .build());
                }

                gettersetterSpecs.add(MethodSpec.methodBuilder("set" + StringUtils.capitalize(fieldName))
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(typeName, "value")
                        .addStatement("this.$N = value", fieldName)
                        .build());
                gettersetterSpecs.add(getterSpecBuilder
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return this.$N", fieldName)
                        .returns(typeName).build());
            }
        }
        TypeSpec.Builder resultBuilder = classBuilder.addFields(fieldSpecs)
                .addMethods(gettersetterSpecs);
        if(!Strings.isNullOrEmpty(cursor.getDtoDocumentation())) {
            AnnotationSpec.Builder typeAnnotationBuilder = AnnotationSpec.builder(ApiModel.class)
                    .addMember("description", "$S", cursor.getDtoDocumentation());
            resultBuilder.addAnnotation(typeAnnotationBuilder.build());
        }
        resultBuilder.addType(createBuilder(name, cursor));
        resultBuilder.addMethod(MethodSpec.methodBuilder(BUILDER_CLASS_NAME.toLowerCase())
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addStatement(String.format("return new %s()", BUILDER_CLASS_NAME))
                .returns(ClassName.get("", BUILDER_CLASS_NAME))
                .build());

        return resultBuilder.build();
    }

    public static String buildTypeName(String fileName) {
        return WordUtils.capitalize(fileName, '-','_').replaceAll("\\-", "").replaceAll("_", "");
    }

    public void generate(String rootPath, String path2xml, String packageName, String outputPath) {
        try (InputStream inputStream = Strings.openResourceAsStream(path2xml)) {
            CursorParser.getInstance().setCursorSqlResolver((bioCode, sqlText) -> "select 1 from dual");
            String fullRootPath = new File(rootPath).getAbsolutePath();
            String xmlFileName = Utl.fileName(path2xml);
            String xmlFilePath = path2xml.substring(0, path2xml.length() - xmlFileName.length() - 1);
            String subPackage = xmlFilePath.substring(fullRootPath.length()).replace(File.separator, ".");
            if(!Strings.isNullOrEmpty(subPackage)) subPackage = subPackage.substring(1);
            String javaClassName = buildTypeName(Utl.fileNameWithoutExt(Utl.fileName(path2xml)));
            String bioCode = !Strings.isNullOrEmpty(subPackage) ? subPackage.concat(".").concat(javaClassName) : javaClassName;

            SQLDefinition cursor = CursorParser.getInstance().pars(inputStream, bioCode);
            if(cursor != null && cursor.getFields().size() > 0 && !cursor.getDtoSkip()) {
                if(!Strings.isNullOrEmpty(cursor.getDtoName())) {
                    javaClassName = cursor.getDtoName();
                }
                File outputFolder = new File(outputPath);
                String outputPkg = !Strings.isNullOrEmpty(subPackage) ? packageName.concat(".").concat(subPackage) : packageName;
                String outputFileName = outputPkg.replace(".", "/").concat("/").concat(javaClassName).concat(".java");
                File outputJavaFile = new File(outputFolder, outputFileName);
                Path dir2Cre = outputJavaFile.toPath().getParent();
                Files.createDirectories(dir2Cre);
                TypeSpec typeSpec = createClass(javaClassName, cursor);
                JavaFile.builder(outputPkg, typeSpec).build().writeTo(Paths.get(outputFolder.getAbsolutePath()), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }

    }
}
