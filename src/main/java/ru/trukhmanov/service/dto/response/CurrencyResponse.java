package ru.trukhmanov.service.dto.response;

public record CurrencyResponse(
        Integer id,
        String code,
        String fullName,
        String sign
){}
