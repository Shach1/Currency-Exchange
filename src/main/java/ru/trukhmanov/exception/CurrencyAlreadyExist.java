package ru.trukhmanov.exception;

public class CurrencyAlreadyExist extends Exception{
    public CurrencyAlreadyExist(String reason){
        super(reason);
    }
}
