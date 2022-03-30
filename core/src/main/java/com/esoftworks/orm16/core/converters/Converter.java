package com.esoftworks.orm16.core.converters;

public interface Converter<T, S> {

    S serialize(T value);

    T deserialize(S value);

}
