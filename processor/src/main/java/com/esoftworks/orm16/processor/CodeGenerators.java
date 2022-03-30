package com.esoftworks.orm16.processor;

import com.esoftworks.orm16.core.annotations.SerializationContext;
import com.esoftworks.orm16.processor.model.jdbc.JdbcCodeGenerator;

public class CodeGenerators {

    public static ContextSpecificGenerator forTarget(SerializationContext ctx) {
        switch (ctx) {
            case PERSISTENCE: return new JdbcCodeGenerator();
            default: throw new UnsupportedOperationException();
        }
    }

}
