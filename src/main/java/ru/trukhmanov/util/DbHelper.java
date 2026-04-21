package ru.trukhmanov.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbHelper{
    private static DbHelper instance = null;
    private Connection connection;
    private DbHelper(){
        try{
            connection = DriverManager.getConnection("jdbc:sqlite::resource:db/CurrencyConversion.db");
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    };

    public static DbHelper getInstance(){
        if(instance == null) instance = new DbHelper();
        return instance;
    }

    public PreparedStatement getPrepareStatement(String sql) throws SQLException{
        return connection.prepareStatement(sql);
    }
}
