package com.esoftworks.orm16.core.annotations;

import java.io.Serializable;
import java.lang.annotation.*;

import static com.esoftworks.orm16.core.annotations.MappingContext.INTEGRATION;
import static com.esoftworks.orm16.core.annotations.MappingContext.PERSISTENCE;

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
@Repeatable(AttributeMappings.class)
public @interface Mapping {

    /**
     * The same mapping applies to both contexts by default.
     * @return the context in which this mapping is applicable.
     */
    MappingContext[] context() default {
            PERSISTENCE,
            INTEGRATION
    };

    /**
     * @return kind of mapping for this attribute
     * @see AttributeMappingKind
     */
    AttributeMappingKind serializeAs() default AttributeMappingKind.VALUE;

    /**
     * @return name of the serialized attribute or column name
     */
    String to() default "";

    /**
     * Indicates that before serialization the value of the mapped attribute must be converted to the specified data type.
     * Default value is {@link java.io.Serializable}, indicating that no additional conversion is necessary.
     * @return type of the converted value to be serialized
     */
    Class<?> as() default Serializable.class;

    /**
     * This property can be used only in combination with {@link #as()} = {@link String}.class, {@link #serializeAs()} = {@link AttributeMappingKind#VALUE}.
     * Incorrect use will generate compile time error.
     * @return the output format pattern of the string value of this attribute after conversion
     * @see OutputFormat
     */
    String pattern() default "";

    /**
     * This property can be used only in combination with {@link #as()} = {@link String}.class and {@link #serializeAs()} = {@link AttributeMappingKind#VALUE}.
     * Incorrect use will generate compile time error.
     * @return the output format of the string value of this attribute after conversion
     * @see OutputFormat
     */
    OutputFormat format() default OutputFormat.NONE;

    /**
     * This property can be used only in combination with {@link #serializeAs()} = {@link AttributeMappingKind#EMBEDDED}.
     * Incorrect use will generate compile time error.
     * @return mappings of the attributes of the embedded entity
     * @see AttributeOverride
     * @see AttributeMappingKind
     */
    AttributeOverride[] overrides() default {};

    /**
     * Defines name override pattern for embedded entity attributes. The pattern may include {parent} and {child} macros:
     * <dl>
     *     <dt>parent</dt>
     *     <dd>The name of this attribute</dd>
     *     <dt>child</dt>
     *     <dd>The name of the attribute of the embedded entity</dd>
     * </dl>
     * This property can be used only in combination with {@link #serializeAs()} = {@link AttributeMappingKind#EMBEDDED}.
     * Incorrect use will generate compile time error.
     * @return mapping pattern for the attributes of the embedded entity
     * @see AttributeMappingKind
     */
    String overrideAs() default "{parent}_{child}";

}
