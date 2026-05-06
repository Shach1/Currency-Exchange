package ru.trukhmanov.service;

import ru.trukhmanov.exception.*;
import ru.trukhmanov.model.dao.ExchangeRatesDao;
import ru.trukhmanov.model.entity.ExchangeRate;
import ru.trukhmanov.service.dto.request.CreateExchangeRateRequest;
import ru.trukhmanov.service.dto.request.UpdateExchangeRateRequest;
import ru.trukhmanov.service.dto.response.ExchangeRateResponse;
import ru.trukhmanov.util.Parser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExchangeRatesService{
    public static final Integer SCALE = 6;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.DOWN;
    private final ExchangeRatesDao ratesDao = new ExchangeRatesDao();
    private final CurrenciesService currenciesService = new CurrenciesService();

    public List<ExchangeRateResponse> getAllExchangeRates(){
        return ratesDao.getAll()
                .stream()
                .map(this::mapToExchangeRateResponse)
                .toList();
    }

    private ExchangeRateResponse mapToExchangeRateResponse(ExchangeRate exchangeRate){
        var currency1 = currenciesService.getCurrencyById(exchangeRate.baseCurrencyId());
        var currency2 = currenciesService.getCurrencyById(exchangeRate.targetCurrencyId());
        return new ExchangeRateResponse(exchangeRate.id(), currency1, currency2, exchangeRate.rate());
    }

    public ExchangeRateResponse getExchangeRate(String codePair){
        return mapToExchangeRateResponse(getExchangeRateByCodePair(codePair));
    }

    private ExchangeRate getExchangeRateByCodePair(String codePair){
        if(codePair.length() != CurrenciesService.CODE_LENGTH * 2) throw new InvalidRequestFormat();
        String baseCurrencyCode = codePair.substring(0, CurrenciesService.CODE_LENGTH);
        String targetCurrencyCode = codePair.substring(CurrenciesService.CODE_LENGTH, CurrenciesService.CODE_LENGTH * 2);

        try{
            var currency1 = currenciesService.getCurrencyByCode(baseCurrencyCode);
            var currency2 = currenciesService.getCurrencyByCode(targetCurrencyCode);
            return getExchangeRateByCurrenciesId(currency1.id(), currency2.id());
        } catch (CurrencyNotFound e){
            throw new ExchangeRateNotFound("Exchange rate not found for the pair");
        }
    }

    protected ExchangeRate getExchangeRateByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId){
        var result = ratesDao.findByCurrenciesId(baseCurrencyId, targetCurrencyId);
        if(result.isEmpty()) throw new ExchangeRateNotFound("Exchange rate not found for the pair");
        return result.get();
    }

    public ExchangeRateResponse createExchangeRate(CreateExchangeRateRequest request){
        ExchangeRate exchangeRate = parseCreateExchangeRateRequest(request);

        if(ratesDao.findByCurrenciesId(exchangeRate.baseCurrencyId(), exchangeRate.targetCurrencyId()).isPresent())
            throw new ExchangeRateAlreadyExist();
        ratesDao.insert(exchangeRate);
        var newExchangeRate = ratesDao.findByCurrenciesId(exchangeRate.baseCurrencyId(), exchangeRate.targetCurrencyId());
        if(newExchangeRate.isEmpty()) throw new UnsuspectedException();
        return mapToExchangeRateResponse(newExchangeRate.get());
    }

    private ExchangeRate parseCreateExchangeRateRequest(CreateExchangeRateRequest request){
        if(request.baseCurrencyCode() == null || request.baseCurrencyCode().isEmpty() ||
                request.targetCurrencyCode() == null || request.targetCurrencyCode().isEmpty() ||
                request.rate() == null || request.rate().isEmpty()){
            throw new MissingFormField();
        }
        var rateDecimal = Parser.parseBigDecimal(request.rate());
        var currency1 = currenciesService.getCurrencyByCode(request.baseCurrencyCode());
        var currency2 = currenciesService.getCurrencyByCode(request.targetCurrencyCode());
        return getValidatedExchangeRate(new ExchangeRate(null, currency1.id(), currency2.id(), rateDecimal));
    }

    private ExchangeRate getValidatedExchangeRate(ExchangeRate er){
        if(er.baseCurrencyId() == null) throw new InvalidValue("Base currency id cannot be null");
        if(er.baseCurrencyId() < 1) throw new InvalidValue("Base currency id cannot be less than 1");

        if(er.targetCurrencyId() == null) throw new InvalidValue("Target currency id cannot be null");
        if(er.targetCurrencyId() < 1) throw new InvalidValue("Target currency id cannot be less than 1");
        if(er.targetCurrencyId().equals(er.baseCurrencyId()))
            throw new InvalidValue("Base and target currency identifiers cannot be equal");

        if(er.rate() == null) throw new InvalidValue("Rate cannot be null");
        if(er.rate().compareTo(BigDecimal.ZERO) < 1) throw new InvalidValue("Exchange rate cannot be less than 0");
        return new ExchangeRate(
                null,
                er.baseCurrencyId(),
                er.targetCurrencyId(),
                er.rate().setScale(SCALE, RoundingMode.DOWN)
        );
    }

    public ExchangeRateResponse updateExchangeRate(UpdateExchangeRateRequest request){
        if(request.rate() == null) throw new InvalidRequestFormat("Rate cannot be null");
        var exchangeRate = getExchangeRateByCodePair(request.codePair());
        ratesDao.updateRate(getValidatedExchangeRate(new ExchangeRate(
                exchangeRate.id(),
                exchangeRate.baseCurrencyId(),
                exchangeRate.targetCurrencyId(),
                Parser.parseBigDecimal(request.rate())
        )));
        return mapToExchangeRateResponse(getExchangeRateByCurrenciesId(exchangeRate.baseCurrencyId(), exchangeRate.targetCurrencyId()));
    }
}
