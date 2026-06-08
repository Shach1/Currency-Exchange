package ru.trukhmanov.dao;

import org.sqlite.SQLiteErrorCode;
import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.entity.ExchangeRate;
import ru.trukhmanov.exception.DatabaseException;
import ru.trukhmanov.exception.EntityAlreadyExistException;
import ru.trukhmanov.util.DbManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDaoImpl implements ExchangeRatesDao{
    private static final String FIND_ALL_QUERY = """
            SELECT
            	   er.id				AS id,
            	   base_cur.id			AS baseCurrencyId,
            	   base_cur.code		AS baseCurrencyCode,
            	   base_cur.full_name	AS baseCurrencyFullName,
            	   base_cur.sign		AS baseCurrencySign,
            	   target_cur.id		AS targetCurrencyId,
            	   target_cur.code		AS targetCurrencyCode,
            	   target_cur.full_name	AS targetCurrencyFullName,
            	   target_cur.sign		AS targetCurrencySign,
            	   er.rate				AS rate
            FROM `exchange_rates` er
            		 JOIN `currencies` base_cur ON (er.base_currency_id = base_cur.id)
            		 JOIN `currencies` target_cur ON (er.target_currency_id = target_cur.id)
            """;

    private static final String FIND_BY_CURRENCIES_ID_QUERY = FIND_ALL_QUERY + """
            WHERE er.base_currency_id = ? AND er.target_currency_id = ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO `exchange_rates`(base_currency_id, target_currency_id, rate)
            VALUES(?, ?, ?)
            """;

    private static final String UPDATE_RATE_QUERY = """
            UPDATE `exchange_rates`
            SET rate = ?
            WHERE base_currency_id = ? and target_currency_id = ?
            """;

    @Override
    public List<ExchangeRate> getAll(){
        try(var connection = DbManager.getConnection(); var statement = connection.prepareStatement(FIND_ALL_QUERY)){
            ResultSet resultSet = statement.executeQuery();
            return mapResultSetToList(resultSet);
        } catch (SQLException e){
            throw new DatabaseException("Unsuspected database problem");
        }
    }


    @Override
    public Optional<ExchangeRate> findByCurrenciesId(Integer baseCurrencyId, Integer targetCurrencyId){
        try(var connection = DbManager.getConnection(); var statement = connection.prepareStatement(FIND_BY_CURRENCIES_ID_QUERY)){
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);
            ResultSet resultSet = statement.executeQuery();
            var result = mapResultSetToList(resultSet);
            if(result.isEmpty()){
                return Optional.empty();
            }
            return Optional.of(result.getFirst());
        } catch (SQLException e){
            throw new DatabaseException("Unsuspected database problem");
        }
    }

    @Override
    public Optional<ExchangeRate> insert(ExchangeRate exchangeRate){
        try(var connection = DbManager.getConnection(); var statement = connection.prepareStatement(INSERT_QUERY)){
            statement.setInt(1, exchangeRate.baseCurrency().id());
            statement.setInt(2, exchangeRate.targetCurrency().id());
            statement.setBigDecimal(3, exchangeRate.rate());
            statement.executeUpdate();
            return findByCurrenciesId(exchangeRate.baseCurrency().id(), exchangeRate.targetCurrency().id());
        } catch (SQLException e){
            if(e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code){
                throw new EntityAlreadyExistException("A currency pair with this code already exists");
            }
            throw new DatabaseException("Insert failed");
        }
    }

    @Override
    public Optional<ExchangeRate> updateRate(ExchangeRate exchangeRate){
        try(var connection = DbManager.getConnection(); var statement = connection.prepareStatement(UPDATE_RATE_QUERY)){
            statement.setBigDecimal(1, exchangeRate.rate());
            statement.setInt(2, exchangeRate.baseCurrency().id());
            statement.setInt(3, exchangeRate.targetCurrency().id());
            statement.executeUpdate();
            return findByCurrenciesId(exchangeRate.baseCurrency().id(), exchangeRate.targetCurrency().id());
        } catch (SQLException e){
            throw new DatabaseException("Update failed with massage: " + e.getMessage());
        }
    }

    private List<ExchangeRate> mapResultSetToList(ResultSet resultSet){
        List<ExchangeRate> list = new ArrayList<>();
        try{
            while(resultSet.next()){
                var baseCurrency = new Currency(
                        resultSet.getInt("baseCurrencyId"),
                        resultSet.getString("baseCurrencyCode"),
                        resultSet.getString("baseCurrencyFullName"),
                        resultSet.getString("baseCurrencySign")
                );
                var targetCurrency = new Currency(
                        resultSet.getInt("targetCurrencyId"),
                        resultSet.getString("targetCurrencyCode"),
                        resultSet.getString("targetCurrencyFullName"),
                        resultSet.getString("targetCurrencySign")
                );
                list.add(new ExchangeRate(
                        resultSet.getInt("id"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getBigDecimal("rate")));
            }
        } catch (SQLException e){
            throw new DatabaseException("Failed data conversion from database");
        }
        return list;
    }
}