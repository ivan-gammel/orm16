package com.esoftworks.orm16.processor.builders;

import com.esoftworks.orm16.core.annotations.MappingContext;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class MappingSpec {

    private final AnnotationMirror mirror;
    private final Map<String, AnnotationValue> values;
    private final Elements elements;

    public MappingSpec(Elements elements, AnnotationMirror mirror) {
        this.elements = elements;
        this.mirror = mirror;
        this.values = elements.getElementValuesWithDefaults(mirror).entrySet().stream().collect(toMap(entry -> entry.getKey().getSimpleName().toString(), Map.Entry::getValue));
    }

    public <T> T asValue(String name, Class<T> type) {
        return type.cast(get(name));
    }

    private Object get(String name) {
        AnnotationValue annotationValue = values.get(name);
        if (annotationValue == null) throw new IllegalArgumentException("Annotation field " + name + " does not exist");
        return annotationValue.getValue();
    }

    public <T extends Enum<T>> T asEnum(String name, Class<T> type) {
        if (!type.isEnum()) throw new IllegalArgumentException("Type " + type.getName() + " is not enumeration");
        Object value = get(name);
        return asEnum(type, value);

    }

    private <T extends Enum<T>> T asEnum(Class<T> type, Object value) {
        if (value instanceof VariableElement v) {
            String constantName = v.getSimpleName().toString();
            return Stream.of(type.getEnumConstants()).filter(c -> c.name().equals(constantName)).findAny().orElse(null);
        } else {
            throw new IllegalArgumentException("Annotation value is not enumeration: " + value.getClass().getName());
        }
    }

    @SuppressWarnings({"unchecked", "raw"})
    public <T> T[] asArray(String name, Class<T> type) {
        Object value = get(name);
        if (value instanceof List list) {
            var result = list.stream().map(v -> ((AnnotationValue) v).getValue()).map(v -> type.isEnum() ? asEnum((Class) type, v) : type.cast(v)).toList();
            return (T[]) result.toArray((T[]) Array.newInstance(type, result.size()));
        } else {
            throw new IllegalArgumentException("Annotation value is not an array: " + value.getClass().getName());
        }
    }

    public TypeElement asType(String name) {
        Object annotationValue = get(name);
        if (annotationValue instanceof DeclaredType mirror) {
            return (TypeElement) mirror.asElement();
        } else {
            throw new UnsupportedOperationException("Value is not a type: " + annotationValue);
        }
    }

}
