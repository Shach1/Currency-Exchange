package ru.trukhmanov.exception;

public class InvalidValue extends RuntimeException{
    public InvalidValue(String message){
        super(message);
    }

    public InvalidValue(){
        super("Invalid value");
    }
}
