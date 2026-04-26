package ru.trukhmanov.exception;

public class MissingFormField extends RuntimeException{
    public MissingFormField(String message){
        super(message);
    }

    public MissingFormField(){
        super("A required form field is missing");
    }
}
