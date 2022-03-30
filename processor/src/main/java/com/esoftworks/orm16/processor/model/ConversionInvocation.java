package com.esoftworks.orm16.processor.model;

import java.util.stream.Stream;

public record ConversionInvocation(String packageName,
                                   String converterType,
                                   String method) {

    public static ConversionInvocation invokeOnValue(String method) {
        return new ConversionInvocation(null, null, method);
    }

    public static ConversionInvocation wrap(Class<?> type, String method) {
        return new ConversionInvocation(type.getPackageName(), type.getSimpleName(), method);
    }

    public boolean wrap() {
        return converterType != null;
    }

    public Stream<String> imports() {
        return Stream.of(packageName);
    }

}
