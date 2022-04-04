@GeneratedSources(
        target = MappingContext.PERSISTENCE,
        conversions = @Conversion(
                converter = MoneyAsString.class,
                target = Money.class
        )
)
package com.esoftworks.orm16.bookstore.model;

import com.esoftworks.orm16.bookstore.mapping.MoneyAsString;
import com.esoftworks.orm16.common.Money;
import com.esoftworks.orm16.core.annotations.Conversion;
import com.esoftworks.orm16.core.annotations.GeneratedSources;
import com.esoftworks.orm16.core.annotations.MappingContext;
