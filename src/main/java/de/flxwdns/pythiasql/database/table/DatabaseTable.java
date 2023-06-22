package de.flxwdns.pythiasql.database.table;

import de.flxwdns.pythiasql.database.connect.DatabaseConnectHandler;
import de.flxwdns.pythiasql.database.filter.DatabaseEntryFilter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public final class DatabaseTable {
    @Getter(AccessLevel.NONE)
    private final DatabaseConnectHandler connection;

    private final String tableName;
    private final List<DatabaseEntry> entries;

    /**
     * Method: isEntryExists(String[] columns, Object[] values)
     * <p>
     * Checks if an entry exists in the database table based on the specified columns and values.
     *
     * @param columns (String[]): An array of column names used to identify the entry.
     * @param values (Object[]): An array of values corresponding to the columns used for identification.
     * @return boolean: True if an entry with the specified columns and values exists, false otherwise.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * String[] targetColumns = {"column1", "column2"}; // Specify the column names
     * Object[] targetValues = {value1, value2}; // Specify the corresponding values
     * boolean entryExists = table.isEntryExists(targetColumns, targetValues);
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public boolean isEntryExists(List<String> columns, List<Object> values) {
        for (String column : columns) {
            for (Object value : values) {
                for (DatabaseEntry entry : entries) {
                    if(Objects.equals(entry.getColumnName(), column) && entry.getValue().equals(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method: createEntry(String[] columns, Object[] values)
     * <p>
     * Creates a new entry in the database table. It accepts two parameters: `columns` and `values`.
     *
     * @param columns (String[]): An array of column names to associate with the new entry.
     * @param values (Object[]): An array of values to be assigned to the corresponding columns.
     * <p>
     * Example usage:
     * <p>
     * String[] columns = {"column1", "column2", "column3"};
     * Object[] values = {value1, value2, value3};
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * table.createEntry(columns, values);
     * <p>
     * Note: Ensure that you correctly initialize and connect the `connection` instance to the database before using this method.
     *       Additionally, take care to avoid potential SQL injections by properly sanitizing or handling the values in the database query.
     */
    public CompletableFuture<Void> createEntry(List<String> columns, List<Object> values) {
        String[] stringArray = new String[values.toArray().length];
        for (int i = 0; i < values.toArray().length; i++) {
            stringArray[i] = "'" + values.toArray()[i].toString() + "'";
        }
        try {
            connection.executeUpdate("INSERT INTO `" + tableName + "` (" + "`" + String.join("`, `", columns.toArray(new String[]{})) + "`" + ") VALUES (" +  String.join(", ", stringArray) + ")");
        } catch (Exception e) {
            System.err.println("[ERROR] Error while creating entry in table " + tableName + ": " + e);
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Method: removeEntry(String[] columns, Object[] values)
     * <p>
     * Removes an entry from the database table based on the specified columns and values.
     *
     * @param columns (String[]): An array of column names to identify the entry to be removed.
     * @param values (Object[]): An array of values corresponding to the columns to identify the entry.
     * <p>
     * Example usage:
     * <p>
     * String[] columns = {"column1", "column2"};
     * Object[] values = {value1, value2};
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * table.removeEntry(columns, values);
     * <p>
     * Note: Ensure that you correctly initialize and connect the `connection` instance to the database before using this method.
     *       Additionally, take care to properly sanitize or handle the values in the database query to prevent SQL injection vulnerabilities.
     */
    public CompletableFuture<Void> removeEntry(String[] columns, Object[] values) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(tableName).append(" WHERE ");

        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]).append(" = '").append(values[i].toString()).append("'");
            if (i < columns.length - 1) {
                queryBuilder.append(" AND ");
            }
        }

        try {
            connection.executeUpdate(queryBuilder.toString());
        } catch (Exception e) {
            System.err.println("[ERROR] Error while removing entry in table " + tableName + ": " + e);
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Method: getFirst()
     * <p>
     * Retrieves the first occurrence of a DatabaseEntry from the entries list.
     *
     * @return List<DatabaseEntry>: A list containing the first occurrence of a DatabaseEntry.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * List<DatabaseEntry> firstEntries = table.getFirst();
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public List<DatabaseEntry> getFirst() {
        List<DatabaseEntry> tempList = new ArrayList<>();
        entries.stream().findFirst().ifPresent(tempList::add);

        entries.forEach(entry -> {
            if(tempList.stream().anyMatch(it -> it.getId() == entry.getId()) && entry != tempList.stream().findFirst().get()) {
                tempList.add(entry);
            }
        });
        return tempList;
    }

    /**
     * Method: getEntriesById(int id)
     * <p>
     * Retrieves a list of DatabaseEntry objects from the entries list based on the specified ID.
     *
     * @param id (int): The ID value used to filter the DatabaseEntry objects.
     * @return List<DatabaseEntry>: A list containing the DatabaseEntry objects matching the specified ID.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * int targetId = 123; // Specify the ID value
     * List<DatabaseEntry> entriesById = table.getEntriesById(targetId);
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public List<DatabaseEntry> getEntriesById(int id) {
        return entries.stream().filter(it -> it.getId() == id).collect(Collectors.toList());
    }

    /**
     * Method: filter(DatabaseEntryFilter filter)
     * <p>
     * Filters the entries in the database table based on the provided filter.
     *
     * @param filter (DatabaseEntryFilter): The filter used to specify the criteria for filtering the entries.
     * @return DatabaseTable: A new DatabaseTable instance containing the filtered entries.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * DatabaseEntryFilter filter = new DatabaseEntryFilter(); // Example filter object
     * DatabaseTable filteredTable = table.filter(filter);
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public DatabaseTable filter(DatabaseEntryFilter filter) {
        List<Integer> allowedIds = new ArrayList<>();
        List<DatabaseEntry> temp = new ArrayList<>(entries.stream().filter(entry -> {
            if (filter.getId() != null) {
                return filter.getId() == entry.getId();
            }
            if (filter.getColumnName() != null) {
                return filter.getColumnName().equalsIgnoreCase(entry.getColumnName());
            }
            if(filter.getValue() != null) {
                if(!(filter.getColumn() != null && filter.getColumn().equalsIgnoreCase(entry.getColumnName()))) {
                    return false;
                } else if(filter.getValue().equals(entry.getValue())) {
                    allowedIds.add(entry.getId());
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }).toList());
        entries.forEach(entry -> {
           if(temp.stream().noneMatch(it -> it.equals(entry)) && allowedIds.stream().anyMatch(it -> it == entry.getId())) temp.add(entry);
        });
        return new DatabaseTable(connection, tableName, temp);
    }
}
