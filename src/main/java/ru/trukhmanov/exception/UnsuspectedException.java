package ru.trukhmanov.exception;

public class UnsuspectedException extends RuntimeException{
    public UnsuspectedException(String message){
        super(message);
    }

    public UnsuspectedException(){
        super("Unsuspected problem");
    }
}
