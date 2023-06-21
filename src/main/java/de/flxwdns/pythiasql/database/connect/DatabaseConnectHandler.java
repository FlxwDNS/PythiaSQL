package de.flxwdns.pythiasql.database.connect;

import lombok.Getter;

import java.sql.*;

public final class DatabaseConnectHandler {

    @Getter
    private Connection connection;

    public DatabaseConnectHandler(String host, int port, String database, String user, String password) {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
            System.out.println("[INFO] Connection to database was successfully established!");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface SqlFunction<I, O> {
        O apply(I var1) throws SQLException;
    }

    public void execute(String query) {
        try(var statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public <T> T executeQuery(String query, SqlFunction<ResultSet, T> function, T defaultValue) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return function.apply(resultSet);
            } catch (Exception throwable) {
                throwable.printStackTrace();
                return defaultValue;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return defaultValue;
    }

    public void executeUpdate(String query) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
