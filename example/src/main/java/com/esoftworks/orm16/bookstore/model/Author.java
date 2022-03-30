package com.esoftworks.orm16.bookstore.model;

import com.esoftworks.orm16.core.annotations.*;

import java.util.UUID;

@SerializedEntity("authors")
public record Author(@Id UUID uuid, @Embed PersonalName name) {

    public Author with(PersonalName newName) {
        return new Author(uuid, newName);
    }

}
