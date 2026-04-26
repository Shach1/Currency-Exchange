package ru.trukhmanov.model.dao;

import ru.trukhmanov.exception.DatabaseException;
import ru.trukhmanov.model.entity.Currency;
import ru.trukhmanov.util.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrenciesDao{
    public void insert(Currency currency){
        String sqlInsert = """
                INSERT INTO `currencies`(code, full_name, sign)
                VALUES(?, ?, ?)
                """;
        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlInsert)
        ){
            statement.setString(1, currency.code());
            statement.setString(2, currency.fullName());
            statement.setString(3, currency.sign());
            statement.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
    }

    public List<Currency> getAll(){
        String sqlSelect = "SELECT * from `currencies`";

        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlSelect)
        ){
            ResultSet resultSet = statement.executeQuery();
            return mapResultSetToList(resultSet);
        } catch (SQLException e){
            throw new DatabaseException();
        }
    }

    public Optional<Currency> findById(Integer id){
        String sqlFind = "SELECT * FROM `currencies` WHERE id = ?";
        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlFind)
        ){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            var result = mapResultSetToList(resultSet);
            if(result.isEmpty()) return Optional.empty();
            return Optional.of(result.getFirst());
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
    }

    public Optional<Currency> findByCode(String code){
        String sqlFind = "SELECT * FROM `currencies` WHERE code = ?";
        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlFind)
        ){
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            var result = mapResultSetToList(resultSet);
            if(result.isEmpty()) return Optional.empty();
            return Optional.of(result.getFirst());
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
    }

    private List<Currency> mapResultSetToList(ResultSet resultSet){
        List<Currency> list = new ArrayList<>();
        try{
            while(resultSet.next()){
                list.add(new Currency(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
        return list;
    }

    public void update(Currency currency){
        String sqlUpdate = """
                 UPDATE `currencies`
                 SET\s
                 	code = ?,
                 	full_name = ?,
                 	sign = ?
                 WHERE id == ?
                \s""";
        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlUpdate)
        ){
            statement.setString(1, currency.code());
            statement.setString(2, currency.fullName());
            statement.setString(3, currency.sign());
            statement.setInt(4, currency.id());
            statement.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
    }
}