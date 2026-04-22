package ru.trukhmanov.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ExchangeRate(
        Integer id,
        Integer baseCurrencyId,
        Integer targetCurrencyId,
        BigDecimal rate
){
    public ExchangeRate{
        if(baseCurrencyId == null) throw new IllegalArgumentException("Base currency id cannot be null");
        if(baseCurrencyId < 1) throw new IllegalArgumentException("Base currency id cannot be less than 1");

        if(targetCurrencyId == null) throw new IllegalArgumentException("Target currency id cannot be null");
        if(targetCurrencyId < 1) throw new IllegalArgumentException("Target currency id cannot be less than 1");
        if(baseCurrencyId.equals(targetCurrencyId))
            throw new IllegalArgumentException("Base and target currency identifiers cannot be equal");

        if(rate == null) throw new IllegalArgumentException("Rate cannot be null");
        if(rate.equals(BigDecimal.ZERO)) throw new IllegalArgumentException("Exchange rate cannot be 0");
        rate = rate.setScale(6, RoundingMode.DOWN);
    }
}
