package com.esoftworks.orm16.processor.model;

import com.esoftworks.orm16.core.annotations.MappingContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Namespace(String name,
                        List<Entity> entities,
                        Map<MappingContext, CodeGenerationConfiguration> configuration) {

    public Namespace {
        if (name == null)  throw new NullPointerException("name");
        if (entities == null)  throw new NullPointerException("entities");
        if (configuration  == null)  throw new NullPointerException("configuration");
    }

    public boolean supports(MappingContext ctx) {
        return configuration.containsKey(ctx) || entities.stream().anyMatch(entity -> entity.supports(ctx));
    }

    public Optional<Entity> find(String name) {
        return entities.stream().filter(e -> e.name().equals(name)).findFirst();
    }
}
