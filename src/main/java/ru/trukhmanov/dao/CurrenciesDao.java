package ru.trukhmanov.dao;

import ru.trukhmanov.entity.Currency;

import java.util.Optional;

public interface CurrenciesDao extends BaseDao<Currency>{
    Optional<Currency> findByCode(String code);
}
