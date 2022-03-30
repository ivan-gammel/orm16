package com.esoftworks.orm16.core.annotations;

import java.lang.annotation.*;

import static com.esoftworks.orm16.core.annotations.SerializationContext.INTEGRATION;
import static com.esoftworks.orm16.core.annotations.SerializationContext.PERSISTENCE;

@Documented
@Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target({
        ElementType.TYPE,
        ElementType.PACKAGE
})
@Repeatable(SerializationTargets.class)
public @interface SerializedEntity {

    SerializationContext[] context() default {
        PERSISTENCE, INTEGRATION
    };

    String value() default "";

}
