package de.flxwdns.pythiasql.database.table;

import de.flxwdns.pythiasql.database.connect.DatabaseConnectHandler;
import de.flxwdns.pythiasql.database.filter.DatabaseEntryFilter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public final class DatabaseTable {
    @Getter(AccessLevel.NONE)
    private final DatabaseConnectHandler connection;

    private final String tableName;
    private final List<DatabaseEntry> entries;

    public boolean isEntryExists(String[] columns, Object[] values) {
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

    public DatabaseTable createEntry(String[] columns, Object[] values) {
        String[] stringArray = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            stringArray[i] = "'" + values[i].toString() + "'";
        }
        String joinedString = String.join(", ", stringArray);

        System.out.println(joinedString + " " + "`" + String.join("`, `", columns) + "`");
        connection.executeUpdate("INSERT INTO `" + tableName + "` (" + "`" + String.join("`, `", columns) + "`" + ") VALUES (" + joinedString + ")");
        return this;
    }

    public void removeEntry(String[] columns, Object[] values) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(tableName).append(" WHERE ");

        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]).append(" = '").append(values[i].toString()).append("'");

            if (i < columns.length - 1) {
                queryBuilder.append(" AND ");
            }
        }

        String query = queryBuilder.toString();
        connection.executeUpdate(query);
    }

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

    public List<DatabaseEntry> getEntriesById(int id) {
        return entries.stream().filter(it -> it.getId() == id).collect(Collectors.toList());
    }

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
