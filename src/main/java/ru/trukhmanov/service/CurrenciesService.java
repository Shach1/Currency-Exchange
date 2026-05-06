package ru.trukhmanov.service;

import ru.trukhmanov.exception.*;
import ru.trukhmanov.model.dao.CurrenciesDao;
import ru.trukhmanov.model.entity.Currency;
import ru.trukhmanov.service.dto.request.CreateCurrencyRequest;
import ru.trukhmanov.service.dto.request.UpdateCurrencyRequest;
import ru.trukhmanov.service.dto.response.CurrencyResponse;
import ru.trukhmanov.util.Parser;
import ru.trukhmanov.util.Patterns;

import java.util.List;

public class CurrenciesService{
    public static final int CODE_LENGTH = 3;
    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 20;
    public static final int SIGN_MAX_LENGTH = 4;
    private final CurrenciesDao currenciesDao = new CurrenciesDao();
    private final Currency cacheGeneralCurrency;

    public CurrenciesService(){
        String generalCurrencyCode = "USD";
        var currency = currenciesDao.findByCode(generalCurrencyCode);
        if(currency.isEmpty()) throw new UnsuspectedException("Unable to obtain general currency with code: %s".formatted(generalCurrencyCode));
        cacheGeneralCurrency = currency.get();
    }

    public List<CurrencyResponse> getAllCurrencies(){
        return currenciesDao.getAll().stream()
                .map(this::mapToCurrencyDto)
                .toList();
    }

    public CurrencyResponse mapToCurrencyDto(Currency currency){
        return new CurrencyResponse(
                currency.id(),
                currency.code(),
                currency.fullName(),
                currency.sign());
    }

    public CurrencyResponse getCurrencyByCode(String code){
        if(code == null || code.length() != CODE_LENGTH) throw new InvalidRequestFormat();
        var result = currenciesDao.findByCode(code);
        if(result.isEmpty()) throw new CurrencyNotFound("Currency with code: %s not found".formatted(code));
        return mapToCurrencyDto(result.get());
    }

    public CurrencyResponse getCurrencyById(Integer id){
        var result = currenciesDao.findById(id);
        if(result.isEmpty()) throw new CurrencyNotFound("Currency with id: %d not found".formatted(id));
        return mapToCurrencyDto(result.get());
    }

    public CurrencyResponse createCurrency(CreateCurrencyRequest request){
        var currency = parseCreateCurrencyRequest(request);
        if(currenciesDao.findByCode(currency.code()).isPresent()){
            throw new CurrencyAlreadyExist("A currency with this code already exists");
        }
        currenciesDao.insert(currency);
        var newCurrency = currenciesDao.findByCode(currency.code());
        if(newCurrency.isEmpty()) throw new UnsuspectedException();
        return mapToCurrencyDto(newCurrency.get());
    }

    private Currency parseCreateCurrencyRequest(CreateCurrencyRequest request){
        if(request.name() == null || request.name().isEmpty())
            throw new MissingFormField("%s form field is missing".formatted("name"));
        if(request.code() == null || request.code().isEmpty())
            throw new MissingFormField("%s form field is missing".formatted("code"));
        if(request.sign() == null || request.sign().isEmpty())
            throw new MissingFormField("%s form field is missing".formatted("sign"));
        var currency = new Currency(null, request.code(), request.name(), request.sign());
        validateCurrency(currency);
        return currency;
    }

    private void validateCurrency(Currency currency){
        if(currency.code() == null) throw new InvalidValue("Code cannot be null");
        if(currency.code().length() != CODE_LENGTH) throw new InvalidValue("Code length must be equal %d".formatted(CODE_LENGTH));
        if(!Patterns.ENG_LETTERS.matcher(currency.code()).matches())
            throw new InvalidValue("Code must consist entirely of letters");

        if(currency.fullName() == null) throw new InvalidValue("Full name cannot be null");
        if(currency.fullName().length() < NAME_MIN_LENGTH || currency.fullName().length() > NAME_MAX_LENGTH)
            throw new InvalidValue("Full name length cannot be less than %d and more than %d".formatted(NAME_MIN_LENGTH, NAME_MAX_LENGTH));
        if(!Patterns.ENG_LETTERS_AND_SPACES_BETWEEN_WORDS.matcher(currency.fullName()).matches())
            throw new InvalidValue("Full name can contain only letters and spaces between words");

        if(currency.sign() == null) throw new InvalidValue("Sign cannot be null");
        if(currency.sign().isEmpty() || currency.sign().length() > SIGN_MAX_LENGTH)
            throw new InvalidValue("Sign length cannot be less than 1 and more than %d".formatted(SIGN_MAX_LENGTH));
    }

    public CurrencyResponse updateCurrency(UpdateCurrencyRequest request){
        var currency = parseUpdateCurrencyRequest(request);
        if(currenciesDao.findById(currency.id()).isEmpty())
            throw new CurrencyNotFound("Currency with id: %s not found".formatted(request.id()));
        currenciesDao.update(currency);
        var updatedCurrency = currenciesDao.findById(currency.id());
        if(updatedCurrency.isEmpty()) throw new UnsuspectedException();
        return mapToCurrencyDto(updatedCurrency.get());
    }

    private Currency parseUpdateCurrencyRequest(UpdateCurrencyRequest request){
        if(request.id() == null) throw new MissingFormField("%s form field is missing".formatted("id"));
        Integer id = Parser.parseInteger(request.id());
        var currency = parseCreateCurrencyRequest(new CreateCurrencyRequest(request.code(), request.name(), request.sign()));
        return new Currency(id, currency.code(), currency.fullName(), currency.sign());
    }

    protected Currency getGeneralCurrency(){
        return cacheGeneralCurrency;
    }
}
