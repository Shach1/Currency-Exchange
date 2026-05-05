package ru.trukhmanov.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.trukhmanov.exception.DatabaseException;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class DbHelper{
    private static final DataSource datasource;

    private DbHelper(){
    }

    static{
        var config = new HikariConfig();
        config.setJdbcUrl(getConnectionUrl());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setPoolName("currency-conversion");
        config.setMaximumPoolSize(16);
        config.setMinimumIdle(16);
        config.setConnectionTimeout(5_000);
        config.setValidationTimeout(2_000);
        config.setMaxLifetime(25 * 60_000);
        config.setKeepaliveTime(2 * 60_000);
        config.setTransactionIsolation(String.valueOf(Connection.TRANSACTION_READ_COMMITTED));
        config.setAutoCommit(true);
        datasource = new HikariDataSource(config);
    }
    private static String getConnectionUrl(){
        String base = System.getProperty("catalina.base");
        if(base == null || base.isBlank()){
            throw new DatabaseException("catalina.base is not set");
        }
        Path dbPath = Path.of(base, "webapps", "ROOT", "WEB-INF", "classes", "db", "CurrencyConversion.db");
        return "jdbc:sqlite:" + dbPath.toAbsolutePath();
    }

    public static Connection getConnection(){
        try{
            return datasource.getConnection();
        } catch (SQLException e){
            throw new DatabaseException();
        }
    }
}
