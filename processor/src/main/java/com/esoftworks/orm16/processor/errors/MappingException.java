package com.esoftworks.orm16.processor.errors;

import javax.lang.model.element.TypeElement;

public abstract class MappingException extends RuntimeException {

    private final TypeElement element;

    public MappingException(TypeElement element) {
        super(element.getQualifiedName().toString());
        this.element = element;
    }

}
