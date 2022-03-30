package com.esoftworks.orm16.core.annotations;

public enum SerializationContext {

    /**
     * Serialization context for the data at rest: this context can be used to map the data model to database tables
     * or document model of the storage.
     */
    PERSISTENCE,
    /**
     * Serialization context for the data in motion: this context can be used to map the data model to XML, JSON or
     * any other supported format.
     * @see SerializationFormat
     */
    INTEGRATION

}
