package ru.trukhmanov;


import ru.trukhmanov.dao.CurrenciesDao;
import ru.trukhmanov.dao.ExchangeRatesDao;
import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.entity.ExchangeRate;

import java.math.BigDecimal;

public class Main{
    public static void main(String[] args){
        CurrenciesDao currenciesDao = new CurrenciesDao();
        ExchangeRatesDao ratesDao = new ExchangeRatesDao();


        System.out.println(ratesDao.findExchangeRateById(2));
        ExchangeRate newRate = new ExchangeRate(
                2,
                4,
                2,
                new BigDecimal("75.06884603"));
        ratesDao.updateExchangeRate(newRate);
        System.out.println(ratesDao.findExchangeRateById(2));

        System.out.println(currenciesDao.findCurrencyById(2));
        var newCurrency = new Currency(
                4,
                "RUB",
                "Russian ruble",
                "₽"
        );
        currenciesDao.updateCurrency(newCurrency);
        System.out.println(currenciesDao.findCurrencyById(2));

    }
}