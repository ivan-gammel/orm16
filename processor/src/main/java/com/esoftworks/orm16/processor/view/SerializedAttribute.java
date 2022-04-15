package com.esoftworks.orm16.processor.view;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.*;

import java.util.stream.Stream;

public record SerializedAttribute(MappingContext context,
                                  String name,
                                  DataTypeReference type,
                                  boolean primaryKey,
                                  String serializedName,
                                  /**
                                   * Optional embedded entity referenced by this attribute
                                   */
                                  SerializedEntity entity) {

    public SerializedAttribute(MappingContext context,
                               Model model,
                               Namespace namespace,
                               Entity parentEntity,
                               Attribute attribute) {
        this(context,
                attribute.name(),
                attribute.type(),
                attribute.primaryKey(),
                attribute.serializedNameIn(context),
                null);
    }

    /**
     * Constructor for attributes of serialized entities
     * @param context
     * @param model
     * @param namespace
     * @param parentEntity
     * @param attribute
     * @param serializedName
     */
    public SerializedAttribute(MappingContext context,
                               Model model,
                               Namespace namespace,
                               Entity parentEntity,
                               Attribute attribute,
                               String serializedName) {
        this(context,
                attribute.name(),
                attribute.type(),
                attribute.primaryKey(),
                serializedName,
                embeddedEntity(context, model, namespace, attribute));
    }

    private static SerializedEntity embeddedEntity(MappingContext context,
                                                   Model model,
                                                   Namespace namespace,
                                                   Attribute attribute) {
        AttributeTarget mapping = attribute.mappings().get(context);
        if (mapping == null) {
            return null;
        }
        return mapping.embedded() ? new SerializedEntity(
                context,
                model,
                namespace,
                mapping.embeddedEntity(),
                mapping.overrides(),
                mapping.attributeNameTemplate().replace("{parent}", attribute.serializedNameIn(context))
        ) : null;
    }

    public boolean embedded() {
        return entity != null;
    }

    public Stream<String> imports() {
        return type.primitive()
                ? Stream.empty()
                : Stream.of(type.qualifiedName());
    }

}
