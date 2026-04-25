package ru.trukhmanov.exception;

import java.sql.SQLException;

public class RowAlreadyExist extends SQLException{
    public RowAlreadyExist(String reason){
        super(reason);
    }
}
