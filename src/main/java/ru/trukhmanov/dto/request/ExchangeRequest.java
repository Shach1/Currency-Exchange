package ru.trukhmanov.dto.request;

public record ExchangeRequest(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String amount){
}
