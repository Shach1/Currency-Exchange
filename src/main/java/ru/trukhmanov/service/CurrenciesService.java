package ru.trukhmanov.service;

import ru.trukhmanov.exception.*;
import ru.trukhmanov.model.dao.CurrenciesDao;
import ru.trukhmanov.model.entity.Currency;
import ru.trukhmanov.service.dto.CurrencyDto;

import java.util.List;

public class CurrenciesService{
    private final CurrenciesDao currenciesDao = new CurrenciesDao();

    public List<CurrencyDto> getAllCurrencies(){
        System.out.println("getAllCurrencies service");
        return currenciesDao.getAll().stream()
                .map(this::mapToCurrencyDto)
                .toList();
    }

    public CurrencyDto mapToCurrencyDto(Currency currency){
        return new CurrencyDto(
                currency.id(),
                currency.code(),
                currency.fullName(),
                currency.sign());
    }

    public CurrencyDto getCurrencyByCode(String code){
        if(code == null || code.length() != 3) throw new InvalidRequestFormat();
        var result = currenciesDao.findByCode(code);
        if(result.isEmpty()) throw new CurrencyNotFound("Currency with code: %s not found".formatted(code));
        return mapToCurrencyDto(result.get());
    }

    public CurrencyDto getCurrencyById(Integer id ){
        var result = currenciesDao.findById(id);
        if(result.isEmpty()) throw new CurrencyNotFound("Currency not found");
        return mapToCurrencyDto(result.get());
    }

    public CurrencyDto createCurrency(String code, String name, String sigh){
        if(name == null || name.isEmpty() ||
                code == null || code.isEmpty() ||
                sigh == null || sigh.isEmpty()) throw new MissingFormField();

        if(currenciesDao.findByCode(code).isPresent()){
            throw new CurrencyAlreadyExist("A currency with this code already exists");
        }
        currenciesDao.insert(new Currency(null, code, name, sigh));
        var currency = currenciesDao.findByCode(code);
        if(currency.isEmpty()) throw new RuntimeException("Unsuspected problem");
        return mapToCurrencyDto(currency.get());
    }
}
