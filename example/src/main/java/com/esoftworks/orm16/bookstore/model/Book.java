package com.esoftworks.orm16.bookstore.model;

import com.esoftworks.orm16.common.Money;
import com.esoftworks.orm16.core.annotations.*;
import com.esoftworks.orm16.core.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.esoftworks.orm16.core.annotations.MappingContext.INTEGRATION;
import static com.esoftworks.orm16.core.annotations.MappingContext.PERSISTENCE;
import static com.esoftworks.orm16.core.annotations.AttributeMappingKind.EMBEDDED;
import static com.esoftworks.orm16.core.annotations.AttributeMappingKind.VALUE;

@MappedEntity(as = "books")
public record Book(
        @Id UUID uuid,
        @References(Author.class) UUID authorId,
        @Mapping(                       // unwrap object (only in database)
                context = PERSISTENCE,
                serializeAs = EMBEDDED,
                overrides = {
                        @AttributeOverride(map = "currencyCode", to = "currency")
                })
        @Mapping(context = INTEGRATION, serializeAs = VALUE) // serialize to string (only in JSON)
        Money price,
        LocalDate published
) {

}
