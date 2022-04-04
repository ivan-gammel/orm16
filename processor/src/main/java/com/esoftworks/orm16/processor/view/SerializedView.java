package com.esoftworks.orm16.processor.view;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.*;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

public record SerializedView(MappingContext context,
                             List<SerializedEntity> serializedEntities,
                             List<SerializedEntity> embeddedEntities) {

    public SerializedView(MappingContext context,
                          Model model) {
        this(context,
             serializedEntities(context, model).toList(),
             embeddedEntities(context, model).toList());
    }


    private static Stream<SerializedEntity> serializedEntities(MappingContext context,
                                                               Model model) {
        return model.namespaces().stream()
                .flatMap(ns -> ns.entities().stream()
                        .filter(entity -> entity.supports(context) && !entity.mappings().get(context).embeddable())
                        .map(entity -> new SerializedEntity(context, model, ns, entity)));
    }

    public Stream<SerializedEntity> generatedEntities() {
        return concat(serializedEntities().stream(), embeddedEntities().stream())
                .flatMap(SerializedEntity::generatedEntities);
    }

    private static Stream<SerializedEntity> embeddedEntities(MappingContext context,
                                                             Model model) {
        return model.namespaces().stream()
                .flatMap(ns -> ns.entities().stream()
                        .filter(entity -> entity.supports(context) && entity.mappings().get(context).embeddable())
                        .map(entity -> new SerializedEntity(context, model, ns, entity)));
    }

}
