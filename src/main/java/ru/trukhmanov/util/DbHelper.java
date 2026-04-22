package ru.trukhmanov.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHelper{
    private static DbHelper instance = null;
    private final Connection connection;

    private DbHelper(){
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:./src/main/resources/db/CurrencyConversion.db");
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(true);
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static DbHelper getInstance(){
        if(instance == null) instance = new DbHelper();
        return instance;
    }

    public Connection getConnection(){
        return connection;
    }
}
