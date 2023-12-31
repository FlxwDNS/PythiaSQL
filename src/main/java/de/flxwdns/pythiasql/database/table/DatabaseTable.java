package de.flxwdns.pythiasql.database.table;

import de.flxwdns.pythiasql.database.connect.DatabaseConnectHandler;
import de.flxwdns.pythiasql.database.filter.DatabaseEntryFilter;
import de.flxwdns.pythiasql.database.result.DataResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private final List<String> types;
    private final List<DatabaseEntry> entries;

    /**
     * Method: isEntryExists(Map<String, Object> values)
     * <p>
     * Checks if an entry exists in the database table based on the specified columns and values.
     *
     * @param values (Map<String, Object>): An array of values corresponding to the columns used for identification.
     * @return boolean: True if an entry with the specified columns and values exists, false otherwise.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * Map<String, Object> targetValues = Map.of("uuid", "49d12a56-f1e9-4918-a521-fcd4d7c838b9"); // Specify the values
     * boolean entryExists = table.isEntryExists(targetValues);
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public boolean isEntryExists(Map<String, Object> values) {
        return values.entrySet().stream().anyMatch(set -> entries.stream().anyMatch(entry -> entry.getColumnName().equals(set.getKey()) && entry.getValue().equals(set.getValue())));
    }

    /**
     * Method: ifEntryExists(Map<String, Object> values)
     * <p>
     * Checks if an entry exists in the database table based on the specified columns and values.
     *
     * @param values (Map<String, Object>): An array of values corresponding to the columns used for identification.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * Map<String, Object> targetValues = Map.of("uuid", "49d12a56-f1e9-4918-a521-fcd4d7c838b9"); // Specify the values
     * table.ifEntryExists(targetValues, runnable);
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public void ifEntryExists(Map<String, Object> values, Runnable runnable) {
        if(isEntryExists(values)) {
            runnable.run();
        }
    }

    /**
     * Method: ifEntryExistsOrElse(Map<String, Object> values)
     * <p>
     * Checks if an entry exists in the database table based on the specified columns and values.
     *
     * @param values (Map<String, Object>): An array of values corresponding to the columns used for identification.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * Map<String, Object> targetValues = Map.of("uuid", "49d12a56-f1e9-4918-a521-fcd4d7c838b9"); // Specify the values
     * table.ifEntryExistsOrElse(targetValues, runnable, runnable);
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public void ifEntryExistsOrElse(Map<String, Object> values, Runnable ifPresent, Runnable ifNotPresent) {
        if(isEntryExists(values)) {
            ifPresent.run();
        } else {
            ifNotPresent.run();
        }
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
        try {
            connection.executeUpdate("INSERT INTO `" + tableName + "` (" + "`" + String.join("`, `", values.keySet().toArray(new String[]{})) + "`" + ") VALUES (" +  String.join(", ", stringArray) + ")");

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(" WHERE ");
            int index = 0;
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                queryBuilder.append("`").append(entry.getKey()).append("`='").append(entry.getValue()).append("'");
                if (index < values.size() - 1) {
                    queryBuilder.append(" AND ");
                }
                index++;
            }
            connection.executeQuery("SELECT * FROM " + tableName + queryBuilder, resultSet -> {
                List<DatabaseEntry> tempEntries = new ArrayList<>();
                int[] id = new int[]{0};
                while (resultSet.next()) {
                    types.forEach(it -> {
                        try {
                            entries.add(new DatabaseEntry(id[0], resultSet.getObject(it), it));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    id[0]++;
                }
                return null;
            }, null);

            //values.forEach((key, value) -> entries.add(new DatabaseEntry(index, value, key)));
        } catch (Exception e) {
            System.err.println("[ERROR] Error while creating entry in table " + tableName + ": " + e);
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> editEntry(Map<String, Object> conditions, Map<String, Object> values) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(tableName).append(" SET ");

        int index = 0;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            queryBuilder.append(entry.getKey()).append(" = '").append(entry.getValue()).append("'");
            if (index < values.size() - 1) {
                queryBuilder.append(", ");
            }
            index++;
        }

        queryBuilder.append(" WHERE ");
        index = 0;
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            queryBuilder.append("`").append(entry.getKey()).append("`='").append(entry.getValue()).append("'");
            if (index < conditions.size() - 1) {
                queryBuilder.append(" AND ");
            }
            index++;
        }

        try {
            connection.executeUpdate(queryBuilder.toString());
            for (Map.Entry<String, Object> conditionEntry : conditions.entrySet()) {
                String conditionColumn = conditionEntry.getKey();
                Object conditionValue = conditionEntry.getValue();

                for (DatabaseEntry entry : entries) {
                    if (entry.getValue().equals(conditionValue) && entry.getColumnName().equals(conditionColumn)) {
                        int entryId = entry.getId();

                        for (DatabaseEntry updatedEntry : entries) {
                            if (updatedEntry.getId() == entryId && values.containsKey(updatedEntry.getColumnName())) {
                                updatedEntry.setValue(values.get(updatedEntry.getColumnName()));
                            }
                        }
                    }
                }
            }
            future.complete(null);
        } catch (Exception e) {
            System.err.println("[ERROR] Error while editing entry in table " + tableName + ": " + e);
            e.printStackTrace();
            future.completeExceptionally(e);
        }

        return future;
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
        queryBuilder.append("DELETE FROM `").append(tableName).append("` WHERE ");

        int index = 0;
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            queryBuilder.append("`").append(entry.getKey()).append("`='").append(entry.getValue()).append("'");
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
     * Method: firstAsResult()
     * <p>
     * Retrieves the first occurrence of a DatabaseEntry from the entries list.
     *
     * @return DataResult: A data result containing the first occurrence of a DatabaseEntry.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * DataResult dataResult = table.firstAsResult();
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public DataResult firstAsResult() {
        List<DatabaseEntry> tempList = new ArrayList<>();
        entries.stream().findFirst().ifPresent(tempList::add);

        entries.forEach(entry -> {
            if(tempList.stream().anyMatch(it -> it.getId() == entry.getId()) && entry != tempList.stream().findFirst().get()) {
                tempList.add(entry);
            }
        });
        return new DataResult(tempList);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Method: allAsResult()
     * <p>
     * Retrieves the first occurrence of a DatabaseEntry from the entries list.
     *
     * @return List<DataResult>: A data result list containing the first occurrence of a DatabaseEntry.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * List<DataResult> dataResults = table.allAsResult();
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public List<DataResult> allAsResult() {
        Map<Integer, List<DatabaseEntry>> groupedEntries = new HashMap<>();

        for (DatabaseEntry entry : entries) {
            int id = entry.getId();
            groupedEntries.computeIfAbsent(id, k -> new ArrayList<>()).add(entry);
        }

        List<DataResult> resultList = new ArrayList<>();
        for (List<DatabaseEntry> group : groupedEntries.values()) {
            resultList.add(new DataResult(group));
        }
        return resultList;
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
     * @param values (Map<String, Object> values): The filter used to specify the criteria for filtering the entries.
     * @return DatabaseTable: A new DatabaseTable instance containing the filtered entries.
     * <p>
     * Example usage:
     * <p>
     * DatabaseTable table = new DatabaseTable(); // Example instance of the database table
     * DatabaseTable filteredTable = table.filter(Map.of("", ""));
     *
     * Note: The behavior of this method assumes that the `entries` list has been populated with DatabaseEntry objects prior to calling this method.
     */
    public DatabaseTable filter(Map<String, Object> values) {
        List<DatabaseEntry> filteredEntries = new ArrayList<>();
        Map<Integer, Boolean> idFilterMap = new HashMap<>();

        for (DatabaseEntry entry : entries) {
            int entryId = entry.getId();

            // Überprüfen, ob die ID bereits gefiltert wurde
            if (idFilterMap.containsKey(entryId) && !idFilterMap.get(entryId)) {
                continue;
            }

            if (matchesFilter(entry, values)) {
                //filteredEntries.add(entry);
                idFilterMap.put(entryId, true);
            } else {
                idFilterMap.put(entryId, false);
            }
        }

        entries.forEach(it -> {
            if(idFilterMap.get(it.getId())) {
                filteredEntries.add(it);
            }
        });

        return new DatabaseTable(connection, tableName, types, filteredEntries);
    }



    /**
     * Method: getFirstValue(String column)
     * <p>
     * Is deprecated please do not use this method.
     * <p>
     * Use now firstAsResult()
     */
    @Deprecated
    public Object getFirstValue(String column) {
        return entries.stream().filter(entry -> entry.getColumnName().equalsIgnoreCase(column)).findFirst().map(DatabaseEntry::getValue).orElse(null);
    }

    private boolean matchesFilter(DatabaseEntry entry, Map<String, Object> values) {
        for (Map.Entry<String, Object> filterEntry : values.entrySet()) {
            String columnName = filterEntry.getKey();
            Object filterValue = filterEntry.getValue();

            if (columnName.equals(entry.getColumnName()) && !filterValue.equals(entry.getValue())) {
                return false; // Eintrag erfüllt nicht die Filterbedingung für diese Spalte
            }
        }
        return true; // Eintrag erfüllt alle Filterbedingungen oder Spalte ist nicht im Filter enthalten
    }
}
