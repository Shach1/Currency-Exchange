package ru.trukhmanov.service.dto.request;

public record CreateCurrencyRequest(
        String code,
        String name,
        String sign
){
}
