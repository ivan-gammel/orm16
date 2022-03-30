package com.esoftworks.orm16.core.annotations;

import java.io.Serializable;
import java.lang.annotation.*;

import static com.esoftworks.orm16.core.annotations.SerializationContext.INTEGRATION;
import static com.esoftworks.orm16.core.annotations.SerializationContext.PERSISTENCE;

/**
 * The mapping of an attribute to external data model depends on whether there exists known
 * mapping between the attribute class and target JDBC data type and whether the serialization is customized.
 *
 * <ul>
 * <li>Known type - use get/setXXX</li>
 * <li>Known type, has mapping - attempt conversion, then use get/setXXX</li>
 * <li>Unknown type:<ul>
 *     <li>serialize "as is" using get/setObject</li>
 *     <li>serialize as value using model conversion (factory method or constructor and serialization method)</li>
 *     <li>serialize as formatted string using designated serializer</li>
 * </ul></li>
 * </ul>
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target({
        ElementType.RECORD_COMPONENT
})
@Repeatable(Mappings.class)
public @interface Mapping {

    /**
     * By default same mapping applies to both contexts
     * @return
     */
    SerializationContext[] context() default {
            PERSISTENCE,
            INTEGRATION
    };

    /**
     * @return name of the serialized attribute or column name
     */
    String to() default "";

    /**
     * Indicates that before serialization the value of the mapped attribute must be converted to the specified data type.
     * Default value is {@link java.io.Serializable}, indicating that no additional conversion is necessary.
     * @return type of the converted value to be serialized
     */
    Class<?> as() default Object.class;

    /**
     *
     * @return
     */
    String pattern() default "";

    /**
     *
     * @return
     */
    SerializationFormat format() default SerializationFormat.NONE;

    /**
     *
     * @return
     */
    AttributeOverride[] overrides() default {};

}
