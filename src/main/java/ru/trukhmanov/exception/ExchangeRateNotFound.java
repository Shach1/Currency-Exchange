package ru.trukhmanov.exception;

public class ExchangeRateNotFound extends RuntimeException{
    public ExchangeRateNotFound(String message){
        super(message);
    }

    public ExchangeRateNotFound(){
        super("ExchangeRate not found");
    }
}
