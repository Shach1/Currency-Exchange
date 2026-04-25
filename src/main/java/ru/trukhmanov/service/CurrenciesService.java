package ru.trukhmanov.service;

import ru.trukhmanov.model.dao.CurrenciesDao;
import ru.trukhmanov.model.entity.Currency;
import ru.trukhmanov.exception.RowAlreadyExist;

import java.util.List;
import java.util.Optional;

public class CurrenciesService{
    private final CurrenciesDao currenciesDao = new CurrenciesDao();

    public List<Currency> getAllCurrencies(){
        return currenciesDao.getAll();
    }

    public Optional<Currency> getCurrencyBuCode(String code){
        return currenciesDao.findByCode(code);
    }

    public Optional<Currency> createCurrency(String code, String name, String sigh) throws NoSuchFieldException, RowAlreadyExist{
        if(name == null || name.isEmpty() ||
                code == null || code.isEmpty() ||
                sigh == null || sigh.isEmpty()) throw new NoSuchFieldException("A required form field is missing");

        if(currenciesDao.findByCode(code).isPresent())
            throw new RowAlreadyExist("A currency with this code already exists");
        currenciesDao.insert(new Currency(null, code, name, sigh));
        return currenciesDao.findByCode(code);
    }
}
