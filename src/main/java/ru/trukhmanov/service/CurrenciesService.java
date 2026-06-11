package ru.trukhmanov.service;

import ru.trukhmanov.dto.request.CreateCurrencyRequestDto;
import ru.trukhmanov.dto.response.CurrencyDto;

import java.util.List;

public interface CurrenciesService{
    int CODE_LENGTH = 3;
    int NAME_MIN_LENGTH = 3;
    int NAME_MAX_LENGTH = 20;
    int SIGN_MAX_LENGTH = 3;

    List<CurrencyDto> getAllCurrencies();

    CurrencyDto getCurrencyByCode(String code);

    CurrencyDto createCurrency(CreateCurrencyRequestDto request);
}
