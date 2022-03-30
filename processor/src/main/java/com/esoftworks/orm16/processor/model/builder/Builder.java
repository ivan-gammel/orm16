package com.esoftworks.orm16.processor.model.builder;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public interface Builder<T> {

    T build();

    static <T> T given(Builder<T> builder) {
        return builder.build();
    }

    static <T> List<T> givenAll(Iterable<? extends Builder<T>> builders) {
        return stream(builders.spliterator(), false)
                .map(Builder::build)
                .collect(toList());
    }

}
