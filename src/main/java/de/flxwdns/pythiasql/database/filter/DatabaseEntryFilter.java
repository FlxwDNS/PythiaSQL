package de.flxwdns.pythiasql.database.filter;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings("unused")
public class DatabaseEntryFilter {
    private Integer id;
    private String columnName;

    private final List<String> columns;
    private final List<Object> values;

    public DatabaseEntryFilter() {
        this.columns = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    /**
     * Method: withId(Integer id)
     * <p>
     * Sets the ID for filtering database entries based on the specified ID.
     *
     * @param id (Integer): The ID to filter by.
     * @return DatabaseEntryFilter: The current DatabaseEntryFilter instance.
     * <p>
     * Example usage:
     * <p>
     * DatabaseEntryFilter filter = new DatabaseEntryFilter();
     * filter.withId(123);
     */
    public DatabaseEntryFilter withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Method: withColumnName(String columnName)
     * <p>
     * Sets the column name for filtering database entries based on the specified column name.
     *
     * @param columnName (String): The column name to filter by.
     * @return DatabaseEntryFilter: The current DatabaseEntryFilter instance.
     * <p>
     * Example usage:
     * <p>
     * DatabaseEntryFilter filter = new DatabaseEntryFilter();
     * filter.withColumnName("name");
     */
    public DatabaseEntryFilter withColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Method: withValue(Object requiredValue)
     * <p>
     * Sets the required value for filtering database entries based on the specified value.
     *
     * @param value (Object): The value to filter by.
     * @return DatabaseEntryFilter: The current DatabaseEntryFilter instance.
     * <p>
     * Example usage:
     * <p>
     * DatabaseEntryFilter filter = new DatabaseEntryFilter();
     * filter.withValue("John");
     */
    public DatabaseEntryFilter withValue(Object value) {
        values.add(value);
        return this;
    }

    /**
     * Method: withValueAndColumn(Object value, String column)
     * <p>
     * Sets both the value and column name for filtering database entries based on the specified value and column name.
     *
     * @param value (Object): The value to filter by.
     * @param column (String): The column name to filter by.
     * @return DatabaseEntryFilter: The current DatabaseEntryFilter instance.
     * <p>
     * Example usage:
     * <p>
     * DatabaseEntryFilter filter = new DatabaseEntryFilter();
     * filter.withValueAndColumn("John", "name");
     */
    public DatabaseEntryFilter withColumnAndValue(String column, Object value) {
        columns.add(column);
        values.add(value);
        return this;
    }
}