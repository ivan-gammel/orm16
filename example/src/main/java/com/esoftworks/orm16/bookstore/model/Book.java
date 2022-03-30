package com.esoftworks.orm16.bookstore.model;

import com.esoftworks.orm16.common.Money;
import com.esoftworks.orm16.core.annotations.*;

import java.time.LocalDate;
import java.util.UUID;

@SerializedEntity("books")
public record Book(
        @Id UUID uuid,
        @References(Author.class) UUID authorId,
        Money price,
        LocalDate published
) {


}
