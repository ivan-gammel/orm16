package com.esoftworks.orm16.processor;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.jdbc.JdbcCodeGenerator;

public class CodeGenerators {

    public static ContextSpecificGenerator forTarget(MappingContext ctx) {
        switch (ctx) {
            case PERSISTENCE: return new JdbcCodeGenerator();
            default: throw new UnsupportedOperationException(ctx + " mapping context not supported yet");
        }
    }

}
