package com.esoftworks.orm16.processor.model;

public sealed interface Target
        permits
        AttributeTarget,
        EntityTarget {

    String name();

}
