package ru.trukhmanov.dao;

import ru.trukhmanov.entity.Currencies;
import ru.trukhmanov.util.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDao{
    public static boolean insertCurrencies(Currencies currencies){
        String sqlInsert = """
                INSERT INTO `currencies`(code, full_name, sign)
                VALUES(?, ?, ?)
                """;
        try(var statement = DbHelper.getInstance().getConnection().prepareStatement(sqlInsert)){
            statement.setString(1, currencies.code());
            statement.setString(2, currencies.fullName());
            statement.setString(3, currencies.sign());
            return statement.executeUpdate() == 1;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static List<Currencies> getAllCurrencies(){
        String sqlSelect = "SELECT * from `currencies`";

        try(var statement = DbHelper.getInstance().getConnection().prepareStatement(sqlSelect)){
            ResultSet resultSet = statement.executeQuery();
            return mapResultSetToList(resultSet);
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static List<Currencies> mapResultSetToList(ResultSet resultSet){
        List<Currencies> list = new ArrayList<>();
        try{
            while(resultSet.next()){
                list.add(new Currencies(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }

    public static boolean updateCurrencies(Currencies currencies){
        String sqlUpdate = """
                 UPDATE `currencies`
                 SET\s
                 	code = ?,
                 	full_name = ?,
                 	sign = ?
                 WHERE id == ?
                \s""";
        try(var statement = DbHelper.getInstance().getConnection().prepareStatement(sqlUpdate)){
            if(currencies.id() == null) throw new IllegalArgumentException("Id cannot be null");
            statement.setString(1, currencies.code());
            statement.setString(2, currencies.fullName());
            statement.setString(3, currencies.sign());
            statement.setInt(4, currencies.id());
            return statement.executeUpdate() == 1;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
