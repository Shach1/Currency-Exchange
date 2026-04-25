package ru.trukhmanov.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHelper{
    private DbHelper(){}
    private final static String CONNECTION_URL = "jdbc:sqlite:./src/main/resources/db/CurrencyConversion.db";

    public static Connection getConnection() {
        try{
            Class.forName("org.sqlite.JDBC");
            var connection = DriverManager.getConnection(CONNECTION_URL);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }
}
