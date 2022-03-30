package com.esoftworks.orm16.bookstore.mapping;

import com.esoftworks.orm16.common.Money;
import com.esoftworks.orm16.core.converters.StringConverter;


public class MoneyAsString implements StringConverter<Money> {

    public MoneyAsString() {}

    public String serialize(Money money) {
        return money == null ? null : money.toString();
    }

    public Money deserialize(String string) {
        return string == null ? null : Money.fromString(string);
    }

}
