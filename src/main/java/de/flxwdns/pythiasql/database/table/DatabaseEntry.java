package de.flxwdns.pythiasql.database.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseEntry {
    private final int id;
    private Object value;
    private final String columnName;
}
