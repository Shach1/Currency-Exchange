package ru.trukhmanov;

import ru.trukhmanov.util.DbHelper;

import java.sql.*;

public class Main{
    public static void main(String[] args) throws SQLException{

        String sqlGetAllCurrencies = ("SELECT * FROM `currencies`");
        PreparedStatement statement = DbHelper.getInstance().getPrepareStatement(sqlGetAllCurrencies);

        ResultSet result = statement.executeQuery();
        while(result.next()){
            System.out.println(result.getString("full_name"));
        }
    }
}