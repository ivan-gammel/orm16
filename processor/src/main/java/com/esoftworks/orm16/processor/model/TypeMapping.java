package com.esoftworks.orm16.processor.model;

public record TypeMapping(String jdbcType,
                          boolean cast,
                          ConversionInvocation onRead,
                          ConversionInvocation onWrite) {

    public static TypeMapping mapTo(String type) {
        return new TypeMapping(type, false, null, null);
    }

    public static TypeMapping cast(String type) {
        return new TypeMapping(type, true, null, null);
    }

    public static TypeMapping mapTo(String type, ConversionInvocation onRead, ConversionInvocation onWrite) {
        return new TypeMapping(type, false, onRead, onWrite);
    }

}
