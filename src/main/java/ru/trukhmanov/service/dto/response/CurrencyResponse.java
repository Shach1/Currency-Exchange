package ru.trukhmanov.service.dto.response;

public record CurrencyResponse(
        Integer id,
        String code,
        String name,
        String sign
){}
