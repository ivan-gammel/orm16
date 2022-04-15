package com.esoftworks.orm16.processor;

import com.esoftworks.orm16.core.annotations.*;
import com.esoftworks.orm16.processor.builders.*;
import com.esoftworks.orm16.processor.model.Model;
import com.esoftworks.orm16.processor.model.builder.ModelBuilder;
import com.google.auto.service.AutoService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static com.esoftworks.orm16.processor.model.builder.Builder.given;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.joining;

@SupportedAnnotationTypes({
        "com.esoftworks.orm16.core.annotations.CodeGenerator",
        "com.esoftworks.orm16.core.annotations.GeneratedSources",
        "com.esoftworks.orm16.core.annotations.EntityMappings",
        "com.esoftworks.orm16.core.annotations.MappedEntity",
        "com.esoftworks.orm16.core.annotations.AttributeMappings",
        "com.esoftworks.orm16.core.annotations.Mapping",
        "com.esoftworks.orm16.core.annotations.References",
        "com.esoftworks.orm16.core.annotations.Id"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class RepositoryGenerator extends AbstractProcessor {

    private final Map<String, ModelContributor> contributors = given(() -> {
        var map = new HashMap<String, ModelContributor>();
        map.put(CodeGenerator.class.getSimpleName(), new PackageMappingContributor());
        map.put(GeneratedSources.class.getSimpleName(), new PackageMappingContributor());

        map.put(EntityMappings.class.getSimpleName(), new EntityMappingContributor());
        map.put(MappedEntity.class.getSimpleName(), new EntityMappingContributor());

        map.put(AttributeMappings.class.getSimpleName(), new AttributeMappingContributor());
        map.put(Mapping.class.getSimpleName(), new AttributeMappingContributor());

        map.put(References.class.getSimpleName(), new ReferencesContributor());
        map.put(Id.class.getSimpleName(), new IdContributor());
        return unmodifiableMap(map);
    });

    private final Configuration config = given(() -> {
        var config = new Configuration(Configuration.VERSION_2_3_21);
        config.setClassForTemplateLoading(this.getClass(), "/templates");
        return config;
    });

    private ModelBuilder builder;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Messager messager = processingEnv.getMessager();
        builder = new ModelBuilder(messager);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        if (annotations.size() > 0) {
            for (TypeElement annotation : annotations) {
                Set<? extends Element> annotatedElements
                        = roundEnv.getElementsAnnotatedWith(annotation);
                var contributor = contributors.get(annotation.getSimpleName().toString());
                if (contributor != null) {
                    contributor.accept(builder, annotatedElements, processingEnv);
                } else {
                    messager.printMessage(Diagnostic.Kind.WARNING, "Feature @" + annotation.getSimpleName() + " not supported on "
                            + annotatedElements.stream().map(Objects::toString).collect(Collectors.joining(", ")));
                }
            }
        } else {
            Model model = builder.build();
            for (MappingContext ctx : MappingContext.values()) {
                String contextName = ctx.toString().toLowerCase(Locale.ROOT);
                if (model.supports(ctx)) {
                    try {
                        CodeGenerators.forTarget(ctx)
                                .enumerateTemplates(model, config, processingEnv)
                                .forEach(template -> generate(template, roundEnv));
                        messager.printMessage(Diagnostic.Kind.NOTE, "Successfully generated code for " + contextName + " mapping.");
                    } catch (UnsupportedOperationException e) {
                        messager.printMessage(Diagnostic.Kind.WARNING, "Cannot generate code for " + contextName + " mapping. Operation not supported.");
                    }
                } else {
                    messager.printMessage(Diagnostic.Kind.NOTE, "Mapping not defined for " + contextName + ". Code will not be generated.");
                }
            }
        }
        return true;
    }

    private void generate(ClassTemplate template, RoundEnvironment roundEnv) {
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(template.name());
            try (OutputStream stream = file.openOutputStream(); PrintWriter writer = new PrintWriter(stream)) {
                template.writeTo(writer);
            }
        } catch (IOException | TemplateException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,  "Failed to create class file " + template.name() + e.getMessage(), template.element());
        }
    }

}
