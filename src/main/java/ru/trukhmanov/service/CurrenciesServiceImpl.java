package ru.trukhmanov.service;

import ru.trukhmanov.dao.CurrenciesDao;
import ru.trukhmanov.dto.request.CreateCurrencyRequestDto;
import ru.trukhmanov.dto.response.CurrencyDto;
import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.exception.EntityNotFoundException;
import ru.trukhmanov.mapper.CurrencyMapper;
import ru.trukhmanov.validation.CurrencyValidator;

import java.util.List;

public class CurrenciesServiceImpl implements CurrenciesService{
    private final CurrenciesDao currenciesDao;

    public CurrenciesServiceImpl(CurrenciesDao currenciesDao){
        this.currenciesDao = currenciesDao;
    }

    @Override
    public List<CurrencyDto> getAllCurrencies(){
        return currenciesDao.getAll().stream()
                .map(CurrencyMapper.INSTANCE::CurrencyToCurrencyDto)
                .toList();
    }

    @Override
    public CurrencyDto getCurrencyByCode(String code){
        CurrencyValidator.validateCode(code);
        var result = currenciesDao.findByCode(code);
        if(result.isEmpty()){
            throw new EntityNotFoundException("Currency with code: %s not found".formatted(code));
        }
        return CurrencyMapper.INSTANCE.CurrencyToCurrencyDto(result.get());
    }

    @Override
    public CurrencyDto createCurrency(CreateCurrencyRequestDto request){
        Currency newCurrency = new Currency(null, request.code(), request.name(), request.sign());
        CurrencyValidator.validateCurrency(newCurrency);
        newCurrency = currenciesDao.insert(newCurrency).orElseThrow(() -> new RuntimeException("Unsuspected problem"));
        return CurrencyMapper.INSTANCE.CurrencyToCurrencyDto(newCurrency);
    }
}
