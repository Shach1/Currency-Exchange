package ru.trukhmanov.service;

import ru.trukhmanov.dao.CurrenciesDao;
import ru.trukhmanov.dto.request.CreateCurrencyRequestDto;
import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.exception.EntityNotFoundException;
import ru.trukhmanov.validation.CurrencyValidator;

import java.util.List;

public class CurrenciesServiceImpl implements CurrenciesService{
    private final CurrenciesDao currenciesDao;

    public CurrenciesServiceImpl(CurrenciesDao currenciesDao){
        this.currenciesDao = currenciesDao;
    }

    @Override
    public List<Currency> getAllCurrencies(){
        return currenciesDao.getAll();
    }

    @Override
    public Currency getCurrencyByCode(String code){
        CurrencyValidator.validateCode(code);
        var result = currenciesDao.findByCode(code.toUpperCase());
        if (result.isEmpty()){
            throw new EntityNotFoundException("Currency with code: %s not found".formatted(code));
        }
        return result.get();
    }

    @Override
    public Currency createCurrency(CreateCurrencyRequestDto request){
        CurrencyValidator.validateCreateCurrencyRequestDto(request);
        Currency newCurrency = new Currency(
                null,
                request.code().trim().toUpperCase(),
                request.name().trim(),
                request.sign().trim());
        CurrencyValidator.validateCurrency(newCurrency);
        newCurrency = currenciesDao.insert(newCurrency).orElseThrow(() -> new RuntimeException("Unsuspected problem"));
        return newCurrency;
    }
}