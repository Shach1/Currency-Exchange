package ru.trukhmanov;

import ru.trukhmanov.dao.CurrenciesDao;
import ru.trukhmanov.entity.Currencies;

public class Main{
    public static void main(String[] args){

        System.out.println(CurrenciesDao.getAllCurrencies());

        Currencies currencies = new Currencies(3, "EUR", "Euro", "€");
        System.out.println(CurrenciesDao.updateCurrencies(currencies));

        System.out.println(CurrenciesDao.getAllCurrencies());
    }
}