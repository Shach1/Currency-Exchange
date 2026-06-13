package ru.trukhmanov.validation;

import ru.trukhmanov.dto.request.CreateCurrencyRequestDto;
import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.exception.ValidationException;

import java.util.regex.Pattern;

import static ru.trukhmanov.service.CurrenciesService.*;

public final class CurrencyValidator{
    public static final Pattern ENG_LETTERS = Pattern.compile("[A-Za-z]+");
    public static final Pattern ENG_LETTERS_AND_SPACES_BETWEEN_WORDS = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");

    private CurrencyValidator(){
    }

    public static void validateCurrency(Currency currency){
        validateCode(currency.code());
        validateFullName(currency.fullName());
        validateSign(currency.sign());
    }

    public static void validateCode(String code){
        if (code == null || code.isBlank()){
            throw new ValidationException("%s form field is missing".formatted("code"));
        }
        if (code.length() != CODE_LENGTH){
            throw new ValidationException("Code length must be equal %d".formatted(CODE_LENGTH));
        }
        if (! ENG_LETTERS.matcher(code).matches()){
            throw new ValidationException("Code must consist entirely of English letters");
        }
    }

    public static void validateFullName(String fullName){
        if (fullName == null || fullName.isBlank()){
            throw new ValidationException("%s form field is missing".formatted("fullName"));
        }
        if (fullName.length() < NAME_MIN_LENGTH || fullName.length() > NAME_MAX_LENGTH){
            throw new ValidationException("Full name length cannot be less than %d and more than %d"
                    .formatted(NAME_MIN_LENGTH, NAME_MAX_LENGTH));
        }
        if (! ENG_LETTERS_AND_SPACES_BETWEEN_WORDS.matcher(fullName).matches()){
            throw new ValidationException("Full name can contain only English letters and spaces between words");
        }
    }

    public static void validateSign(String sign){
        if (sign == null || sign.isBlank()){
            throw new ValidationException("%s form field is missing".formatted("sign"));
        }
        if (sign.length() > SIGN_MAX_LENGTH){
            throw new ValidationException("Sign length cannot be less than 1 and more than %d"
                    .formatted(SIGN_MAX_LENGTH));
        }
    }

    public static void validateCreateCurrencyRequestDto(CreateCurrencyRequestDto request){
        if(request.name() == null || request.name().isBlank())
            throw new ValidationException("%s form field is missing".formatted("name"));
        if(request.code() == null || request.code().isBlank())
            throw new ValidationException("%s form field is missing".formatted("code"));
        if(request.sign() == null || request.sign().isBlank())
            throw new ValidationException("%s form field is missing".formatted("sign"));
    }
}
