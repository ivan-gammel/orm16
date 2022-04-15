package com.esoftworks.orm16.processor.view;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.Entity;
import com.esoftworks.orm16.processor.model.Model;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

public record SerializedView(MappingContext context,
                             List<SerializedEntity> serializedEntities,
                             List<SerializedEntity> embeddedEntities) {

    public SerializedView(MappingContext context,
                          Model model,
                          ProcessingEnvironment processingEnv) {
        this(context,
             serializedEntities(context, model, processingEnv).toList(),
             embeddedEntities(context, model).toList());
    }


    private static Stream<SerializedEntity> serializedEntities(MappingContext context,
                                                               Model model,
                                                               ProcessingEnvironment processingEnv) {
        return model.namespaces().stream()
                .flatMap(ns -> ns.entities().stream()
                        .peek(entity -> {
                            if (!entity.hasPrimaryKey() && !isEmbeddable(entity, context)) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Entity " + entity.name() + " does not have primary key. Use @Id to annotate the key field.");
                            }
                        })
                        .filter(entity -> entity.supports(context) && !isEmbeddable(entity, context) && entity.hasPrimaryKey())
                        .map(entity -> new SerializedEntity(context, model, ns, entity)));
    }

    private static boolean isEmbeddable(Entity entity, MappingContext context) {
        return entity.supports(context) && entity.mappings().get(context).embeddable();
    }

    public Stream<SerializedEntity> generatedEntities() {
        return concat(serializedEntities().stream(), embeddedEntities().stream())
                .flatMap(SerializedEntity::generatedEntities);
    }

    private static Stream<SerializedEntity> embeddedEntities(MappingContext context,
                                                             Model model) {
        return model.namespaces().stream()
                .flatMap(ns -> ns.entities().stream()
                        .filter(entity -> isEmbeddable(entity, context))
                        .map(entity -> new SerializedEntity(context, model, ns, entity)));
    }

}
