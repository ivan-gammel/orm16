package com.esoftworks.orm16.processor.view;

import com.esoftworks.orm16.core.annotations.SerializationContext;
import com.esoftworks.orm16.processor.model.*;

import java.util.Optional;
import java.util.stream.Stream;

public record SerializedAttribute(SerializationContext context,
                                  String name,
                                  DataTypeReference type,
                                  boolean primaryKey,
                                  String serializedName) {

    public SerializedAttribute(SerializationContext context,
                               Model model,
                               Namespace namespace,
                               Entity entity,
                               Attribute attribute) {
        this(context,
                attribute.name(),
                attribute.type(),
                attribute.primaryKey(),
                Optional.ofNullable(attribute.mappings().get(context)).map(AttributeTarget::name).orElse(attribute.name()));
    }

    public Stream<String> imports() {
        return type.primitive()
                ? Stream.empty()
                : Stream.of(type.qualifiedName());
    }

}
