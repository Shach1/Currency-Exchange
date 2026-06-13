package ru.trukhmanov.service;

import ru.trukhmanov.dto.request.CreateExchangeRateRequestDto;
import ru.trukhmanov.dto.request.UpdateExchangeRateRequestDto;
import ru.trukhmanov.entity.ExchangeRate;

import java.math.RoundingMode;
import java.util.List;

public interface ExchangeRatesService{
    Integer SCALE = 6;
    RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    List<ExchangeRate> getAllExchangeRates();

    ExchangeRate getExchangeRateByCodePair(String codePair);

    ExchangeRate getExchangeRateByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId);

    ExchangeRate createExchangeRate(CreateExchangeRateRequestDto request);

    ExchangeRate updateExchangeRate(UpdateExchangeRateRequestDto request);
}
