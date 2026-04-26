package ru.trukhmanov.exception;

public class CurrencyNotFound extends RuntimeException{
    public CurrencyNotFound(String message){
        super(message);
    }

    public CurrencyNotFound(){
        super("Currency not found");
    }
}
