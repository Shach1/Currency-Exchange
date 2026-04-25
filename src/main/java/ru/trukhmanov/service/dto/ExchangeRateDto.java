package ru.trukhmanov.service.dto;

import java.math.BigDecimal;

public record ExchangeRateDto(
        Integer id,
        CurrencyDto baseCurrency,
        CurrencyDto targetCurrency,
        BigDecimal rate
){
}
