package ru.trukhmanov.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.trukhmanov.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnectionProvider{
    private static final HikariDataSource DATA_SOURCE;

    private DatabaseConnectionProvider(){
    }

    static{
        var config = new HikariConfig("db.properties");
        DATA_SOURCE = new HikariDataSource(config);
    }

    public static Connection getConnection(){
        try{
            return DATA_SOURCE.getConnection();
        } catch (SQLException e){
            throw new DatabaseException();
        }
    }

    public static void closeDataSource(){
        DATA_SOURCE.close();
    }
}
