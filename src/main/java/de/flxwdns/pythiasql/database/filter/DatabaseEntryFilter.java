package de.flxwdns.pythiasql.database.filter;

import lombok.Getter;

@Getter
@SuppressWarnings("ALL")
public class DatabaseEntryFilter {
    private Integer id;
    private String columnName;

    private Object value;
    private String column;

    public DatabaseEntryFilter withId(Integer id) {
        this.id = id;
        return this;
    }

    public DatabaseEntryFilter withColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public DatabaseEntryFilter withValue(Object requiedValue) {
        this.value = requiedValue;
        return this;
    }

    public DatabaseEntryFilter withValueAndColumn(Object value, String column) {
        this.value = value;
        this.column = column;
        return this;
    }
}