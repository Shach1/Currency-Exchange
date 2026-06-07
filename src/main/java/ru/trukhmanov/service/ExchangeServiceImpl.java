package ru.trukhmanov.service;

import ru.trukhmanov.exception.EntityNotFoundException;
import ru.trukhmanov.exception.ValidationException;
import ru.trukhmanov.model.entity.ExchangeRate;
import ru.trukhmanov.service.dto.request.ExchangeRequest;
import ru.trukhmanov.service.dto.response.ExchangeResponse;
import ru.trukhmanov.util.Parser;

import java.math.BigDecimal;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService{
    private final ExchangeRatesService ratesService;
    private final CurrenciesService currenciesService;

    public ExchangeServiceImpl(ExchangeRatesService ratesService, CurrenciesService currenciesService){
        this.ratesService = ratesService;
        this.currenciesService = currenciesService;
    }

    @Override
    public ExchangeResponse calculateExchange(ExchangeRequest request){
        if(request.baseCurrencyCode() == null || request.baseCurrencyCode().isBlank() ||
                request.targetCurrencyCode() == null || request.targetCurrencyCode().isBlank() ||
                request.amount() == null || request.amount().isBlank()) throw new ValidationException("Invalid request format");
        if(request.baseCurrencyCode().equals(request.targetCurrencyCode())) throw new ValidationException("Base and target currencies must be different");
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
                convertedAmount.setScale(SCALE, ExchangeRatesService.ROUNDING_MODE));
    }

    private BigDecimal getRate(Integer baseCurrencyId, Integer targetCurrencyId){
        var rate = getDirectRate(baseCurrencyId, targetCurrencyId);
        if(rate.isPresent()) return rate.get();

        rate = getReversedRate(baseCurrencyId, targetCurrencyId);
        return rate.orElseGet(() -> getRateByGeneralCurrency(baseCurrencyId, targetCurrencyId));
    }

    private Optional<BigDecimal> getDirectRate(Integer baseCurrencyId, Integer targetCurrencyId){
        ExchangeRate exchangeRate = null;
        try{
            exchangeRate = ratesService.getExchangeRateByCurrenciesId(baseCurrencyId, targetCurrencyId);
        } catch (EntityNotFoundException ignore){}
        if(exchangeRate != null) return Optional.of(exchangeRate.rate());
        return Optional.empty();
    }

    private Optional<BigDecimal> getReversedRate(Integer baseCurrencyId, Integer targetCurrencyId){
        ExchangeRate exchangeRate = null;
        try{
            exchangeRate = ratesService.getExchangeRateByCurrenciesId(targetCurrencyId, baseCurrencyId);
        } catch (EntityNotFoundException ignore){}
        if(exchangeRate != null){
            var reverseRate = BigDecimal.ONE.divide(
                    exchangeRate.rate(),
                    ExchangeRatesService.SCALE,
                    ExchangeRatesService.ROUNDING_MODE);
            return Optional.of(reverseRate);
        }
        return Optional.empty();
    }

    private BigDecimal getRateByGeneralCurrency(Integer baseCurrencyId, Integer targetCurrencyId){
        var generalCurrency = currenciesService.getGeneralCurrency();

        Optional<BigDecimal> baseToGeneralRate = getDirectRate(baseCurrencyId, generalCurrency.id());
        if(baseToGeneralRate.isEmpty()) baseToGeneralRate = getReversedRate( baseCurrencyId, generalCurrency.id());
        if(baseToGeneralRate.isEmpty()) throw new EntityNotFoundException("Impossible to calculate the exchange rate for this currency pair");

        Optional<BigDecimal> generalToTargetRate = getDirectRate(generalCurrency.id(), targetCurrencyId);
        if(generalToTargetRate.isEmpty()) generalToTargetRate = getReversedRate(generalCurrency.id(), targetCurrencyId);
        if(generalToTargetRate.isEmpty()) throw new EntityNotFoundException("Impossible to calculate the exchange rate for this currency pair");

        return baseToGeneralRate.get().multiply(generalToTargetRate.get());
    }
}
