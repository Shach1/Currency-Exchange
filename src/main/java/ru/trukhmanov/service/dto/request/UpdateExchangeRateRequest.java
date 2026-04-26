package ru.trukhmanov.service.dto.request;

public record UpdateExchangeRateRequest(
        String codePair,
        String rate
){
}
