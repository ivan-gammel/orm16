package com.example;

import com.esoftworks.orm16.core.annotations.Id;
import com.esoftworks.orm16.core.annotations.SerializationContext;
import com.esoftworks.orm16.core.annotations.SerializedEntity;

import java.time.Instant;
import java.util.UUID;

@SerializedEntity(
    context = SerializationContext.PERSISTENCE,
    value = "documents"
)
public record Document(@Id UUID uuid,
                       Instant created,
                       String subject,
                       String content) {

    public Document {
    }

}