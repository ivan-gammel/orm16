package com.esoftworks.orm16.processor.model.builder;

import com.esoftworks.orm16.core.annotations.AttributeOverride;
import com.esoftworks.orm16.core.annotations.Embed;
import com.esoftworks.orm16.core.annotations.Mapping;
import com.esoftworks.orm16.core.annotations.SerializedEntity;
import com.esoftworks.orm16.processor.model.AttributeTarget;

public class MappingBuilder implements Builder<AttributeTarget> {

    private String name;
    private boolean embedded;
    private AttributeOverride[] overrides;

    public MappingBuilder basedOn(Mapping target) {
        this.name = target.to();

        this.overrides = target.overrides();
        return this;
    }

    public MappingBuilder asEmbedded(Embed embed) {
        this.embedded = true;
        return this;
    }

    @Override
    public AttributeTarget build() {
        return new AttributeTarget(name, embedded);
    }
}
