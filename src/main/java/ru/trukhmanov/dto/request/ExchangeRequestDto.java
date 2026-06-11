package ru.trukhmanov.dto.request;

public record ExchangeRequestDto(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String amount){
}
