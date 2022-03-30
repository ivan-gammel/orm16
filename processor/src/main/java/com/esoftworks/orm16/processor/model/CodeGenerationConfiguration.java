package com.esoftworks.orm16.processor.model;

import com.esoftworks.orm16.core.annotations.Conversion;
import com.esoftworks.orm16.core.annotations.GeneratedSources;
import com.esoftworks.orm16.core.annotations.SerializationContext;
import com.esoftworks.orm16.core.annotations.SerializationFormat;
import com.esoftworks.orm16.core.converters.Converter;

import java.util.HashMap;
import java.util.Map;

public record CodeGenerationConfiguration(
        SerializationFormat preferredFormat,
        Map<Class<?>, Converter<?, ?>> conversions
) {

    public CodeGenerationConfiguration(GeneratedSources annotation) {
        this(annotation.preferredFormat(), createConverters(annotation.conversions()));
    }

    private static Map<Class<?>, Converter<?, ?>> createConverters(Conversion[] conversions) {
        var map = new HashMap<Class<?>, Converter<?, ?>>();
        for (Conversion conversion : conversions) {
            try {
                map.put(conversion.target(), conversion.converter().getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return map;
    }

    public static Map<SerializationContext, CodeGenerationConfiguration> configure(GeneratedSources annotation) {
        var map = new HashMap<SerializationContext, CodeGenerationConfiguration>();
        CodeGenerationConfiguration config = new CodeGenerationConfiguration(annotation);
        for (SerializationContext target : annotation.target()) {
            map.put(target, config);
        }
        return map;
    }


}
