package com.esoftworks.orm16.core.annotations;

import java.lang.annotation.*;

/**
 * Code generation policy for certain serialization contexts
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target({
        ElementType.PACKAGE,
        ElementType.MODULE
})
@Repeatable(CodeGenerator.class)
public @interface GeneratedSources {

    /**
     * Preferred serialization format when writing objects as strings
     * @return
     */
    OutputFormat preferredFormat() default OutputFormat.JSON;

    /**
     * Targets for which this code generation policy applies
     * @return list of applicable serialization contexts
     */
    MappingContext[] target();

    /**
     * Custom conversions applied in given serialization contexts.
     * @return list of applicable conversions
     */
    Conversion[] conversions() default {};

}
