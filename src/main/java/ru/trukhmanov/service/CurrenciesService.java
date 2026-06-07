package ru.trukhmanov.service;

import ru.trukhmanov.model.entity.Currency;
import ru.trukhmanov.service.dto.request.CreateCurrencyRequest;
import ru.trukhmanov.service.dto.response.CurrencyResponse;

import java.util.List;

public interface CurrenciesService{
    int CODE_LENGTH = 3;
    int NAME_MIN_LENGTH = 3;
    int NAME_MAX_LENGTH = 20;
    int SIGN_MAX_LENGTH = 3;

    List<CurrencyResponse> getAllCurrencies();

    CurrencyResponse mapToCurrencyDto(Currency currency);

    CurrencyResponse getCurrencyByCode(String code);

    CurrencyResponse getCurrencyById(Integer id);

    CurrencyResponse createCurrency(CreateCurrencyRequest request);

    Currency getGeneralCurrency();
}
