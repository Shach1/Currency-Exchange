package ru.trukhmanov.model.dao;

import ru.trukhmanov.exception.DatabaseException;
import ru.trukhmanov.model.entity.ExchangeRate;
import ru.trukhmanov.util.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao{
    public boolean insert(ExchangeRate exchangeRate){
        String sqlInsert = """
                INSERT INTO `exchange_rates`(base_currency_id, target_currency_id, rate)
                VALUES(?, ?, ?)
                """;
        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlInsert)
        ){
            statement.setInt(1, exchangeRate.baseCurrencyId());
            statement.setInt(2, exchangeRate.targetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.rate());
            return statement.executeUpdate() == 1;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
    }

    public List<ExchangeRate> getAll(){
        String sqlSelect = "SELECT * from `exchange_rates`";

        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlSelect)
        ){
            ResultSet resultSet = statement.executeQuery();
            return mapResultSetToList(resultSet);
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
    }

    private List<ExchangeRate> mapResultSetToList(ResultSet resultSet){
        List<ExchangeRate> list = new ArrayList<>();
        try{
            while(resultSet.next()){
                list.add(new ExchangeRate(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getBigDecimal(4)));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
        return list;
    }

    public Optional<ExchangeRate> findById(Integer id){
        String sqlFind = "SELECT * FROM `exchange_rates` WHERE id = ?";
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

    public Optional<ExchangeRate> findByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId){
        String sqlFind = """
        SELECT * FROM `exchange_rates`
        WHERE base_currency_id = ? AND target_currency_id = ?
        """;
        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlFind)
        ){
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);
            ResultSet resultSet = statement.executeQuery();
            var result = mapResultSetToList(resultSet);
            if(result.isEmpty()) return Optional.empty();
            return Optional.of(result.getFirst());
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
    }

    public boolean updateRate(ExchangeRate exchangeRate){
        String sqlUpdate = """
                UPDATE `exchange_rates`
                SET rate = ?
                WHERE base_currency_id = ? and target_currency_id = ?
                """;
        try(var connection = DbHelper.getConnection();
            var statement = connection.prepareStatement(sqlUpdate)
        ){
            statement.setBigDecimal(1, exchangeRate.rate());
            statement.setInt(2, exchangeRate.baseCurrencyId());
            statement.setInt(3, exchangeRate.targetCurrencyId());
            return statement.executeUpdate() == 1;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException();
        }
    }
}
