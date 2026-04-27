package ru.trukhmanov.service;

import ru.trukhmanov.exception.ExchangeRateCannotBeCalculated;
import ru.trukhmanov.exception.ExchangeRateNotFound;
import ru.trukhmanov.exception.InvalidRequestFormat;
import ru.trukhmanov.model.entity.ExchangeRate;
import ru.trukhmanov.service.dto.request.ExchangeRequest;
import ru.trukhmanov.service.dto.response.ExchangeResponse;
import ru.trukhmanov.util.Parser;

import java.math.BigDecimal;
import java.util.Optional;

public class ExchangeService{
    private final ExchangeRatesService ratesService = new ExchangeRatesService();
    private final CurrenciesService currenciesService = new CurrenciesService();


    public ExchangeResponse calculateExchange(ExchangeRequest request){
        if(request.baseCurrencyCode() == null || request.baseCurrencyCode().isEmpty() ||
                request.targetCurrencyCode() == null || request.targetCurrencyCode().isEmpty() ||
                request.amount() == null || request.amount().isEmpty()) throw new InvalidRequestFormat();
        var amount = Parser.parseBigDecimal(request.amount());
        var baseCurrency = currenciesService.getCurrencyByCode(request.baseCurrencyCode());
        var targetCurrency = currenciesService.getCurrencyByCode(request.targetCurrencyCode());
        var rate = getRate(baseCurrency.id(), targetCurrency.id());
        var convertedAmount = rate.multiply(amount);

        return new ExchangeResponse(
                baseCurrency,
                targetCurrency,
                rate,
                amount,
                convertedAmount);
    }

    private BigDecimal getRate(Integer baseCurrencyId, Integer targetCurrencyId){
        var rate = getDirectRate(baseCurrencyId, targetCurrencyId);
        if(rate.isPresent()) return rate.get();

        rate = getReversedRate(baseCurrencyId, targetCurrencyId);
        if(rate.isPresent()) return rate.get();

        rate = getRateByUsd(baseCurrencyId, targetCurrencyId);
        if(rate.isPresent()) return rate.get();

        throw new ExchangeRateCannotBeCalculated();
    }

    private Optional<BigDecimal> getDirectRate(Integer baseCurrencyId, Integer targetCurrencyId){
        ExchangeRate exchangeRate = null;
        try{
            exchangeRate = ratesService.getExchangeRateByCurrenciesId(baseCurrencyId, targetCurrencyId);
        } catch (ExchangeRateNotFound ignore){}
        if(exchangeRate != null) return Optional.of(exchangeRate.rate());
        return Optional.empty();
    }

    private Optional<BigDecimal> getReversedRate(Integer baseCurrencyId, Integer targetCurrencyId){
        ExchangeRate exchangeRate = null;
        try{
            exchangeRate = ratesService.getExchangeRateByCurrenciesId(targetCurrencyId, baseCurrencyId);
        } catch (ExchangeRateNotFound ignore){}
        if(exchangeRate != null){
            var reverseRate = BigDecimal.ONE.divide(
                    exchangeRate.rate(),
                    ExchangeRatesService.SCALE,
                    ExchangeRatesService.ROUNDING_MODE);
            return Optional.of(reverseRate);
        }
        return Optional.empty();
    }

    private Optional<BigDecimal> getRateByUsd(Integer baseCurrencyId, Integer targetCurrencyId){
        //todo: добавить возможность конвертации через USD (USD-A USD-B)
        return Optional.empty();
    }
}
