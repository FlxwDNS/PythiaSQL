package de.flxwdns.pythiasql.database.filter;

import lombok.Getter;

@Getter
@SuppressWarnings("ALL")
public class DatabaseEntryFilter {
    private Integer id;
    private String columnName;

    private Object value;
    private String column;

    /**
     * Method: withId(Integer id)
     *
     * Sets the ID for filtering database entries based on the specified ID.
     *
     * @param id (Integer): The ID to filter by.
     * @return DatabaseEntryFilter: The current DatabaseEntryFilter instance.
     *
     * Example usage:
     *
     * DatabaseEntryFilter filter = new DatabaseEntryFilter();
     * filter.withId(123);
     */
    public DatabaseEntryFilter withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Method: withColumnName(String columnName)
     *
     * Sets the column name for filtering database entries based on the specified column name.
     *
     * @param columnName (String): The column name to filter by.
     * @return DatabaseEntryFilter: The current DatabaseEntryFilter instance.
     *
     * Example usage:
     *
     * DatabaseEntryFilter filter = new DatabaseEntryFilter();
     * filter.withColumnName("name");
     */
    public DatabaseEntryFilter withColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Method: withValue(Object requiredValue)
     *
     * Sets the required value for filtering database entries based on the specified value.
     *
     * @param requiredValue (Object): The value to filter by.
     * @return DatabaseEntryFilter: The current DatabaseEntryFilter instance.
     *
     * Example usage:
     *
     * DatabaseEntryFilter filter = new DatabaseEntryFilter();
     * filter.withValue("John");
     */
    public DatabaseEntryFilter withValue(Object requiedValue) {
        this.value = requiedValue;
        return this;
    }

    /**
     * Method: withValueAndColumn(Object value, String column)
     *
     * Sets both the value and column name for filtering database entries based on the specified value and column name.
     *
     * @param value (Object): The value to filter by.
     * @param column (String): The column name to filter by.
     * @return DatabaseEntryFilter: The current DatabaseEntryFilter instance.
     *
     * Example usage:
     *
     * DatabaseEntryFilter filter = new DatabaseEntryFilter();
     * filter.withValueAndColumn("John", "name");
     */
    public DatabaseEntryFilter withValueAndColumn(Object value, String column) {
        this.value = value;
        this.column = column;
        return this;
    }
}