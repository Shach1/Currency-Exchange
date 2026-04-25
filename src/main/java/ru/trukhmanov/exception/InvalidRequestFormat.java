package ru.trukhmanov.exception;

public class InvalidRequestFormat extends Exception{
    public InvalidRequestFormat(String message){
        super(message);
    }

    public InvalidRequestFormat(){
        super("Invalid request format");
    }
}
