package ru.trukhmanov.util;

import ru.trukhmanov.exception.ValidationException;

import java.math.BigDecimal;

public class Parser{
    public static BigDecimal parseBigDecimal(String bigDecimal){
        try{
            return new BigDecimal(bigDecimal);
        } catch (RuntimeException ignore){
            throw new ValidationException("Invalid value: %s".formatted(bigDecimal));
        }
    }
}
