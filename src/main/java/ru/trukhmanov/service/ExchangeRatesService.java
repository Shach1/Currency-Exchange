package ru.trukhmanov.service;

import ru.trukhmanov.exception.*;
import ru.trukhmanov.model.dao.ExchangeRatesDao;
import ru.trukhmanov.model.entity.ExchangeRate;
import ru.trukhmanov.service.dto.ExchangeRateDto;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRatesService{
    private final ExchangeRatesDao ratesDao = new ExchangeRatesDao();
    private final CurrenciesService currenciesService = new CurrenciesService();

    public List<ExchangeRateDto> getAllExchangeRates(){
        return ratesDao.getAll()
                .stream()
                .map(this::mapToExchangeRateDto)
                .toList();
    }

    public ExchangeRateDto mapToExchangeRateDto(ExchangeRate exchangeRate){
        try{
            var currency1 = currenciesService.getCurrencyById(exchangeRate.baseCurrencyId());
            var currency2 = currenciesService.getCurrencyById(exchangeRate.targetCurrencyId());
            return new ExchangeRateDto(exchangeRate.id(), currency1, currency2, exchangeRate.rate());
        } catch (CurrencyNotFound e){
            throw new RuntimeException(e);
        }
    }

    public ExchangeRateDto getExchangeRateByCodePair(String codePair) throws InvalidRequestFormat, ExchangeRateNotFound{
        if(codePair.length() != 6) throw new InvalidRequestFormat();
        String baseCurrencyCode = codePair.substring(0, 3);
        String targetCurrencyCode = codePair.substring(3, 6);

        try{
            var currency1 = currenciesService.getCurrencyByCode(baseCurrencyCode);
            var currency2 = currenciesService.getCurrencyByCode(targetCurrencyCode);
            return getExchangeRateByCurrenciesId(currency1.id(), currency2.id());
        } catch (CurrencyNotFound e){
            throw new ExchangeRateNotFound("Exchange rate not found for the pair");
        }
    }

    public ExchangeRateDto getExchangeRateByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId) throws ExchangeRateNotFound{
        var result = ratesDao.findByCurrenciesId(baseCurrencyId, targetCurrencyId);
        if(result.isEmpty()) throw new ExchangeRateNotFound("Exchange rate not found for the pair");
        return mapToExchangeRateDto(result.get());
    }

    public ExchangeRateDto createExchangeRateByCodePair(String baseCurrencyCode,
                                                        String targetCurrencyCode,
                                                        String rate) throws MissingFormField, InvalidRequestFormat, CurrencyNotFound, ExchangeRateAlreadyExist{
        if(baseCurrencyCode == null || baseCurrencyCode.isEmpty() ||
                targetCurrencyCode == null || targetCurrencyCode.isEmpty() ||
                rate == null || rate.isEmpty()){throw new MissingFormField();}

        //TODO: как-то валидировать поле, чтобы не было ошибки при создании BigDecimal
        var rateDecimal = new BigDecimal(rate);

        var currency1 = currenciesService.getCurrencyByCode(baseCurrencyCode);
        var currency2 = currenciesService.getCurrencyByCode(targetCurrencyCode);

        if(ratesDao.findByCurrenciesId(currency1.id(), currency2.id()).isPresent()) throw new ExchangeRateAlreadyExist();

        ratesDao.insert(new ExchangeRate(null, currency1.id(), currency2.id(), rateDecimal));
        var exchangeRate = ratesDao.findByCurrenciesId(currency1.id(), currency2.id());
        if(exchangeRate.isEmpty()) throw new RuntimeException("Unsuspected problem");
        return mapToExchangeRateDto(exchangeRate.get());
    }
}
