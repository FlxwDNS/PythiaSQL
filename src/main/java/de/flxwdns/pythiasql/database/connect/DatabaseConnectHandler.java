package de.flxwdns.pythiasql.database.connect;

import lombok.Getter;
import java.sql.*;

@SuppressWarnings("unused")
public final class DatabaseConnectHandler {
    @Getter
    private Connection connection;

    /**
     * Constructor: DatabaseConnectHandler(String host, int port, String database, String user, String password)
     * <p>
     * Constructs a new instance of the DatabaseConnectHandler class and establishes a connection to the specified database.
     *
     * @param host (String): The host address or name of the database server.
     * @param port (int): The port number used for the database connection.
     * @param database (String): The name of the database to connect to.
     * @param user (String): The username for authentication.
     * @param password (String): The password for authentication.
     * <p>
     * Example usage:
     * <p>
     * String host = "localhost"; // Specify the host address
     * int port = 3306; // Specify the port number
     * String database = "mydatabase"; // Specify the database name
     * String user = "myuser"; // Specify the username
     * String password = "mypassword"; // Specify the password
     *
     * DatabaseConnectHandler handler = new DatabaseConnectHandler(host, port, database, user, password);
     */
    public DatabaseConnectHandler(String host, int port, String database, String user, String password) {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
            System.out.println("[INFO] Connection to database was successfully established!");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Functional Interface: SqlFunction<I, O>
     * <p>
     * Represents a function that accepts an input of type I and produces an output of type O, with the possibility of throwing a SQLException.
     *
     * @param <I>: The type of the input to the function.
     * @param <O>: The type of the output from the function.
     * <p>
     * Example usage:
     * <p>
     * SqlFunction<ResultSet, List<String>> function = resultSet -> {
     *     // Process the result set and return a list of strings
     *     // ...
     * };
     * <p>
     * List<String> result = function.apply(resultSet);
     * <p>
     * Note: This interface can be used in scenarios where a function needs to be applied to a result set obtained from executing SQL queries.
     * The function takes an input of type ResultSet and produces an output of type O. It has the flexibility to handle SQLExceptions.
     * Implementations of this interface can be passed as arguments to methods that require result set processing.
     */
    @FunctionalInterface
    public interface SqlFunction<I, O> {
        O apply(I var1) throws SQLException;
    }

    /**
     * Method: execute(String query)
     * <p>
     * Executes the provided SQL query without returning any results.
     *
     * @param query (String): The SQL query to execute.
     * <p>
     * Example usage:
     * <p>
     * String query = "SELECT * FROM mytable"; // Specify the SQL query
     * handler.execute(query);
     */
    public void execute(String query) {
        try(var statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method: executeQuery(String query, SqlFunction<ResultSet, T> function, T defaultValue)
     * <p>
     * Executes the provided SQL query and applies the specified function to the result set.
     *
     * @param query (String): The SQL query to execute.
     * @param function (SqlFunction<ResultSet, T>): The function to apply to the result set.
     * @param defaultValue (T): The default value to return if an exception occurs during execution or applying the function.
     * @return T: The result of applying the function to the result set, or the default value if an exception occurs.
     * <p>
     * Example usage:
     * <p>
     * String query = "SELECT * FROM mytable"; // Specify the SQL query
     * SqlFunction<ResultSet, List<String>> function = resultSet -> {
     *     // Process the result set and return a list of strings
     *     // ...
     * };
     * List<String> result = handler.executeQuery(query, function, null);
     */
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

    /**
     * Method: executeUpdate(String query)
     * <p>
     * Executes the provided SQL query that performs an update operation (e.g., INSERT, UPDATE, DELETE).
     *
     * @param query (String): The SQL query to execute.
     * <p>
     * Example usage:
     * <p>
     * String query = "INSERT INTO mytable (column1, column2) VALUES ('value1', 'value2')"; // Specify the SQL query
     * handler.executeUpdate(query);
     */
    public void executeUpdate(String query) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        }
    }
}
