package ru.trukhmanov.dao;

import org.sqlite.SQLiteErrorCode;
import ru.trukhmanov.entity.Currency;
import ru.trukhmanov.exception.DatabaseException;
import ru.trukhmanov.exception.EntityAlreadyExistException;
import ru.trukhmanov.util.DatabaseConnectionProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrenciesDaoImpl implements CurrenciesDao{
    private static final String FIND_ALL_QUERY = """
            SELECT * from `currencies`
            """;

    private static final String FIND_BY_CODE_QUERY = FIND_ALL_QUERY + """
            WHERE code = ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO `currencies`(code, full_name, sign)
            VALUES(?, ?, ?)
            RETURNING *
            """;

    @Override
    public List<Currency> getAll(){
        try(var connection = DatabaseConnectionProvider.getConnection();
            var statement = connection.prepareStatement(FIND_ALL_QUERY)){
            ResultSet resultSet = statement.executeQuery();
            return mapResultSetToList(resultSet);
        } catch (SQLException e){
            throw new DatabaseException("Unsuspected database problem");
        }
    }

    @Override
    public Optional<Currency> findByCode(String code){
        try(var connection = DatabaseConnectionProvider.getConnection();
            var statement = connection.prepareStatement(FIND_BY_CODE_QUERY)){
            statement.setString(1, code);
            ResultSet result = statement.executeQuery();
            return mapResultSetToOptional(result);
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new DatabaseException("Unsuspected database problem");
        }
    }

    @Override
    public Optional<Currency> insert(Currency currency){
        try(var connection = DatabaseConnectionProvider.getConnection();
            var statement = connection.prepareStatement(INSERT_QUERY)){
            statement.setString(1, currency.code());
            statement.setString(2, currency.fullName());
            statement.setString(3, currency.sign());
            ResultSet result = statement.executeQuery();
            return mapResultSetToOptional(result);
        } catch (SQLException e){
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code){
                throw new EntityAlreadyExistException("A currency with this code already exists");
            }
            throw new DatabaseException("Insert failed");
        }
    }

    private Optional<Currency> mapResultSetToOptional(ResultSet resultSet){
        try{
            if (resultSet.next()){
                return Optional.of(new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sign"))
                );
            }
            return Optional.empty();
        } catch (SQLException e){
            throw new DatabaseException("Failed data conversion from database");
        }
    }

    private List<Currency> mapResultSetToList(ResultSet resultSet){
        List<Currency> list = new ArrayList<>();
        try{
            while(resultSet.next()){
                list.add(new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sign"))
                );
            }
        } catch (SQLException e){
            throw new DatabaseException("Failed data conversion from database");
        }
        return list;
    }
}