package com.esoftworks.orm16.common;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal value, Currency currency) {

    public static Money fromString(String value) {
        String[] components = value.split(" ");
        return new Money(new BigDecimal(components[0]), Currency.getInstance(components[1]));
    }

}
