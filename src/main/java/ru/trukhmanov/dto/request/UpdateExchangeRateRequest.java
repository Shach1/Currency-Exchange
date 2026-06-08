package ru.trukhmanov.dto.request;

public record UpdateExchangeRateRequest(
        String codePair,
        String rate
){
}
