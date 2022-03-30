package com.esoftworks.orm16.processor.errors;

import javax.lang.model.element.TypeElement;

public class MissingPrimaryKeyException extends MappingException {

    public MissingPrimaryKeyException(TypeElement element) {
        super(element);
    }

}
