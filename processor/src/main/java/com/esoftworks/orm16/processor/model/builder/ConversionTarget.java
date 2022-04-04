package com.esoftworks.orm16.processor.model.builder;

import com.esoftworks.orm16.core.annotations.OutputFormat;

public record ConversionTarget (Class<?> targetClass,
                                OutputFormat format,
                                String pattern) {
}
