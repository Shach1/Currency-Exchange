package ru.trukhmanov.util;

import ru.trukhmanov.exception.InvalidValue;

import java.math.BigDecimal;

public class Parser{
    public static BigDecimal parseBigDecimal(String bigDecimal){
        try{
            return new BigDecimal(bigDecimal);
        } catch (RuntimeException ignore){
            throw new InvalidValue();
        }
    }

    public static Integer parseInteger(String integer){
        try{
            return Integer.valueOf(integer);
        } catch (RuntimeException ignore){
            throw new InvalidValue();
        }
    }
}
