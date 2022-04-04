package com.esoftworks.orm16.core.annotations;

import java.lang.annotation.*;

import static com.esoftworks.orm16.core.annotations.MappingContext.INTEGRATION;
import static com.esoftworks.orm16.core.annotations.MappingContext.PERSISTENCE;

@Documented
@Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target({
        ElementType.TYPE,
        ElementType.PACKAGE
})
@Repeatable(EntityMappings.class)
public @interface MappedEntity {

    MappingContext[] context() default {
        PERSISTENCE, INTEGRATION
    };

    String as() default "";

}
