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
        return (String) getObject(column);
    }

    public boolean getBoolean(String column) {
        return (Boolean) getObject(column);
    }

    public byte getByte(String column) {
        return (Byte) getObject(column);
    }

    public short getShort(String column) {
        return (Short) getObject(column);
    }

    public int getInt(String column) {
        return (Integer) getObject(column);
    }

    public long getLong(String column) {
        return (Long) getObject(column);
    }

    public float getFloat(String column) {
        return (Float) getObject(column);
    }

    public double getDouble(String column) {
        return (Double) getObject(column);
    }

    public BigDecimal getBigDecimal(String column, int scale) {
        return (BigDecimal) getObject(column);
    }

    public UUID getUUID(String column) {
        return (UUID) getObject(column);
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
        return (Date) getObject(column);
    }

    public Time getTime(String column) {
        return (Time) getObject(column);
    }

    public Timestamp getTimestamp(String column) {
        return (Timestamp) getObject(column);
    }

    public Object getObject(String column) {
        return entries.stream().filter(entry -> entry.getColumnName().equalsIgnoreCase(column)).findFirst().map(DatabaseEntry::getValue).orElse(null);
    }
}
