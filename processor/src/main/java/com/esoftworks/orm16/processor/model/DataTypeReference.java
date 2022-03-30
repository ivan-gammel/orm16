package com.esoftworks.orm16.processor.model;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.esoftworks.orm16.processor.model.ConversionInvocation.invokeOnValue;
import static com.esoftworks.orm16.processor.model.ConversionInvocation.wrap;
import static com.esoftworks.orm16.processor.model.TypeMapping.cast;
import static com.esoftworks.orm16.processor.model.TypeMapping.mapTo;

public record DataTypeReference(String packageName,
                                String name,
                                TypeMapping mapping,
                                boolean primitive) {

    private static final Map<String, Class<?>> PRIMITIVES = Map.of(
            "long", Long.TYPE,
            "int", Integer.TYPE,
            "short", Short.TYPE,
            "byte", Byte.TYPE,
            "double", Double.TYPE,
            "float", Float.TYPE,
            "boolean", Boolean.TYPE,
            "char", Character.TYPE,
            "void", Void.TYPE
    );

    private static final Map<Class<?>, TypeMapping> JDBC_MAPPINGS;

    static {
        var map = new HashMap<Class<?>, TypeMapping>();
        map.put(String.class, mapTo("String"));
        map.put(Integer.class, mapTo("Int", null, null));
        map.put(int.class, mapTo("Int", null, null));
        map.put(Long.class, mapTo("Long", null, null));
        map.put(long.class, mapTo("Long", null, null));
        map.put(Short.class, mapTo("Short", null, null));
        map.put(short.class, mapTo("Short", null, null));
        map.put(Byte.class, mapTo("Byte", null, null));
        map.put(byte.class, mapTo("byte", null, null));
        map.put(Double.class, mapTo("Double", null, null));
        map.put(double.class, mapTo("double", null, null));
        map.put(Float.class, mapTo("Float", null, null));
        map.put(float.class, mapTo("float", null, null));
        map.put(Date.class, mapTo("Date", null, null));
        map.put(Time.class, mapTo("Time", null, null));
        map.put(Timestamp.class, mapTo("Timestamp", null, null));
        map.put(Instant.class, mapTo("Timestamp", invokeOnValue("toInstant"), wrap(java.sql.Timestamp.class, "from")));
        map.put(LocalDateTime.class, mapTo("Timestamp", invokeOnValue("toLocalDateTime"), wrap(java.sql.Timestamp.class, "valueOf")));
        map.put(LocalDate.class, mapTo("Date", invokeOnValue("toLocalDate"), wrap(java.sql.Date.class, "valueOf")));
        map.put(LocalTime.class, mapTo("Time", invokeOnValue("toLocalTime"), wrap(java.sql.Time.class, "valueOf")));
        map.put(UUID.class, cast("Object"));
        JDBC_MAPPINGS = Collections.unmodifiableMap(map);

    }

    public static DataTypeReference forName(String name) {
        int delimiter = name.lastIndexOf('.');
        if (delimiter == 0 || delimiter == name.length() - 1) throw new IllegalArgumentException("Invalid name of the converterType: " + name);
        boolean primitive = false;
        String packageName = delimiter > 0 ? name.substring(0, delimiter) : null;
        String typeName = name.substring(delimiter + 1);
        Class<?> aClass = null;
        if (delimiter < 0) {
            primitive = true;
            aClass = PRIMITIVES.get(name);
            if (aClass == null) {
                throw new IllegalArgumentException("Unsupported converterType: " + name);
            }
        } else {
            try {
                aClass = Class.forName(name);
            } catch (ClassNotFoundException e) {
                // fine, we assume this class is not available yet and will be deserialized as binary value
            }
        }
        TypeMapping mapping = aClass != null ? mapping = JDBC_MAPPINGS.get(aClass) : cast("Object");
        return new DataTypeReference(packageName, typeName, mapping, primitive);
    }

    public String qualifiedName() {
        return primitive ? name : packageName + "." + name;
    }

}
