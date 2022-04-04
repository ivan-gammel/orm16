package com.esoftworks.orm16.processor.model.builder;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.Attribute;
import com.esoftworks.orm16.processor.model.AttributeTarget;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.Map;

public class PropertyBuilder implements Builder<Attribute> {

    private final String name;
    private final String type;
    private Map<MappingContext, MappingBuilder> mappings = new HashMap<>();
    private boolean isPrimaryKey = false;

    public PropertyBuilder(RecordComponentElement element) {
        this.name = element.getSimpleName().toString();
        this.type = element.getAccessor().getReturnType().toString();
    }

    public PropertyBuilder(VariableElement element) {
        this.name = element.getSimpleName().toString();
        this.type = element.asType().toString();
    }

    public MappingBuilder in(MappingContext ctx) {
        return this.mappings.computeIfAbsent(ctx, $ -> new MappingBuilder());
    }

    public PropertyBuilder asPrimaryKey() {
        this.isPrimaryKey = true;
        return this;
    }

    @Override
    public Attribute build() {
        var targets = new HashMap<MappingContext, AttributeTarget>();
        for (MappingContext ctx : mappings.keySet()) {
            targets.put(ctx, mappings.get(ctx).build());
        }
        return new Attribute(name, type, isPrimaryKey, targets);
    }
}
