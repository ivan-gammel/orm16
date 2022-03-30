package com.esoftworks.orm16.processor.builders;

import com.esoftworks.orm16.processor.model.builder.ModelBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class IdContributor implements ModelContributor {

    @Override
    public void accept(ModelBuilder builder,
                       Set<? extends Element> annotatedElements,
                       ProcessingEnvironment env) {
        for (Element element : annotatedElements) {
            if (element instanceof RecordComponentElement attribute) {
                var clazz = (TypeElement) attribute.getEnclosingElement();
                builder.merge(clazz, env).ifPresent(entity -> entity.merge(attribute, env).asPrimaryKey());
            } // ignore Var/Method symbols
        }
    }
}
