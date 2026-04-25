package ru.trukhmanov.exception;

public class MissingFormField extends Exception{
    public MissingFormField(String message){
        super(message);
    }
}
