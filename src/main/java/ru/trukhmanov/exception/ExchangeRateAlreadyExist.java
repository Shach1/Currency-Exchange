package ru.trukhmanov.exception;

public class ExchangeRateAlreadyExist extends Exception{
    public ExchangeRateAlreadyExist(String message){
        super(message);
    }

    public ExchangeRateAlreadyExist(){
        super("A currency pair with this code already exists.");
    }
}
