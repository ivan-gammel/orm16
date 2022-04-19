package com.esoftworks.orm16.processor.builders;

import com.esoftworks.orm16.core.annotations.*;
import com.esoftworks.orm16.processor.model.builder.ModelBuilder;
import com.esoftworks.orm16.processor.model.builder.PropertyBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AttributeMappingContributor implements ModelContributor {

    @Override
    public void accept(ModelBuilder builder,
                       Set<? extends Element> annotatedElements,
                       ProcessingEnvironment env) {
        for (Element element : annotatedElements) {
            if (element instanceof RecordComponentElement attribute) {
                var clazz = (TypeElement) attribute.getEnclosingElement();
                String attributeName = clazz.getSimpleName() + "#" + attribute.getSimpleName();

                var modifiers = new LinkedList<Consumer<PropertyBuilder>>();

                Elements elements = env.getElementUtils();
                var mappings = attribute.getAnnotationMirrors().stream()
                        .filter(mirror -> mirror.getAnnotationType().asElement().getSimpleName().contentEquals(Mapping.class.getSimpleName()))
                        .map(mirror -> new MappingSpec(elements, mirror))
                        .toList();

                for (var mapping : mappings) {
                    MappingContext[] context = mapping.asArray("context", MappingContext.class);
                    for (MappingContext ctx : context) {
                        String to = mapping.asValue("to" , String.class);
                        if (!to.isEmpty()) {
                            modifiers.add(property -> property.in(ctx).mapTo(to));
                        }
                    }
                    var kind = mapping.asEnum("serializeAs", AttributeMappingKind.class);
                    TypeElement as = mapping.asType("as");
                    String targetTypeName = as.getQualifiedName().toString();
                    switch (kind) {
                        case VALUE -> {
                            OutputFormat format = mapping.asEnum("format" , OutputFormat.class);
                            switch (format) {
                                case NONE -> {
                                    // domain-specific format: convert if necessary
                                    if (!targetTypeName.equals(Serializable.class.getName())) {
                                        Stream.of(context).forEach(ctx -> modifiers.add(property -> property.in(ctx).convertTo(targetTypeName)));
                                    }
                                }
                                case PATTERN -> {
                                    // expect target type to be String
                                    if (!targetTypeName.equals(String.class.getName())) {
                                        env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Output format PATTERN defined for " + attributeName + " but target type is " + targetTypeName + ". String expected.");
                                    }
                                    // expect pattern not to be empty
                                    String pattern = mapping.asValue("pattern" , String.class);
                                    if (pattern.isEmpty()) {
                                        env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Output format PATTERN defined for " + attributeName + " but pattern is not specified.");
                                    }
                                    Stream.of(context).forEach(ctx -> modifiers.add(property -> property.in(ctx).convertTo(String.class.getName()).applyPattern(pattern)));
                                }
                                case XML, JSON -> {
                                    // expect target type to be String
                                    if (!targetTypeName.equals(String.class.getName())) {
                                        env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Output format " + format.name() + " defined for " + attributeName + " but target type is " + targetTypeName + ". String expected.");
                                    }
                                    Stream.of(context).forEach(ctx -> modifiers.add(property -> property.in(ctx).convertTo(String.class.getName()).transform(format)));
                                }
                            }
                        }
                        case EMBEDDED -> {
                            TypeMirror returnType = attribute.getAccessor().getReturnType();
                            if (returnType.getKind() != TypeKind.DECLARED) {
                                env.getMessager().printMessage(Diagnostic.Kind.ERROR, returnType.toString() + " cannot be embedded as " + attributeName);
                                continue;
                            }
                            var returnTypeElement = (TypeElement) ((DeclaredType) returnType).asElement();
                            builder.merge(returnTypeElement, env).ifPresent(embeddedEntity -> Stream.of(context).forEach(ctx -> {
                                var overrides = mapping.asArray("overrides" , AttributeOverride.class);
                                var overrideAs = mapping.asValue("overrideAs" , String.class);
                                modifiers.add(property -> property.in(ctx).asEmbedded(embeddedEntity, overrides, overrideAs));
                                embeddedEntity.embeddableIn(ctx);
                            }));
                        }
                    }
                }

                var result = builder.merge(clazz, env);
                if (result.isEmpty()) {
                    env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Entity " + clazz.getSimpleName() + " not mapped. Attribute " + attributeName + " annotations cannot be processed.");
                    continue;
                }
                var propertyBuilder = result.get().merge(attribute, env);
                for (Consumer<PropertyBuilder> modifier : modifiers) {
                    modifier.accept(propertyBuilder);
                }
            } // ignore Var/Method symbols
        }
    }

}
