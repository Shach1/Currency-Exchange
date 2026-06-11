package ru.trukhmanov.service;

import ru.trukhmanov.dto.request.ExchangeRequestDto;
import ru.trukhmanov.dto.response.ExchangeDto;

public interface ExchangeService{
    Integer SCALE = 2;

    ExchangeDto calculateExchange(ExchangeRequestDto request);
}
