package ru.trukhmanov.service;

import ru.trukhmanov.model.dao.CurrenciesDao;
import ru.trukhmanov.model.dao.ExchangeRatesDao;
import ru.trukhmanov.model.entity.ExchangeRate;
import ru.trukhmanov.service.dto.ExchangeRateDto;

import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.NoSuchElementException;

public class ExchangeRatesService{
    private final ExchangeRatesDao ratesDao = new ExchangeRatesDao();
    private final CurrenciesDao currenciesDao = new CurrenciesDao();
    private final CurrenciesService currenciesService = new CurrenciesService();

    public List<ExchangeRateDto> getAllExchangeRates(){
        return ratesDao.getAll()
                .stream()
                .map(this::mapToExchangeRateDto)
                .toList();
    }

    public ExchangeRateDto mapToExchangeRateDto(ExchangeRate exchangeRate){
        var currency1 = currenciesDao.findById(exchangeRate.baseCurrencyId());
        var currency2 = currenciesDao.findById(exchangeRate.targetCurrencyId());
        return new ExchangeRateDto(exchangeRate.id(), currency1.get(), currency2.get(), exchangeRate.rate());
    }

    public ExchangeRateDto getExchangeRateByCodePaid(String codePair) throws InvalidPropertiesFormatException, NoSuchElementException{
        if(codePair.length() != 6) throw new InvalidPropertiesFormatException("Invalid request format");
        String baseCurrencyCode = codePair.substring(0, 3);
        String targetCurrencyCode = codePair.substring(3, 6);

        var currency1 = currenciesService.getCurrencyByCode(baseCurrencyCode);
        var currency2 = currenciesService.getCurrencyByCode(targetCurrencyCode);
        return getExchangeRateByCurrenciesId(currency1.id(), currency2.id());
    }
    public ExchangeRateDto getExchangeRateByCurrenciesId(Integer baseCurrencyCode, Integer targetCurrencyCode) throws NoSuchElementException{
        var result = ratesDao.findByCurrenciesId(baseCurrencyCode, targetCurrencyCode);
        if(result.isEmpty()) throw new NoSuchElementException("Exchange rate not found for the pair");
        return mapToExchangeRateDto(result.get());
    }




}
