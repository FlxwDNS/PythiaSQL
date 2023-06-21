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

    public static void enable(String host, int port, String database, String user, String password) {
        connection = new DatabaseConnectHandler(host, port, database, user, password);
    }

    @Deprecated
    private static void createTableIfNotExists(String tableName, String[] columns) {
    }

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

            return connection.executeQuery("SELECT * FROM " + tableName, resultSet -> {
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
        });
    }
}