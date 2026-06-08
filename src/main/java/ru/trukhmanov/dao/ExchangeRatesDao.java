package ru.trukhmanov.dao;

import ru.trukhmanov.entity.ExchangeRate;

import java.util.Optional;

public interface ExchangeRatesDao extends BaseDao<ExchangeRate>{
    Optional<ExchangeRate> findByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId);

    Optional<ExchangeRate> updateRate(ExchangeRate exchangeRate);
}
