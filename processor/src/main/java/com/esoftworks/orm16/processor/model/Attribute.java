package com.esoftworks.orm16.processor.model;

import com.esoftworks.orm16.core.annotations.SerializationContext;

import java.util.Map;

public record Attribute(String name,
                        DataTypeReference type,
                        boolean primaryKey,
                        Map<SerializationContext, AttributeTarget> mappings,
                        Entity entity) {

    public Attribute(String name,
                     String type,
                     boolean primaryKey,
                     Map<SerializationContext, AttributeTarget> mappings,
                     Entity embeddedEntity) {
        this(name, DataTypeReference.forName(type), primaryKey, mappings, embeddedEntity);
    }

    public boolean embedded() {
        return entity != null;
    }

}
