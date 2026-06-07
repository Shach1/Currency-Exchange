package ru.trukhmanov.service;

import ru.trukhmanov.exception.*;
import ru.trukhmanov.model.dao.CurrenciesDao;
import ru.trukhmanov.model.entity.Currency;
import ru.trukhmanov.service.dto.request.CreateCurrencyRequest;
import ru.trukhmanov.service.dto.response.CurrencyResponse;
import ru.trukhmanov.util.Patterns;

import java.util.List;

public class CurrenciesServiceImpl implements CurrenciesService{
    private final CurrenciesDao currenciesDao;
    private final Currency cacheGeneralCurrency;

    public CurrenciesServiceImpl(CurrenciesDao currenciesDao){
        this.currenciesDao = currenciesDao;
        String generalCurrencyCode = "USD";
        var currency = currenciesDao.findByCode(generalCurrencyCode);
        if(currency.isEmpty()) throw new RuntimeException("Unable to obtain general currency with code: %s".formatted(generalCurrencyCode));
        cacheGeneralCurrency = currency.get();
    }

    @Override
    public List<CurrencyResponse> getAllCurrencies(){
        return currenciesDao.getAll().stream()
                .map(this::mapToCurrencyDto)
                .toList();
    }

    @Override
    public CurrencyResponse mapToCurrencyDto(Currency currency){
        return new CurrencyResponse(
                currency.id(),
                currency.code(),
                currency.fullName(),
                currency.sign());
    }

    @Override
    public CurrencyResponse getCurrencyByCode(String code){
        if(code == null || code.length() != CODE_LENGTH) throw new ValidationException("Invalid request format");
        var result = currenciesDao.findByCode(code);
        if(result.isEmpty()) throw new EntityNotFoundException("Currency with code: %s not found".formatted(code));
        return mapToCurrencyDto(result.get());
    }

    @Override
    public CurrencyResponse getCurrencyById(Integer id){
        var result = currenciesDao.findById(id);
        if(result.isEmpty()) throw new EntityNotFoundException("Currency with id: %d not found".formatted(id));
        return mapToCurrencyDto(result.get());
    }

    @Override
    public CurrencyResponse createCurrency(CreateCurrencyRequest request){
        var currency = parseCreateCurrencyRequest(request);
        currenciesDao.insert(currency);
        var newCurrency = currenciesDao.findByCode(currency.code());
        if(newCurrency.isEmpty()) throw new RuntimeException("Unsuspected problem");
        return mapToCurrencyDto(newCurrency.get());
    }

    private Currency parseCreateCurrencyRequest(CreateCurrencyRequest request){
        if(request.name() == null || request.name().isBlank())
            throw new ValidationException("%s form field is missing".formatted("name"));
        if(request.code() == null || request.code().isBlank())
            throw new ValidationException("%s form field is missing".formatted("code"));
        if(request.sign() == null || request.sign().isBlank())
            throw new ValidationException("%s form field is missing".formatted("sign"));
        var currency = new Currency(null, request.code().toUpperCase(), request.name(), request.sign());
        validateCurrency(currency);
        return currency;
    }

    private void validateCurrency(Currency currency){
        if(currency.code() == null) throw new ValidationException("Code cannot be null");
        if(currency.code().length() != CODE_LENGTH) throw new ValidationException("Code length must be equal %d".formatted(CODE_LENGTH));
        if(!Patterns.ENG_LETTERS.matcher(currency.code()).matches())
            throw new ValidationException("Code must consist entirely of English letters");

        if(currency.fullName() == null) throw new ValidationException("Full name cannot be null");
        if(currency.fullName().length() < NAME_MIN_LENGTH || currency.fullName().length() > NAME_MAX_LENGTH)
            throw new ValidationException("Full name length cannot be less than %d and more than %d".formatted(NAME_MIN_LENGTH, NAME_MAX_LENGTH));
        if(!Patterns.ENG_LETTERS_AND_SPACES_BETWEEN_WORDS.matcher(currency.fullName()).matches())
            throw new ValidationException("Full name can contain only letters and spaces between words");

        if(currency.sign() == null) throw new ValidationException("Sign cannot be null");
        if(currency.sign().isBlank() || currency.sign().length() > SIGN_MAX_LENGTH)
            throw new ValidationException("Sign length cannot be less than 1 and more than %d".formatted(SIGN_MAX_LENGTH));
    }

    @Override
    public Currency getGeneralCurrency(){
        return cacheGeneralCurrency;
    }
}
