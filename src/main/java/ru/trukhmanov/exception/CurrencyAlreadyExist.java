package ru.trukhmanov.exception;

public class CurrencyAlreadyExist extends RuntimeException{
    public CurrencyAlreadyExist(String reason){
        super(reason);
    }
}
