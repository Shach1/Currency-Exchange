package ru.trukhmanov.dto.request;

public record UpdateExchangeRateRequestDto(
        String codePair,
        String rate
){
}
