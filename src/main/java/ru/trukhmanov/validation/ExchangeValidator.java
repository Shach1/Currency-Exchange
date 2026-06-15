package ru.trukhmanov.validation;

import ru.trukhmanov.dto.request.ExchangeRequestDto;
import ru.trukhmanov.exception.ValidationException;

public final class ExchangeValidator{
    public static void validateExchangeRequestDto(ExchangeRequestDto request){
        if (request.baseCurrencyCode() == null || request.baseCurrencyCode().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("baseCurrencyCode"));
        }

        if (request.targetCurrencyCode() == null || request.targetCurrencyCode().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("targetCurrencyCode"));
        }

        if (request.amount() == null || request.amount().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("amount"));
        }

        if (request.baseCurrencyCode().equals(request.targetCurrencyCode())){
            throw new ValidationException("Base and target currencies must be different");
        }
    }
}
