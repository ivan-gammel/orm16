package com.esoftworks.orm16.processor.view;

import com.esoftworks.orm16.core.annotations.Mapping;
import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.*;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.function.Predicate.not;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public record SerializedEntity(MappingContext context,
                               TypeElement element,
                               String packageName,
                               String name,
                               String serializedName,
                               List<SerializedAttribute> attributes,
                               SerializedEntity compositeKey) {


    public SerializedEntity(MappingContext context,
                            Model model,
                            Namespace namespace,
                            Entity entity) {
        this(context,
                entity.element(),
                entity.packageName(),
                entity.name(),
                Optional.ofNullable(entity.mappings().get(context)).map(EntityTarget::name).orElse(entity.name()),
                attributes(context, model, namespace, entity, emptyMap(), null),
                entity.compositeKey() == null ? null : new SerializedEntity(context, model, namespace, entity.compositeKey()));
    }

    /**
     * Constructor for embedded entities
     * @param context
     * @param model
     * @param namespace
     * @param entity
     * @param overrides entity attribute overrides defined on the referring attribute level
     * @param attributeNameTemplate see {@link Mapping#overrideAs()}
     */
    public SerializedEntity(MappingContext context,
                            Model model,
                            Namespace namespace,
                            Entity entity,
                            Map<String, String> overrides,
                            String attributeNameTemplate) {
        this(context,
                entity.element(),
                entity.packageName(),
                entity.name(),
                Optional.ofNullable(entity.mappings().get(context)).map(EntityTarget::name).orElse(entity.name()),
                attributes(context, model, namespace, entity, overrides, attributeNameTemplate),
                entity.compositeKey() == null ? null : new SerializedEntity(context, model, namespace, entity.compositeKey()));
    }

    private static List<SerializedAttribute> attributes(MappingContext context,
                                                        Model model,
                                                        Namespace namespace,
                                                        Entity entity,
                                                        Map<String, String> overrides,
                                                        String attributeNameTemplate) {
        return entity.attributes().stream().map(attribute -> attribute(context, model, namespace, entity, overrides, attributeNameTemplate, attribute)).toList();
    }

    private static SerializedAttribute attribute(MappingContext context,
                                                 Model model,
                                                 Namespace namespace,
                                                 Entity entity,
                                                 Map<String, String> overrides,
                                                 String attributeNameTemplate,
                                                 Attribute attribute) {
        String name = attribute.serializedNameIn(context);
        String override = overrides.get(name);
        if (override == null && attributeNameTemplate != null) {
            override = attributeNameTemplate.replace("{child}", name);
        }
        String serializedName = override != null ? override : name;
        return new SerializedAttribute(context, model, namespace, entity, attribute, serializedName);
    }

    public boolean generated() {
        return element == null || !name.equals(element.getSimpleName().toString());
    }

    public Stream<SerializedEntity> generatedEntities() {
        return Stream.empty();
    }

    public List<SerializedEntity> embeddedEntities() {
        return attributes.stream()
                .filter(SerializedAttribute::embedded)
                .map(SerializedAttribute::entity)
                .flatMap(entity -> concat(of(entity), entity.embeddedEntities().stream())).toList();
    }

    public SerializedAttribute key() {
        return attributes().stream().filter(SerializedAttribute::primaryKey).findFirst().orElse(null);
    }

    public List<SerializedAttribute> keys() {
        return attributes().stream().filter(SerializedAttribute::primaryKey).toList();
    }

    public List<SerializedAttribute> attributesExcludingKeys() {
        return attributes().stream().filter(not(SerializedAttribute::primaryKey)).toList();
    }

    public Stream<String> imports() {
        return attributes().stream().flatMap(SerializedAttribute::imports).distinct().filter(i -> !i.startsWith("java.lang."));
    }

}
