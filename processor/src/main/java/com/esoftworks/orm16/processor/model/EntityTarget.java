package com.esoftworks.orm16.processor.model;

/**
 * @param name name of the serialized version of the entity (e.g. table or XML element)
 * @param embeddable indicates that this entity can be "embedded", i.e. mapped as a list of attributes of the container.
 */
public record EntityTarget(String name,
                           boolean embeddable) implements Target {
}
