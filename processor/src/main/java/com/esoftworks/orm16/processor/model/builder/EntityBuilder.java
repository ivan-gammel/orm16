package com.esoftworks.orm16.processor.model.builder;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.core.annotations.EntityMappings;
import com.esoftworks.orm16.core.annotations.MappedEntity;
import com.esoftworks.orm16.processor.model.Entity;
import com.esoftworks.orm16.processor.model.EntityTarget;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import java.util.*;

import static com.esoftworks.orm16.processor.model.builder.Builder.givenAll;

public class EntityBuilder implements Builder<Entity> {

    private final TypeElement element;
    private String packageName;
    private String name;
    private Map<String, PropertyBuilder> properties = new LinkedHashMap<>();
    private Map<MappingContext, EntityTarget> mappings = new HashMap<>();
    private EntityBuilder key;

    public EntityBuilder(String packageName,
                         TypeElement element,
                         ProcessingEnvironment env) {
        this.element = element;
        this.name = element.getSimpleName().toString();
        this.packageName = packageName;
        element.getRecordComponents().forEach(e -> this.add(e, env));
        MappedEntity[] targets = {};
        EntityMappings serialized = element.getAnnotation(EntityMappings.class);
        if (serialized != null) {
            targets = serialized.value();
        } else {
            var target = element.getAnnotation(MappedEntity.class);
            if (target != null) {
                targets = new MappedEntity[] { target };
            }
        }
        for (MappedEntity target : targets) {
            for (MappingContext ctx : target.context()) {
                boolean embeddable = false;
                String name = target.as();
                if ("".equals(name)) {
                    name = this.name;
                }
                mappings.putIfAbsent(ctx, new EntityTarget(name, embeddable));
            }
        }
    }

    private EntityBuilder add(RecordComponentElement element, ProcessingEnvironment env) {
        merge(element, env);
        return this;
    }

    @Override
    public Entity build() {
        return new Entity(element, packageName, name, givenAll(properties.values()),
                key != null ? key.build() : null,
                mappings);
    }

    public PropertyBuilder merge(RecordComponentElement element, ProcessingEnvironment env) {
        PropertyBuilder builder = properties.computeIfAbsent(element.getSimpleName().toString(), name -> new PropertyBuilder(element));
        return builder;
    }
}
