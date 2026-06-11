package ru.trukhmanov.dto.request;

public record CreateExchangeRateRequestDto(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String rate
){
}
