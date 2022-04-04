package com.esoftworks.orm16.bookstore.model;

import com.esoftworks.orm16.core.annotations.Id;
import com.esoftworks.orm16.core.annotations.MappedEntity;
import com.esoftworks.orm16.core.annotations.Mapping;

import java.util.UUID;

import static com.esoftworks.orm16.core.annotations.AttributeMappingKind.EMBEDDED;
import static com.esoftworks.orm16.core.annotations.MappingContext.PERSISTENCE;

@MappedEntity(as = "authors")
public record Author(@Id UUID uuid,
                     @Mapping(context = PERSISTENCE, serializeAs = EMBEDDED) PersonalName name) {

    public Author with(PersonalName newName) {
        return new Author(uuid, newName);
    }

}
