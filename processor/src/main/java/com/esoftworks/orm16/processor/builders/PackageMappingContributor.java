package com.esoftworks.orm16.processor.builders;

import com.esoftworks.orm16.processor.model.builder.ModelBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Set;

public class PackageMappingContributor implements ModelContributor {

    @Override
    public void accept(ModelBuilder builder,
                       Set<? extends Element> annotatedElements,
                       ProcessingEnvironment processingEnv) {
    }
}
