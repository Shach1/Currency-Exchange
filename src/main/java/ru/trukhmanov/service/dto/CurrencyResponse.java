package ru.trukhmanov.service.dto;

public record CurrencyResponse(
        Integer id,
        String code,
        String fullName,
        String sign
){}
