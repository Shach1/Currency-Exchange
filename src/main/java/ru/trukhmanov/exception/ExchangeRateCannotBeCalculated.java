package ru.trukhmanov.exception;

public class ExchangeRateCannotBeCalculated extends RuntimeException{
    public ExchangeRateCannotBeCalculated(String message){
        super(message);
    }

    public ExchangeRateCannotBeCalculated(){
        super("It is impossible to calculate the exchange rate for this currency pair");
    }
}
