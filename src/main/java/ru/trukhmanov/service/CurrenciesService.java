package ru.trukhmanov.service;

import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.dto.request.CreateCurrencyRequest;
import ru.trukhmanov.dto.response.CurrencyResponse;

import java.util.List;

public interface CurrenciesService{
    int CODE_LENGTH = 3;
    int NAME_MIN_LENGTH = 3;
    int NAME_MAX_LENGTH = 20;
    int SIGN_MAX_LENGTH = 3;

    List<CurrencyResponse> getAllCurrencies();

    CurrencyResponse mapToCurrencyDto(Currency currency);

    Currency mapToCurrency(CurrencyResponse currencyResponse);

    CurrencyResponse getCurrencyByCode(String code);

    CurrencyResponse createCurrency(CreateCurrencyRequest request);

    Currency getGeneralCurrency();
}
