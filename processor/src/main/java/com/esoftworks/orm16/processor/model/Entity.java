package com.esoftworks.orm16.processor.model;

import com.esoftworks.orm16.core.annotations.MappingContext;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Entity(
        TypeElement element,
        String packageName,
        String name,
        List<Attribute> attributes,
        Entity compositeKey,
        Map<MappingContext, EntityTarget> mappings) {

    public boolean hasPrimaryKey() {
        return attributes.stream().anyMatch(Attribute::primaryKey);
    }

    public Optional<Attribute> find(String name) {
        return attributes.stream()
                .filter(attribute -> attribute.name().equals(name))
                .findFirst();
    }

    public boolean supports(MappingContext ctx) {
        return mappings.containsKey(ctx);
    }

}
