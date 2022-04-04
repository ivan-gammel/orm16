package com.example;

import com.esoftworks.orm16.core.annotations.Id;
import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.core.annotations.MappedEntity;

import java.time.Instant;
import java.util.UUID;

@MappedEntity(
    context = MappingContext.PERSISTENCE,
    as = "documents"
)
public record Document(@Id UUID uuid,
                       Instant created,
                       String subject,
                       String content) {

    public Document {
    }

}