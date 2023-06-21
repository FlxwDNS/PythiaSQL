package de.flxwdns.pythiasql.database.table;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatabaseEntry {
    private final int id;
    private final Object value;
    private final String columnName;
}
