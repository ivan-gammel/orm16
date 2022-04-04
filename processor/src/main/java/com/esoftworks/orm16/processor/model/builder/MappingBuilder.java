package com.esoftworks.orm16.processor.model.builder;

import com.esoftworks.orm16.core.annotations.AttributeOverride;
import com.esoftworks.orm16.core.annotations.OutputFormat;
import com.esoftworks.orm16.processor.model.AttributeTarget;

public class MappingBuilder implements Builder<AttributeTarget> {

    private String name;
    private EntityBuilder embeddedEntity;
    private AttributeOverride[] overrides;
    private Class<?> targetClass;
    private OutputFormat format;
    private String pattern;

    public MappingBuilder mapTo(String name) {
        this.name = name;
        return this;
    }

    public MappingBuilder asEmbedded(EntityBuilder embeddedEntity,
                                     AttributeOverride[] overrides) {
        this.embeddedEntity = embeddedEntity;
        this.overrides = overrides;
        return this;
    }

    public MappingBuilder convertTo(Class<?> targetClass) {
        this.targetClass = targetClass;
        return this;
    }

    public MappingBuilder transform(OutputFormat format) {
        this.format = format;
        return this;
    }

    public MappingBuilder applyPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public boolean conversionRequired() {
        return this.targetClass != null;
    }

    @Override
    public AttributeTarget build() {
        return new AttributeTarget(name, embeddedEntity.build(), conversionRequired() ? new ConversionTarget(targetClass, format, pattern) : null);
    }
}
