package ru.trukhmanov.service;

import ru.trukhmanov.entity.ExchangeRate;
import ru.trukhmanov.dto.request.CreateExchangeRateRequestDto;
import ru.trukhmanov.dto.request.UpdateExchangeRateRequestDto;
import ru.trukhmanov.dto.response.ExchangeRateDto;

import java.math.RoundingMode;
import java.util.List;

public interface ExchangeRatesService{
    Integer SCALE = 6;
    RoundingMode ROUNDING_MODE = RoundingMode.DOWN;

    List<ExchangeRateDto> getAllExchangeRates();

    ExchangeRateDto mapToExchangeRateResponse(ExchangeRate exchangeRate);

    ExchangeRateDto getExchangeRate(String codePair);

    ExchangeRateDto createExchangeRate(CreateExchangeRateRequestDto request);

    ExchangeRateDto updateExchangeRate(UpdateExchangeRateRequestDto request);

    ExchangeRate getExchangeRateByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId);
}
