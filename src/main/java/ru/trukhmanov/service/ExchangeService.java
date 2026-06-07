package ru.trukhmanov.service;

import ru.trukhmanov.service.dto.request.ExchangeRequest;
import ru.trukhmanov.service.dto.response.ExchangeResponse;

public interface ExchangeService{
    Integer SCALE = 2;

    ExchangeResponse calculateExchange(ExchangeRequest request);
}
