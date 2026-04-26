package ru.trukhmanov.service.dto.response;

import java.math.BigDecimal;

public record ExchangeRateResponse(
        Integer id,
        CurrencyResponse baseCurrency,
        CurrencyResponse targetCurrency,
        BigDecimal rate
){
}
