package com.esoftworks.orm16.common;

import java.math.BigDecimal;

public record Money(BigDecimal value,
                    String currencyCode) {

    public static Money fromString(String value) {
        String[] components = value.split(" ");
        return new Money(new BigDecimal(components[0]), components[1]);
    }

}
