package ru.trukhmanov.service.dto;

public record CreateExchangeRateRequest(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String rate
){
}
