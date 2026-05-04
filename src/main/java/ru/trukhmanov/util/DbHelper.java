package ru.trukhmanov.util;

import ru.trukhmanov.exception.DatabaseException;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHelper{
    private DbHelper(){}

    public static Connection getConnection() {
        try{
            String base = System.getProperty("catalina.base");
            if (base == null || base.isBlank()) {
                throw new DatabaseException("catalina.base is not set");
            }
            Path dbPath = Path.of(base, "webapps", "ROOT", "WEB-INF", "classes", "db", "CurrencyConversion.db");
            String connectionUrl = "jdbc:sqlite:" + dbPath.toAbsolutePath();

            Class.forName("org.sqlite.JDBC");
            var connection = DriverManager.getConnection(connectionUrl);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException | ClassNotFoundException e){
            throw new DatabaseException();
        }
    }
}
