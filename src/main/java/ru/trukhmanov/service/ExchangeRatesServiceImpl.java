package ru.trukhmanov.service;

import ru.trukhmanov.dao.ExchangeRatesDao;
import ru.trukhmanov.entity.ExchangeRate;
import ru.trukhmanov.exception.EntityNotFoundException;
import ru.trukhmanov.exception.ValidationException;
import ru.trukhmanov.dto.request.CreateExchangeRateRequest;
import ru.trukhmanov.dto.request.UpdateExchangeRateRequest;
import ru.trukhmanov.dto.response.ExchangeRateResponse;
import ru.trukhmanov.util.Parser;

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
    public List<ExchangeRateResponse> getAllExchangeRates(){
        return ratesDao.getAll()
                .stream()
                .map(this::mapToExchangeRateResponse)
                .toList();
    }

    @Override
    public ExchangeRateResponse mapToExchangeRateResponse(ExchangeRate exchangeRate){
        return new ExchangeRateResponse(
                exchangeRate.id(),
                currenciesService.mapToCurrencyDto(exchangeRate.baseCurrency()),
                currenciesService.mapToCurrencyDto(exchangeRate.targetCurrency()),
                exchangeRate.rate());
    }

    @Override
    public ExchangeRateResponse getExchangeRate(String codePair){
        return mapToExchangeRateResponse(getExchangeRateByCodePair(codePair));
    }

    private ExchangeRate getExchangeRateByCodePair(String codePair){
        if(codePair.length() != CurrenciesService.CODE_LENGTH * 2)
            throw new ValidationException("Invalid request format");
        String baseCurrencyCode = codePair.substring(0, CurrenciesService.CODE_LENGTH);
        String targetCurrencyCode = codePair.substring(CurrenciesService.CODE_LENGTH, CurrenciesService.CODE_LENGTH * 2);

        var currency1 = currenciesService.getCurrencyByCode(baseCurrencyCode);
        var currency2 = currenciesService.getCurrencyByCode(targetCurrencyCode);
        return getExchangeRateByCurrenciesId(currency1.id(), currency2.id());
    }

    public ExchangeRate getExchangeRateByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId){
        var result = ratesDao.findByCurrenciesId(baseCurrencyId, targetCurrencyId);
        if(result.isEmpty()) throw new EntityNotFoundException("Exchange rate not found for the pair");
        return result.get();
    }

    @Override
    public ExchangeRateResponse createExchangeRate(CreateExchangeRateRequest request){
        ExchangeRate exchangeRate = parseCreateExchangeRateRequest(request);

        ExchangeRate created = ratesDao.insert(exchangeRate).orElseThrow(() -> new RuntimeException("Unsuspected problem"));
        return mapToExchangeRateResponse(created);
    }

    private ExchangeRate parseCreateExchangeRateRequest(CreateExchangeRateRequest request){
        if(request.baseCurrencyCode() == null || request.baseCurrencyCode().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("baseCurrencyCode"));
        }
        if(request.targetCurrencyCode() == null || request.targetCurrencyCode().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("targetCurrencyCode"));
        }
        if(request.rate() == null || request.rate().isBlank()){
            throw new ValidationException("%s form field is missing".formatted("rate"));
        }
        var rateDecimal = Parser.parseBigDecimal(request.rate());
        var currency1 = currenciesService.getCurrencyByCode(request.baseCurrencyCode());
        var currency2 = currenciesService.getCurrencyByCode(request.targetCurrencyCode());
        return getValidatedExchangeRate(new ExchangeRate(
                null,
                currenciesService.mapToCurrency(currency1),
                currenciesService.mapToCurrency(currency2),
                rateDecimal));
    }

    private ExchangeRate getValidatedExchangeRate(ExchangeRate er){
        if(er.baseCurrency().id() == null) throw new ValidationException("Base currency id cannot be null");
        if(er.baseCurrency().id() < 1) throw new ValidationException("Base currency id cannot be less than 1");

        if(er.targetCurrency().id() == null) throw new ValidationException("Target currency id cannot be null");
        if(er.targetCurrency().id() < 1) throw new ValidationException("Target currency id cannot be less than 1");
        if(er.targetCurrency().id().equals(er.baseCurrency().id()))
            throw new ValidationException("Base and target currency identifiers cannot be equal");

        if(er.rate() == null) throw new ValidationException("Rate cannot be null");
        if(er.rate().compareTo(BigDecimal.ZERO) < 1)
            throw new ValidationException("Exchange rate cannot be less than 0");
        return new ExchangeRate(
                null,
                er.baseCurrency(),
                er.targetCurrency(),
                er.rate().setScale(SCALE, ROUNDING_MODE)
        );
    }

    @Override
    public ExchangeRateResponse updateExchangeRate(UpdateExchangeRateRequest request){
        if(request.rate() == null) throw new ValidationException("Rate cannot be null");
        ExchangeRate exchangeRate = getExchangeRateByCodePair(request.codePair());
        ExchangeRate updated = ratesDao.updateRate(getValidatedExchangeRate(new ExchangeRate(
                exchangeRate.id(),
                exchangeRate.baseCurrency(),
                exchangeRate.targetCurrency(),
                Parser.parseBigDecimal(request.rate())
        ))).orElseThrow(() -> new EntityNotFoundException("Exchange rate not found for the pair"));
        return mapToExchangeRateResponse(updated);
    }
}