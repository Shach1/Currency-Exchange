package ru.trukhmanov.exception;

public class ExchangeRateAlreadyExist extends RuntimeException{
    public ExchangeRateAlreadyExist(String message){
        super(message);
    }

    public ExchangeRateAlreadyExist(){
        super("A currency pair with this code already exists.");
    }
}
