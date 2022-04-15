package com.example;

import com.esoftworks.orm16.core.annotations.*;

import java.time.Instant;
import java.util.UUID;

@MappedEntity(
    context = MappingContext.PERSISTENCE,
    as = "embed_examples"
)
public record EmbedExample(@Id UUID uuid,
                           @Mapping(context = MappingContext.PERSISTENCE,
                                    serializeAs = AttributeMappingKind.EMBEDDED) PersonalName name) {

    public EmbedExample {
    }

}