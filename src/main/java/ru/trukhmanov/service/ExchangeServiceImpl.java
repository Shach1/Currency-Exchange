package ru.trukhmanov.service;

import ru.trukhmanov.dto.request.ExchangeRequestDto;
import ru.trukhmanov.dto.response.ExchangeDto;
import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.entity.ExchangeRate;
import ru.trukhmanov.exception.EntityNotFoundException;
import ru.trukhmanov.exception.ValidationException;
import ru.trukhmanov.mapper.CurrencyMapper;
import ru.trukhmanov.util.Parser;

import java.math.BigDecimal;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService{
    private final static String GENERAL_CURRENCY_CODE = "USD";
    private Currency cacheGeneralCurrency;
    private final ExchangeRatesService ratesService;
    private final CurrenciesService currenciesService;

    public ExchangeServiceImpl(ExchangeRatesService ratesService, CurrenciesService currenciesService){
        this.ratesService = ratesService;
        this.currenciesService = currenciesService;
    }

    @Override
    public ExchangeDto calculateExchange(ExchangeRequestDto request){
        if (request.baseCurrencyCode() == null || request.baseCurrencyCode().isBlank() ||
                request.targetCurrencyCode() == null || request.targetCurrencyCode().isBlank() ||
                request.amount() == null || request.amount().isBlank()){
            throw new ValidationException("Invalid request format");
        }

        if (request.baseCurrencyCode().equals(request.targetCurrencyCode())){
            throw new ValidationException("Base and target currencies must be different");
        }

        BigDecimal amount = Parser.parseBigDecimal(request.amount());
        Currency baseCurrency = currenciesService.getCurrencyByCode(request.baseCurrencyCode());
        Currency targetCurrency = currenciesService.getCurrencyByCode(request.targetCurrencyCode());
        BigDecimal rate = getRate(baseCurrency.id(), targetCurrency.id());
        BigDecimal convertedAmount = rate.multiply(amount);

        return new ExchangeDto(
                CurrencyMapper.INSTANCE.toCurrencyDto(baseCurrency),
                CurrencyMapper.INSTANCE.toCurrencyDto(targetCurrency),
                rate,
                amount,
                convertedAmount.setScale(SCALE, ExchangeRatesService.ROUNDING_MODE));
    }

    private BigDecimal getRate(Integer baseCurrencyId, Integer targetCurrencyId){
        var rate = getDirectRate(baseCurrencyId, targetCurrencyId);
        if (rate.isPresent()){
            return rate.get();
        }

        rate = getReversedRate(baseCurrencyId, targetCurrencyId);
        return rate.orElseGet(() -> getRateByGeneralCurrency(baseCurrencyId, targetCurrencyId));
    }

    private Optional<BigDecimal> getDirectRate(Integer baseCurrencyId, Integer targetCurrencyId){
        ExchangeRate exchangeRate = null;
        try{
            exchangeRate = ratesService.getExchangeRateByCurrenciesId(baseCurrencyId, targetCurrencyId);
        } catch (EntityNotFoundException ignore){}

        if (exchangeRate != null){
            return Optional.of(exchangeRate.rate());
        }
        return Optional.empty();
    }

    private Optional<BigDecimal> getReversedRate(Integer baseCurrencyId, Integer targetCurrencyId){
        ExchangeRate exchangeRate = null;
        try{
            exchangeRate = ratesService.getExchangeRateByCurrenciesId(targetCurrencyId, baseCurrencyId);
        } catch (EntityNotFoundException ignore){
        }
        if (exchangeRate != null){
            var reverseRate = BigDecimal.ONE.divide(
                    exchangeRate.rate(),
                    ExchangeRatesService.SCALE,
                    ExchangeRatesService.ROUNDING_MODE);
            return Optional.of(reverseRate);
        }
        return Optional.empty();
    }

    private BigDecimal getRateByGeneralCurrency(Integer baseCurrencyId, Integer targetCurrencyId){
        if (cacheGeneralCurrency == null){
            cacheGeneralCurrency = getGeneralCurrency();
        }

        Optional<BigDecimal> baseToGeneralRate = getDirectRate(baseCurrencyId, cacheGeneralCurrency.id());
        if (baseToGeneralRate.isEmpty()){
            baseToGeneralRate = getReversedRate(baseCurrencyId, cacheGeneralCurrency.id());
        }
        if (baseToGeneralRate.isEmpty()){
            throw new EntityNotFoundException("Impossible to calculate the exchange rate for this currency pair");
        }

        Optional<BigDecimal> generalToTargetRate = getDirectRate(cacheGeneralCurrency.id(), targetCurrencyId);
        if (generalToTargetRate.isEmpty()){
            generalToTargetRate = getReversedRate(cacheGeneralCurrency.id(), targetCurrencyId);
        }
        if (generalToTargetRate.isEmpty()){
            throw new EntityNotFoundException("Impossible to calculate the exchange rate for this currency pair");
        }

        return baseToGeneralRate.get().multiply(generalToTargetRate.get());
    }

    private Currency getGeneralCurrency(){
        try{
            return currenciesService.getCurrencyByCode(GENERAL_CURRENCY_CODE);
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("Error receiving general currency: " + e.getMessage());
        }
    }
}
