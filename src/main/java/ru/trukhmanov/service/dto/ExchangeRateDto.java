package ru.trukhmanov.service.dto;

import ru.trukhmanov.model.entity.Currency;

import java.math.BigDecimal;

public record ExchangeRateDto(
        Integer id,
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate
){}
