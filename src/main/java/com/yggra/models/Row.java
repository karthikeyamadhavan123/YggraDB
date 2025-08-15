package com.yggra.models;

import java.util.List;

/**
 * ⚔️ Represents a single row within the great table of YggraDB.
 * Each row carries the values for every column in its table —
 * much like a warrior bearing the marks of his saga.
 */
public class Row {

    /**
     * 📜 The sacred list of values that this row holds.
     * Each value corresponds to a column in the table's schema.
     * Note:
     *  - The index of each value in this list matches the index of the column in the table's columnList.
     *  - Can store any object type, but it is the table’s duty to ensure
     *    each one matches its column's declared data type.
     */

    public final List<Object> values;

    /**
     * 🏗️ Forges a new Row from the provided values.
     *
     * @param values The initial values to bind into this row.
     */

    public Row(List<Object> values) {
        this.values = values;
    }

    /**
     * 📜 Converts this row into a string representation.
     * Useful for quick inspection of the row’s saga without the full table formatting.
     *
     * @return The list of values as a string.
     */

    @Override
    public String toString() {
        return values.toString();
    }

    /**
     * 🌟 Adds a new value to this row — often used when a new column
     * has been forged into the table and must be granted to all existing rows.
     *
     * @param object The value to append for the new column.
     *               Can be a default, NULL, or any divine essence chosen by the caller.
     */

    public void addDefaultValues(Object object) {
        values.add(object);
    }
}
