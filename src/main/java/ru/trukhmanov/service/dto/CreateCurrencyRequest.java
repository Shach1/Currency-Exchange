package ru.trukhmanov.service.dto;

public record CreateCurrencyRequest(
        String code,
        String name,
        String sign
){
}
