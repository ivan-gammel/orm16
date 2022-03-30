package com.esoftworks.orm16.core.annotations;

import com.esoftworks.orm16.core.converters.Converter;

import java.lang.annotation.*;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({
        ElementType.MODULE,
        ElementType.PACKAGE,
        ElementType.TYPE,
        ElementType.RECORD_COMPONENT,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
})
public @interface Conversion {

    Class<? extends Converter> converter();
    Class<?> target();

}
