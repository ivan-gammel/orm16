package com.esoftworks.orm16.common;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTest {

    @Test
    public void shouldParseAmount() {
        Money money = Money.fromString("-100.45 EUR");
        assertEquals(BigDecimal.valueOf(-10045, 2), money.value());
        assertEquals("EUR", money.currency().getCurrencyCode());
    }

}
