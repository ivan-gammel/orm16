package com.esoftworks.orm16.conversions;

import java.util.Set;
import java.util.UUID;

public final class DefaultConversions {

    public static final Set<Conversion> CONVERSIONS;

    static {
        CONVERSIONS = Set.of(
                new ConversionMacro(java.time.LocalDate.class, java.sql.Date.class),
                new ConversionMacro(java.time.LocalTime.class, java.sql.Time.class),
                new ConversionMacro(java.time.LocalDateTime.class, java.sql.Timestamp.class),
                new ConversionMacro(java.time.Instant.class, java.sql.Timestamp.class),
                new ConversionMacro(UUID.class, java.sql.Timestamp.class)
        );
    }


    record ConversionMacro(Class<?> model,
                           Class<?> serializedValue) implements Conversion {
        @Override
        public String template() {
            return "DefaultConversions";
        }

        @Override
        public String importsMacro() {
            return "imports" + model.getSimpleName();
        }

        @Override
        public String serializeMacro() {
            return "serialize" + model.getSimpleName();
        }

        @Override
        public String deserializeMacro() {
            return "deserialize" + model.getSimpleName();
        }
    }

}
