package com.esoftworks.orm16.processor.model.builder;

import com.esoftworks.orm16.processor.model.Model;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelBuilder implements Builder<Model> {

    private final Map<String, NamespaceBuilder> packages = new HashMap<>();
    private final Messager messager;

    public ModelBuilder(Messager messager) {
        this.messager = messager;
    }

    public ModelBuilder add(ModuleElement moduleElement) {
        messager.printMessage(Diagnostic.Kind.WARNING, "Module-level serialization annotations are not supported yet");
        return this;
    }

    public ModelBuilder add(PackageElement packageElement) {
        String name = packageElement.getQualifiedName().toString();
        packages.computeIfAbsent(name, $ -> new NamespaceBuilder(packageElement));
        return this;
    }

    public ModelBuilder add(TypeElement element,
                            ProcessingEnvironment processingEnv) {
        merge(element, processingEnv);
        return this;
    }

    public Optional<EntityBuilder> merge(TypeElement element, ProcessingEnvironment processingEnv) {
        Element e = element.getEnclosingElement();
        if (!(e instanceof PackageElement packageElement)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Element " + element.getSimpleName() + " has no enclosing package and will be omitted");
            return Optional.empty();
        }
        String name = packageElement.getQualifiedName().toString();
        NamespaceBuilder namespaceBuilder = packages.computeIfAbsent(name, $ -> new NamespaceBuilder(packageElement));
        return Optional.of(namespaceBuilder.merge(element, processingEnv));
    }

    @Override
    public Model build() {
        return new Model(Builder.givenAll(packages.values()));
    }

}
