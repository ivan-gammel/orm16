package com.esoftworks.orm16.conversions;

public interface Conversion {

    Class<?> model();

    Class<?> serializedValue();

    String template();

    String importsMacro();

    String serializeMacro();

    String deserializeMacro();

}
