package com.esoftworks.orm16.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target({
        ElementType.RECORD_COMPONENT,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.METHOD
})
public @interface AttributeOverride {

    String map();

    String to();

}
