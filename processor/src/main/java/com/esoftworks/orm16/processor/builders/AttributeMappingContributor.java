package com.esoftworks.orm16.processor.builders;

import com.esoftworks.orm16.core.annotations.Mapping;
import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.core.annotations.OutputFormat;
import com.esoftworks.orm16.processor.model.builder.ModelBuilder;
import com.esoftworks.orm16.processor.model.builder.PropertyBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.LinkedList;
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

                var mappings = attribute.getAnnotationsByType(Mapping.class);
                for (Mapping mapping : mappings) {
                    for (MappingContext ctx : mapping.context()) {
                        if (!mapping.to().isEmpty()) {
                            modifiers.add(property -> property.in(ctx).mapTo(mapping.to()));
                        }
                    }
                    switch (mapping.serializeAs()) {
                        case VALUE:
                            if (!mapping.as().equals(Object.class)) {
                                Stream.of(mapping.context()).forEach(ctx -> modifiers.add(property -> property.in(ctx).convertTo(mapping.as())));
                            }
                            if (mapping.format() != OutputFormat.NONE || !mapping.pattern().isEmpty()) {
                                if (!(mapping.as().equals(Object.class) || mapping.as().equals(String.class))) {
                                    env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Incorrect mapping of " + attributeName + ": cannot set output format for target class " + mapping.as().getName() + ". String target expected.");
                                } else {
                                    if (mapping.format() != OutputFormat.NONE) {
                                        Stream.of(mapping.context()).forEach(ctx -> modifiers.add(property -> property.in(ctx).convertTo(String.class).transform(mapping.format())));
                                    }
                                    if (!mapping.pattern().isEmpty()) {
                                        Stream.of(mapping.context()).forEach(ctx -> modifiers.add(property -> property.in(ctx).convertTo(String.class).applyPattern(mapping.pattern())));
                                    }
                                }
                            }
                            break;
                        case EMBEDDED:
                            TypeMirror returnType = attribute.getAccessor().getReturnType();
                            if (returnType.getKind() != TypeKind.DECLARED) {
                                env.getMessager().printMessage(Diagnostic.Kind.ERROR, returnType.toString() + " cannot be embedded as " + attributeName);
                                continue;
                            }
                            var returnTypeElement = (TypeElement) ((DeclaredType) returnType).asElement();
                            builder.merge(returnTypeElement, env).ifPresent(embeddedEntity -> Stream.of(mapping.context()).forEach(ctx -> {
                                modifiers.add(property -> property.in(ctx).asEmbedded(embeddedEntity, mapping.overrides(), mapping.overrideAs()));
                                embeddedEntity.embeddableIn(ctx);
                            }));
                            break;

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
