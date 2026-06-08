package ru.trukhmanov.dto.request;

public record CreateExchangeRateRequest(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String rate
){
}
