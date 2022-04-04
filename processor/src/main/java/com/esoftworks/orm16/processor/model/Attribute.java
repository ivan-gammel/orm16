package com.esoftworks.orm16.processor.model;

import com.esoftworks.orm16.core.annotations.MappingContext;

import java.util.Map;

public record Attribute(String name,
                        DataTypeReference type,
                        boolean primaryKey,
                        Map<MappingContext, AttributeTarget> mappings) {

    public Attribute(String name,
                     String type,
                     boolean primaryKey,
                     Map<MappingContext, AttributeTarget> mappings) {
        this(name, DataTypeReference.forName(type), primaryKey, mappings);
    }

}
