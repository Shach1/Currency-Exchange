package ru.trukhmanov.service;

import ru.trukhmanov.dao.ExchangeRatesDao;
import ru.trukhmanov.dto.request.CreateExchangeRateRequestDto;
import ru.trukhmanov.dto.request.UpdateExchangeRateRequestDto;
import ru.trukhmanov.entity.ExchangeRate;
import ru.trukhmanov.exception.EntityNotFoundException;
import ru.trukhmanov.exception.ValidationException;
import ru.trukhmanov.util.Parser;
import ru.trukhmanov.validation.ExchangeRateValidator;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRatesServiceImpl implements ExchangeRatesService{
    private final ExchangeRatesDao ratesDao;
    private final CurrenciesService currenciesService;

    public ExchangeRatesServiceImpl(ExchangeRatesDao ratesDao, CurrenciesService currenciesService){
        this.ratesDao = ratesDao;
        this.currenciesService = currenciesService;
    }

    @Override
    public List<ExchangeRate> getAllExchangeRates(){
        return ratesDao.getAll();
    }

    @Override
    public ExchangeRate getExchangeRateByCodePair(String codePair){
        ExchangeRateValidator.validateCodePair(codePair);
        String baseCurrencyCode = codePair.substring(0, CurrenciesService.CODE_LENGTH);
        String targetCurrencyCode = codePair.substring(CurrenciesService.CODE_LENGTH, CurrenciesService.CODE_LENGTH * 2);

        var baseCurrency = currenciesService.getCurrencyByCode(baseCurrencyCode);
        var targetCurrency = currenciesService.getCurrencyByCode(targetCurrencyCode);
        return getExchangeRateByCurrenciesId(baseCurrency.id(), targetCurrency.id());
    }

    @Override
    public ExchangeRate getExchangeRateByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId){
        var result = ratesDao.findByCurrenciesId(baseCurrencyId, targetCurrencyId);
        if (result.isEmpty()){
            throw new EntityNotFoundException("Exchange rate not found for the pair");
        }
        return result.get();
    }

    @Override
    public ExchangeRate createExchangeRate(CreateExchangeRateRequestDto request){
        ExchangeRate newExchangeRate = parseCreateExchangeRateRequest(request);
        newExchangeRate = ratesDao.insert(newExchangeRate).orElseThrow(() -> new RuntimeException("Create failed. Unsuspected problem"));
        return newExchangeRate;
    }

    private ExchangeRate parseCreateExchangeRateRequest(CreateExchangeRateRequestDto request){
        ExchangeRateValidator.validateCreateExchangeRateRequest(request);
        var rate = Parser.parseBigDecimal(request.rate());
        var baseCurrency = currenciesService.getCurrencyByCode(request.baseCurrencyCode());
        var targetCurrency = currenciesService.getCurrencyByCode(request.targetCurrencyCode());
        var newExchangeRate = new ExchangeRate(
                null,
                baseCurrency,
                targetCurrency,
                getNormalizedRate(rate));

        ExchangeRateValidator.validateExchangeRate(newExchangeRate);
        return newExchangeRate;
    }

    private BigDecimal getNormalizedRate(BigDecimal rate){
        return rate.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public ExchangeRate updateExchangeRate(UpdateExchangeRateRequestDto request){
        ExchangeRateValidator.validateUpdateExchangeRateRequestDto(request);
        ExchangeRate exchangeRate = getExchangeRateByCodePair(request.codePair());
        ExchangeRate updated = new ExchangeRate(
                exchangeRate.id(),
                exchangeRate.baseCurrency(),
                exchangeRate.targetCurrency(),
                getNormalizedRate(Parser.parseBigDecimal(request.rate().trim())));
        ExchangeRateValidator.validateRate(updated.rate());
        updated = ratesDao.updateRate(updated).orElseThrow(() -> new RuntimeException("Update failed. Unsuspected error"));
        return updated;
    }
}