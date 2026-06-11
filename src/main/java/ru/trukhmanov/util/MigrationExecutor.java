package ru.trukhmanov.util;

import org.sqlite.SQLiteErrorCode;
import ru.trukhmanov.exception.DatabaseException;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public final class MigrationExecutor{
    private final List<String> migrationQueries;

    public MigrationExecutor(String fileName){
        try{
            InputStream is = openMigrationFile(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bf = new BufferedReader(isr);

            StringBuilder sqlStrings = new StringBuilder();
            String line = bf.readLine();

            while(line != null){
                sqlStrings.append("\n").append(line);
                line = bf.readLine();
            }

            migrationQueries = Arrays.asList(sqlStrings.toString().split(";"));
        } catch (IOException e){
            throw new DatabaseException("Migration error: The migration file does not exist or could not be found");
        }
    }

    public void migrate(){
        for(String query : migrationQueries){
            try(var connection = DatabaseConnectionProvider.getConnection();
                var statement = connection.prepareStatement(query)){
                statement.executeUpdate();
            } catch (SQLException e){
                if (e.getErrorCode() != SQLiteErrorCode.SQLITE_CONSTRAINT.code){
                    throw new DatabaseException("Error while performing migration");
                }
            }
        }
    }

    private InputStream openMigrationFile(String fileName) throws FileNotFoundException{
        File migrationFile = new File(fileName);
        if (migrationFile.isFile()){
            return new FileInputStream(migrationFile);
        }
        var inputStream = this.getClass().getResourceAsStream(fileName);
        if (inputStream == null){
            inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        }
        return inputStream;
    }
}
