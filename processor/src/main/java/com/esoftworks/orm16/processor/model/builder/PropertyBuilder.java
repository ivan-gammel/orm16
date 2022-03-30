package com.esoftworks.orm16.processor.model.builder;

import com.esoftworks.orm16.core.annotations.Embed;
import com.esoftworks.orm16.core.annotations.SerializationContext;
import com.esoftworks.orm16.core.annotations.SerializedEntity;
import com.esoftworks.orm16.processor.model.AttributeTarget;
import com.esoftworks.orm16.processor.model.Attribute;
import com.esoftworks.orm16.processor.model.Entity;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.Map;

public class PropertyBuilder implements Builder<Attribute> {

    private final String name;
    private final String type;
    private Map<SerializationContext, MappingBuilder> mappings = new HashMap<>();
    private boolean isPrimaryKey = false;
    private EntityBuilder embeddedEntity;

    public PropertyBuilder(RecordComponentElement element) {
        this.name = element.getSimpleName().toString();
        this.type = element.getAccessor().getReturnType().toString();
    }

    public PropertyBuilder(VariableElement element) {
        this.name = element.getSimpleName().toString();
        this.type = element.asType().toString();
    }

    public PropertyBuilder add(SerializationContext ctx, SerializedEntity target) {
        this.mappings.computeIfAbsent(ctx, $ -> new MappingBuilder())
                .basedOn(target);
        return this;
    }

    public PropertyBuilder add(SerializationContext ctx, Embed embed) {
        this.mappings.computeIfAbsent(ctx, $ -> new MappingBuilder())
                .asEmbedded(embed);
        return this;
    }

    public PropertyBuilder asPrimaryKey() {
        this.isPrimaryKey = true;
        return this;
    }

    public PropertyBuilder asEmbedded(EntityBuilder embeddedEntity) {
        this.embeddedEntity = embeddedEntity;
        return this;
    }

    @Override
    public Attribute build() {
        var targets = new HashMap<SerializationContext, AttributeTarget>();
        for (SerializationContext ctx : mappings.keySet()) {
            targets.put(ctx, mappings.get(ctx).build());
        }
        if (embeddedEntity == null) {
            return new Attribute(name, type, isPrimaryKey, targets, null);
        } else {
            Entity entity = embeddedEntity.build();
            // Create unique copy of embedded entity for this specific context.
            // TODO: apply attribute override rules
            return new Attribute(name, type, isPrimaryKey, targets, entity);
        }
    }
}
