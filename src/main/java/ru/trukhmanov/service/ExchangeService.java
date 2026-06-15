package ru.trukhmanov.service;

import ru.trukhmanov.dto.request.ExchangeRequestDto;
import ru.trukhmanov.dto.response.ExchangeDto;

import java.math.RoundingMode;

public interface ExchangeService{
    Integer AMOUNT_SCALE = 2;
    Integer RATE_SCALE = 6;
    RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    ExchangeDto calculateExchange(ExchangeRequestDto request);
}
