package com.esoftworks.orm16.processor.view;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.*;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

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
                attributes(context, model, namespace, entity),
                entity.compositeKey() == null ? null : new SerializedEntity(context, model, namespace, entity.compositeKey()));
    }

    private static List<SerializedAttribute> attributes(MappingContext context,
                                                        Model model,
                                                        Namespace namespace,
                                                        Entity entity) {
        return entity.attributes().stream().map(attribute -> new SerializedAttribute(context, model, namespace, entity, attribute)).toList();
    }

    public boolean generated() {
        return element == null || !name.equals(element.getSimpleName().toString());
    }

    public Stream<SerializedEntity> generatedEntities() {
        return Stream.empty();
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
