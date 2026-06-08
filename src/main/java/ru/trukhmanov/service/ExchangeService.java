package ru.trukhmanov.service;

import ru.trukhmanov.dto.request.ExchangeRequest;
import ru.trukhmanov.dto.response.ExchangeResponse;

public interface ExchangeService{
    Integer SCALE = 2;

    ExchangeResponse calculateExchange(ExchangeRequest request);
}
