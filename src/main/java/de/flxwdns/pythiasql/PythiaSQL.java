package de.flxwdns.pythiasql;

import de.flxwdns.pythiasql.database.connect.DatabaseConnectHandler;
import de.flxwdns.pythiasql.database.table.DatabaseEntry;
import de.flxwdns.pythiasql.database.table.DatabaseTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PythiaSQL {
    private static DatabaseConnectHandler connection = null;
    private final static List<DatabaseTable> tables = new ArrayList<>();

    /**
     * Method: enable(String host, int port, String database, String user, String password)
     * <p>
     * Enables the database connection by creating a new instance of the DatabaseConnectHandler class.
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
     * <p>
     * DatabaseConnector.enable(host, port, database, user, password);
     *
     * Note: This method assumes that the DatabaseConnectHandler class is available and handles the actual connection to the database using the provided parameters.
     */
    public static void enable(String host, int port, String database, String user, String password) {
        connection = new DatabaseConnectHandler(host, port, database, user, password);
    }

    /**
     * Method: createTableIfNotExists(String tableName, String[] columns)
     * <p>
     * [Deprecated] This method is deprecated and currently not implemented.
     *
     * @param tableName (String): The name of the table to create.
     * @param columns (String[]): An array of column names for the table.
     * <p>
     * Example usage:
     * <p>
     * String tableName = "mytable"; // Specify the table name
     * String[] columns = {"column1", "column2"}; // Specify the column names
     * <p>
     * [Deprecated]
     * createTableIfNotExists(tableName, columns);
     * <p>
     * Note: This method is deprecated and currently not implemented. It was intended to create a table if it did not already exist.
     * It is recommended to use alternative methods or frameworks for table creation and management.
     */
    @Deprecated
    @SuppressWarnings("ALL")
    private static void createTableIfNotExists(String tableName, String[] columns) {
    }

    /**
     * Method: getTable(String tableName)
     * <p>
     * Retrieves a DatabaseTable object based on the specified table name.
     *
     * @param tableName (String): The name of the table to retrieve.
     * @return DatabaseTable: The DatabaseTable object representing the specified table.
     * <p>
     * Example usage:
     * <p>
     * String tableName = "mytable"; // Specify the table name
     * DatabaseTable table = DatabaseConnector.getTable(tableName);
     * <p>
     * Note: This method assumes that the connection has been established using the `enable` method before calling this method.
     * If the connection has not been established, an error message is displayed, and null is returned.
     * The behavior of this method assumes that the DatabaseConnectHandler and DatabaseTable classes are available.
     */
    public static DatabaseTable getTable(String tableName) {
        if(connection == null) {
            System.err.println("[ERROR] PythiaSQL is not connected! Use enable() first!");
            return null;
        }

        return tables.stream().filter(it -> it.getTableName().equals(tableName)).findFirst().orElseGet(() -> {
            List<String> types = new ArrayList<>();
            connection.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" + tableName + "'", resultSet -> {
                while (resultSet.next()) {
                    types.add(resultSet.getString("COLUMN_NAME"));
                }
                return null;
            }, null);

           var result = connection.executeQuery("SELECT * FROM " + tableName, resultSet -> {
                List<DatabaseEntry> values = new ArrayList<>();
                int[] id = new int[]{0};
                while (resultSet.next()) {
                    types.forEach(it -> {
                        try {
                            values.add(new DatabaseEntry(id[0], resultSet.getObject(it), it));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    id[0]++;
                }
                var table = new DatabaseTable(connection, tableName, values);
                tables.add(table);
                return table;
            }, null);

           if(result == null) {
               System.err.println("[ERROR] Table " + tableName + " not found!");
           }
           return result;
        });
    }
}