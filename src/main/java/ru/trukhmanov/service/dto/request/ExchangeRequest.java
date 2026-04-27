package ru.trukhmanov.service.dto.request;

public record ExchangeRequest(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String amount){
}
