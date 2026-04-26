package ru.trukhmanov.service.dto;

public record UpdateCurrencyRequest(
        String id,
        String code,
        String name,
        String sign
){
}
