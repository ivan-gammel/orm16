package com.esoftworks.orm16.processor.model;

import com.esoftworks.orm16.processor.model.builder.ConversionTarget;

public record AttributeTarget(String name,
                              Entity embeddedEntity,
                              ConversionTarget conversionTarget) implements Target {

    public boolean embedded() {
        return this.embeddedEntity != null;
    }

}
