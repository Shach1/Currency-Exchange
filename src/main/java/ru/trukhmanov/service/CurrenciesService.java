package ru.trukhmanov.service;

import ru.trukhmanov.exception.RowAlreadyExist;
import ru.trukhmanov.model.dao.CurrenciesDao;
import ru.trukhmanov.model.entity.Currency;

import java.util.List;
import java.util.NoSuchElementException;

public class CurrenciesService{
    private final CurrenciesDao currenciesDao = new CurrenciesDao();

    public List<Currency> getAllCurrencies(){
        return currenciesDao.getAll();
    }

    public Currency getCurrencyByCode(String code) throws NoSuchElementException{
        var result = currenciesDao.findByCode(code);
        if(result.isEmpty()) throw new NoSuchElementException("Currency not found");
        return result.get();
    }

    public Currency createCurrency(String code, String name, String sigh) throws NoSuchFieldException, RowAlreadyExist{
        if(name == null || name.isEmpty() ||
                code == null || code.isEmpty() ||
                sigh == null || sigh.isEmpty()) throw new NoSuchFieldException("A required form field is missing");

        if(currenciesDao.findByCode(code).isPresent()){
            throw new RowAlreadyExist("A currency with this code already exists");
        }
        currenciesDao.insert(new Currency(null, code, name, sigh));
        var currency = currenciesDao.findByCode(code);
        if(currency.isEmpty()) throw new RuntimeException("Unsuspected problem");
        return currency.get();
    }
}
