package com.esoftworks.orm16.processor.view;

import com.esoftworks.orm16.core.annotations.SerializationContext;
import com.esoftworks.orm16.processor.model.*;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

public record SerializedView(SerializationContext context,
                             List<SerializedEntity> serializedEntities,
                             List<SerializedEntity> embeddedEntities) {

    public SerializedView(SerializationContext context,
                          Model model) {
        this(context,
             serializedEntities(context, model).toList(),
             embeddedEntities(context, model).toList());
    }


    private static Stream<SerializedEntity> serializedEntities(SerializationContext context,
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

    private static Stream<SerializedEntity> embeddedEntities(SerializationContext context,
                                                             Model model) {
        return model.namespaces().stream()
                .flatMap(ns -> ns.entities().stream()
                        .filter(entity -> entity.supports(context) && entity.mappings().get(context).embeddable())
                        .map(entity -> new SerializedEntity(context, model, ns, entity)));
    }

}
