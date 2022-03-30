package com.esoftworks.orm16.core.annotations;

import java.lang.annotation.*;

/**
 * Code generation policy for certain serialization contexts
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target({
        ElementType.TYPE,
        ElementType.PACKAGE,
        ElementType.RECORD_COMPONENT,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.METHOD
})
@Repeatable(CodeGenerator.class)
public @interface GeneratedSources {

    /**
     * Preferred serialization format when writing objects as strings
     * @return
     */
    SerializationFormat preferredFormat() default SerializationFormat.JSON;

    /**
     * Targets for which this code generation policy applies
     * @return list of applicable serialization contexts
     */
    SerializationContext[] target();

    /**
     * Custom conversions applied in given serialization contexts.
     * @return list of applicable conversions
     */
    Conversion[] conversions() default {};

}
