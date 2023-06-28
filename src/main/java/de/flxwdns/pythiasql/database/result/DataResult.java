package de.flxwdns.pythiasql.database.result;

import de.flxwdns.pythiasql.database.table.DatabaseEntry;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DataResult {
    private final List<DatabaseEntry> entries;

    public String getString(String column) {
        return String.valueOf(getObject(column));
    }

    public UUID getUUID(String column) {
        if(getObject(column) instanceof String value) {
            return UUID.fromString(value);
        }
        System.err.println("Value is not a valid UUID!");
        return null;
    }

    public boolean getBoolean(String column) {
        if(getObject(column) instanceof Boolean value) {
            return value;
        }
        System.err.println("Value is not a boolean!");
        return false;
    }

    public byte getByte(String column) {
        return (Byte) getObject(column);
    }

    public short getShort(String column) {
        return Short.parseShort(String.valueOf(getObject(column)));
    }

    public int getInt(String column) {
        return Integer.parseInt(String.valueOf(getObject(column)));
    }

    public long getLong(String column) {
        return Long.parseLong(String.valueOf(getObject(column)));
    }

    public float getFloat(String column) {
        return Float.parseFloat(String.valueOf(getObject(column)));
    }

    public double getDouble(String column) {
        return Double.parseDouble(String.valueOf(getObject(column)));
    }

    public BigDecimal getBigDecimal(String column, int scale) {
        if(getObject(column) instanceof BigDecimal value) {
            return value;
        }
        System.err.println("Value is not a bigDecimal!");
        return null;
    }

    /**
     * Method: getBytes(String column)
     * <p>
     * Is deprecated please do not use this method now.
     * <p>
     * The method is currently not implemented.
     */
    @Deprecated
    public byte[] getBytes(String column) {
        return new byte[0];
    }

    public Date getDate(String column) {
        if(getObject(column) instanceof Date value) {
            return value;
        }
        System.err.println("Value is not a data!");
        return null;
    }

    public Time getTime(String column) {
        if(getObject(column) instanceof Time value) {
            return value;
        }
        System.err.println("Value is not a time!");
        return null;
    }

    public Timestamp getTimestamp(String column) {
        if(getObject(column) instanceof Timestamp value) {
            return value;
        }
        System.err.println("Value is not a timestamp!");
        return null;
    }

    public Object getObject(String column) {
        return entries.stream().filter(entry -> entry.getColumnName().equalsIgnoreCase(column)).findFirst().map(DatabaseEntry::getValue).orElse(null);
    }
}
