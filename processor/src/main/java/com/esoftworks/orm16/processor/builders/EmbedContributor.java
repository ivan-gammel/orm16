package com.esoftworks.orm16.processor.builders;

import com.esoftworks.orm16.processor.model.builder.ModelBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Set;

public class EmbedContributor implements ModelContributor {

    @Override
    public void accept(ModelBuilder builder,
                       Set<? extends Element> annotatedElements,
                       ProcessingEnvironment env) {
        for (Element element : annotatedElements) {
            if (element instanceof RecordComponentElement attribute) {
                var clazz = (TypeElement) attribute.getEnclosingElement();
                TypeMirror returnType = attribute.getAccessor().getReturnType();
                if (returnType.getKind() != TypeKind.DECLARED) {
                    env.getMessager().printMessage(Diagnostic.Kind.ERROR, returnType.toString() + " cannot be embedded as " + clazz.getSimpleName() + "#" + attribute.getSimpleName());
                    continue;
                }
                var typeElement = (TypeElement) ((DeclaredType) returnType).asElement();
                builder.merge(typeElement, env).ifPresent(embeddedEntity -> {
                    builder.merge(clazz, env).ifPresent(entity -> entity.merge(attribute, env).asEmbedded(embeddedEntity));
                });
            } // ignore Var/Method symbols
        }
    }

}
