package ru.trukhmanov.dto.request;

public record CreateCurrencyRequest(
        String code,
        String name,
        String sign
){
}
