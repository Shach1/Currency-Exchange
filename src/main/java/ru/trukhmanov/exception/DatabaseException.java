package ru.trukhmanov.exception;

public class DatabaseException extends RuntimeException{
    public DatabaseException(String message){
        super(message);
    }
    public DatabaseException(){
        super("Database is unavailable");
    }
}
