package com.esoftworks.orm16.processor.model.builder;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.CodeGenerationConfiguration;
import com.esoftworks.orm16.processor.model.Namespace;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;

public class NamespaceBuilder implements Builder<Namespace> {

    private final String name;
    private final Map<String, EntityBuilder> entities = new HashMap<>();
    private Map<MappingContext, CodeGenerationConfiguration> configuration = new HashMap<>();

    public NamespaceBuilder(PackageElement e) {
        name = e.getQualifiedName().toString();
    }

    public NamespaceBuilder add(TypeElement element,
                                ProcessingEnvironment processingEnv) {
        merge(element, processingEnv);
        return this;
    }

    @Override
    public Namespace build() {
        return new Namespace(name, Builder.givenAll(entities.values()), configuration);
    }

    public EntityBuilder merge(TypeElement element, ProcessingEnvironment env) {
        var builder = entities.computeIfAbsent(element.getSimpleName().toString(), $ -> new EntityBuilder(name, element, env));
        env.getMessager().printMessage(Diagnostic.Kind.NOTE, "Added serializable entity " + element.getSimpleName());
        return builder;
    }

}
