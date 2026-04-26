package ru.trukhmanov.model.entity;

import java.math.BigDecimal;

public record ExchangeRate(
        Integer id,
        Integer baseCurrencyId,
        Integer targetCurrencyId,
        BigDecimal rate
){
}
