package ru.trukhmanov.dto.request;

public record CreateCurrencyRequestDto(
        String code,
        String name,
        String sign
){
}
