package ru.trukhmanov.util;

import ru.trukhmanov.exception.ValidationException;

import java.math.BigDecimal;

public final class Parser{
    private Parser(){
    }

    public static BigDecimal parseBigDecimal(String bigDecimal){
        try{
            return new BigDecimal(bigDecimal);
        } catch (NumberFormatException e){
            throw new ValidationException("Invalid value: %s".formatted(bigDecimal));
        }
    }
}
