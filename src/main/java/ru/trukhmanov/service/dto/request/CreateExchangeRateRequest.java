package ru.trukhmanov.service.dto.request;

public record CreateExchangeRateRequest(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String rate
){
}
