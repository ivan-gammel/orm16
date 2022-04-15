package com.esoftworks.orm16.processor.model;

import com.esoftworks.orm16.core.annotations.Mapping;
import com.esoftworks.orm16.processor.model.builder.ConversionTarget;

import java.util.Map;

/**
 * Model representation of {@link com.esoftworks.orm16.core.annotations.Mapping} annotation. Attribute target
 * is defined within some context.
 *
 * @param name mapped name of the attribute
 * @param embeddedEntity if set, indicates that this attribute is mapped as given embedded entity
 * @param conversionTarget if set, defines the conversion method for the attribute values
 * @param overrides name overrides for specific attributes of embedded entity, see {@link Mapping#overrides()}
 * @param attributeNameTemplate see {@link Mapping#overrideAs()}
 */
public record AttributeTarget(String name,
                              Entity embeddedEntity,
                              ConversionTarget conversionTarget,
                              Map<String, String> overrides,
                              String attributeNameTemplate) implements Target {

    /**
     * @return <code>true</code> if this attribute is mapped via embedded entity,
     *         <code>false</code> if attribute is mapped as value.
     */
    public boolean embedded() {
        return this.embeddedEntity != null;
    }

}
