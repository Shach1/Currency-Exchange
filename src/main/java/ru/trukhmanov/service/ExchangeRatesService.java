package ru.trukhmanov.service;

import ru.trukhmanov.model.entity.ExchangeRate;
import ru.trukhmanov.service.dto.request.CreateExchangeRateRequest;
import ru.trukhmanov.service.dto.request.UpdateExchangeRateRequest;
import ru.trukhmanov.service.dto.response.ExchangeRateResponse;

import java.math.RoundingMode;
import java.util.List;

public interface ExchangeRatesService{
    Integer SCALE = 6;
    RoundingMode ROUNDING_MODE = RoundingMode.DOWN;

    List<ExchangeRateResponse> getAllExchangeRates();

    ExchangeRateResponse getExchangeRate(String codePair);

    ExchangeRateResponse createExchangeRate(CreateExchangeRateRequest request);

    ExchangeRateResponse updateExchangeRate(UpdateExchangeRateRequest request);

    ExchangeRate getExchangeRateByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId);
}
