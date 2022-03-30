package com.esoftworks.orm16.bookstore.model;

import com.esoftworks.orm16.core.annotations.Embeddable;
import com.esoftworks.orm16.core.annotations.SerializationContext;

@Embeddable(context = SerializationContext.PERSISTENCE)
public record PersonalName(String first,
                           String last,
                           ReferenceForm reference) {

}
