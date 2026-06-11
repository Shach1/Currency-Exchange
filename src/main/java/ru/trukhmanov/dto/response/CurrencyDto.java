package ru.trukhmanov.dto.response;

public record CurrencyDto(
        Integer id,
        String code,
        String name,
        String sign
){}
