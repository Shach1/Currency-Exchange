package ru.trukhmanov.service;

import ru.trukhmanov.dao.ExchangeRatesDao;
import ru.trukhmanov.dto.request.ExchangeRequestDto;
import ru.trukhmanov.dto.response.ExchangeDto;
import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.entity.ExchangeRate;
import ru.trukhmanov.exception.EntityNotFoundException;
import ru.trukhmanov.mapper.CurrencyMapper;
import ru.trukhmanov.util.Parser;
import ru.trukhmanov.validation.ExchangeValidator;

import java.math.BigDecimal;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService{
    private final static String GENERAL_CURRENCY_CODE = "USD";
    private Currency cacheGeneralCurrency;
    private final ExchangeRatesDao ratesDao;
    private final CurrenciesService currenciesService;

    public ExchangeServiceImpl(ExchangeRatesDao ratesDao, CurrenciesService currenciesService){
        this.ratesDao = ratesDao;
        this.currenciesService = currenciesService;
    }

    @Override
    public ExchangeDto calculateExchange(ExchangeRequestDto request){
        ExchangeValidator.validateExchangeRequestDto(request);
        BigDecimal amount = Parser.parseBigDecimal(request.amount());
        Currency baseCurrency = currenciesService.getCurrencyByCode(request.baseCurrencyCode());
        Currency targetCurrency = currenciesService.getCurrencyByCode(request.targetCurrencyCode());
        BigDecimal rate = findRate(baseCurrency.id(), targetCurrency.id());
        BigDecimal convertedAmount = rate.multiply(amount);

        return new ExchangeDto(
                CurrencyMapper.INSTANCE.toCurrencyDto(baseCurrency),
                CurrencyMapper.INSTANCE.toCurrencyDto(targetCurrency),
                rate,
                amount,
                convertedAmount.setScale(AMOUNT_SCALE, ROUNDING_MODE));
    }

    private BigDecimal findRate(Integer baseCurrencyId, Integer targetCurrencyId){
        return getDirectRate(baseCurrencyId, targetCurrencyId)
                .or(() -> getReversedRate(baseCurrencyId, targetCurrencyId))
                .or(() -> getRateByGeneralCurrency(baseCurrencyId, targetCurrencyId))
                .orElseThrow(() -> new EntityNotFoundException("Impossible to calculate the exchange rate for this currency pair"));
    }

    private Optional<BigDecimal> getDirectRate(Integer baseCurrencyId, Integer targetCurrencyId){
        return ratesDao.findByCurrenciesId(baseCurrencyId, targetCurrencyId)
                .map(ExchangeRate::rate);
    }

    private Optional<BigDecimal> getReversedRate(Integer baseCurrencyId, Integer targetCurrencyId){
        return ratesDao.findByCurrenciesId(targetCurrencyId, baseCurrencyId)
                .map(exchangeRate -> BigDecimal.ONE.divide(
                        exchangeRate.rate(),
                        RATE_SCALE,
                        ROUNDING_MODE));
    }

    private Optional<BigDecimal> getRateByGeneralCurrency(Integer baseCurrencyId, Integer targetCurrencyId){
        if (cacheGeneralCurrency == null){
            cacheGeneralCurrency = getGeneralCurrency();
        }

        Optional<BigDecimal> baseToGeneralRate = getDirectRate(baseCurrencyId, cacheGeneralCurrency.id());
        if (baseToGeneralRate.isEmpty()){
            baseToGeneralRate = getReversedRate(baseCurrencyId, cacheGeneralCurrency.id());
        }

        Optional<BigDecimal> generalToTargetRate = getDirectRate(cacheGeneralCurrency.id(), targetCurrencyId);
        if (generalToTargetRate.isEmpty()){
            generalToTargetRate = getReversedRate(cacheGeneralCurrency.id(), targetCurrencyId);
        }

        if (baseToGeneralRate.isEmpty() || generalToTargetRate.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(baseToGeneralRate.get().multiply(generalToTargetRate.get()));
    }

    private Currency getGeneralCurrency(){
        try{
            return currenciesService.getCurrencyByCode(GENERAL_CURRENCY_CODE);
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("Error receiving general currency: " + e.getMessage());
        }
    }
}
