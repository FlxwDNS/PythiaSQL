package de.flxwdns.pythiasql.database.table;

import de.flxwdns.pythiasql.database.connect.DatabaseConnectHandler;
import de.flxwdns.pythiasql.database.filter.DatabaseEntryFilter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.text.html.parser.Entity;
import java.util.*;
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
    public boolean isEntryExists(Map<String, Object> values) {
        for (String column : values.keySet().stream().toList()) {
            for (Object value : values.entrySet().stream().toList()) {
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
     * @param values (Map<String, Object>): An map of column names and values to associate with the new entry.
     * <p>
     * Example usage:
     * <p>
     * Map<String, Object> values = Map.of("ColumnName", false);
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * table.createEntry(values);
     * <p>
     * Note: Ensure that you correctly initialize and connect the `connection` instance to the database before using this method.
     *       Additionally, take care to avoid potential SQL injections by properly sanitizing or handling the values in the database query.
     */
    public CompletableFuture<Void> createEntry(Map<String, Object> values) {
        String[] stringArray = values.values()
                .stream()
                .map(o -> "'" + o + "'")
                .toArray(String[]::new);
        int index = entries.size();
        try {
            connection.executeUpdate("INSERT INTO `" + tableName + "` (" + "`" + String.join("`, `", values.keySet().toArray(new String[]{})) + "`" + ") VALUES (" +  String.join(", ", stringArray) + ")");
            values.forEach((key, value) -> entries.add(new DatabaseEntry(index, value, key)));
        } catch (Exception e) {
            System.err.println("[ERROR] Error while creating entry in table " + tableName + ": " + e);
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Method: removeEntry(Map<String, Object> conditions)
     * <p>
     * Removes an entry from the database table based on the specified columns and values.
     *
     * @param conditions (Map<String, Object>): An map of column names and values to identify the entry to be removed.
     * <p>
     * Example usage:
     * <p>
     * Map<String, Object> conditions = Map.of("ColumnName", false);
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * table.removeEntry(conditions);
     * <p>
     * Note: Ensure that you correctly initialize and connect the `connection` instance to the database before using this method.
     *       Additionally, take care to properly sanitize or handle the values in the database query to prevent SQL injection vulnerabilities.
     */
    public CompletableFuture<Void> removeEntry(Map<String, Object> conditions) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(tableName).append(" WHERE ");

        int index = 0;
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            queryBuilder.append(entry.getKey()).append(" = '").append(entry.getValue()).append("'");
            if (index < conditions.size() - 1) {
                queryBuilder.append(" AND ");
            }
            index++;
        }

        try {
            connection.executeUpdate(queryBuilder.toString());
            for (Map.Entry<String, Object> set : conditions.entrySet()) {
                entries.removeIf(entry -> entry.getValue().equals(set.getValue()) && entry.getColumnName().equals(set.getKey()));
            }
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
