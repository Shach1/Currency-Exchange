package ru.trukhmanov.service.dto;

public record CurrencyDto(
        Integer id,
        String code,
        String fullName,
        String sign
){}
