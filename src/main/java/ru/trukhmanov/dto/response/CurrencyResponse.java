package ru.trukhmanov.dto.response;

public record CurrencyResponse(
        Integer id,
        String code,
        String name,
        String sign
){}
