package ru.trukhmanov.exception;

public class ExchangeRateNotFound extends Exception{
    public ExchangeRateNotFound(String message){
        super(message);
    }

    public ExchangeRateNotFound(){
        super("ExchangeRate not found");
    }
}
