package ru.trukhmanov.validation;

import ru.trukhmanov.dto.request.CreateExchangeRateRequestDto;
import ru.trukhmanov.dto.request.UpdateExchangeRateRequestDto;
import ru.trukhmanov.entity.ExchangeRate;
import ru.trukhmanov.exception.ValidationException;
import ru.trukhmanov.service.CurrenciesService;

import java.math.BigDecimal;

public final class ExchangeRateValidator{
    public static void validateExchangeRate(ExchangeRate er){
        if (er.targetCurrency().id().equals(er.baseCurrency().id())){
            throw new ValidationException("Base and target currency identifiers cannot be equal");
        }
        validateRate(er.rate());
    }

    public static void validateRate(BigDecimal rate){
        if (rate == null){
            throw new ValidationException("Rate cannot be null");
        }
        if (rate.compareTo(BigDecimal.ZERO) < 1){
            throw new ValidationException("Exchange rate cannot be less than 0");
        }
    }

    public static void validateCreateExchangeRateRequest(CreateExchangeRateRequestDto request){
        if (request.baseCurrencyCode() == null || request.baseCurrencyCode().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("baseCurrencyCode"));
        }
        if (request.targetCurrencyCode() == null || request.targetCurrencyCode().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("targetCurrencyCode"));
        }
        if (request.rate() == null || request.rate().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("rate"));
        }
    }

    public static void validateUpdateExchangeRateRequestDto(UpdateExchangeRateRequestDto request){
        validateCodePair(request.codePair());
        if (request.rate() == null || request.rate().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("rate"));
        }
    }

    public static void validateCodePair(String codePair){
        if (codePair.length() != CurrenciesService.CODE_LENGTH * 2){
            throw new ValidationException("Invalid request format");
        }
    }
}
