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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.esoftworks.orm16.processor.model.builder.Builder.given;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.joining;

@SupportedAnnotationTypes({
        "com.esoftworks.orm16.core.annotations.CodeGenerator",
        "com.esoftworks.orm16.core.annotations.Serialize",
        "com.esoftworks.orm16.core.annotations.GeneratedSources",
        "com.esoftworks.orm16.core.annotations.Target",
        "com.esoftworks.orm16.core.annotations.References",
        "com.esoftworks.orm16.core.annotations.Embed",
        "com.esoftworks.orm16.core.annotations.Embeddable",
        "com.esoftworks.orm16.core.annotations.EmbeddableTarget",
        "com.esoftworks.orm16.core.annotations.Conversion",
        "com.esoftworks.orm16.core.annotations.AttributeOverride",
        "com.esoftworks.orm16.core.annotations.Id",
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class RepositoryGenerator extends AbstractProcessor {

    private final Map<String, ModelContributor> contributors = given(() -> {
        var map = new HashMap<String, ModelContributor>();
        map.put(Embed.class.getSimpleName(), new EmbedContributor());
        map.put(EmbeddableTargets.class.getSimpleName(), new EmbeddableContributor());
        map.put(CodeGenerator.class.getSimpleName(), new CodeGeneratorContributor());
        map.put(References.class.getSimpleName(), new ReferencesContributor());
        map.put(SerializationTargets.class.getSimpleName(), new SerializeContributor());
        map.put(SerializedEntity.class.getSimpleName(), new TargetContributor());
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
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "Discovered annotation: " + annotations.stream().map(Object::toString).collect(joining(", ")));
            for (TypeElement annotation : annotations) {
                Set<? extends Element> annotatedElements
                        = roundEnv.getElementsAnnotatedWith(annotation);
                var contributor = contributors.get(annotation.getSimpleName().toString());
                if (contributor != null) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "Processing " + annotation.getSimpleName() + " on "
                            + annotatedElements.stream().map(Objects::toString).collect(Collectors.joining(", ")));
                    contributor.accept(builder, annotatedElements, processingEnv);
                } else {
                    messager.printMessage(Diagnostic.Kind.WARNING, "Feature @" + annotation.getSimpleName() + " not supported on "
                            + annotatedElements.stream().map(Objects::toString).collect(Collectors.joining(", ")));
                }
            }
        } else {
            Model model = builder.build();
            for (SerializationContext ctx : SerializationContext.values()) {
                if (model.supports(ctx)) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "Generating code for " + ctx);
                    try {
                        CodeGenerators.forTarget(ctx)
                                .enumerateTemplates(model, config)
                                .forEach(template -> generate(template, roundEnv));
                    } catch (UnsupportedOperationException e) {
                        messager.printMessage(Diagnostic.Kind.WARNING, "Serialization context not supported: " + ctx);
                    }
                } else {
                    messager.printMessage(Diagnostic.Kind.NOTE, "Serialization context " + ctx + " ignored.");
                }
            }
        }
        return true;
    }

    private void generate(ClassTemplate template, RoundEnvironment roundEnv) {
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(template.name());
            try (OutputStream stream = file.openOutputStream();
                 PrintWriter writer = new PrintWriter(stream)) {
                template.writeTo(writer);
            }
        } catch (IOException | TemplateException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,  "Failed to create class file " + template.name() + e.getMessage(), template.element());
        }
    }

}
