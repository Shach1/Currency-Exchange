package ru.trukhmanov.service.dto.request;

public record UpdateCurrencyRequest(
        String id,
        String code,
        String name,
        String sign
){
}
