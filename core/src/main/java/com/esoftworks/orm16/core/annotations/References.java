package com.esoftworks.orm16.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.RECORD_COMPONENT, ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface References {

    Class<?> value();

    Class<?> entity() default Object.class;

    String as() default "";

}
