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

    /**
     * 🎯 Retrieves the value stored at the given column index for this row.
     * Functional Details:
     *  - Each row internally stores its data in a `values` list.
     *  - The index provided here must correspond to the index of the column
     *    in the table’s schema (`columnList`).
     *  - This gives direct, O(1) access to the value in the row, making it
     *    efficient for SELECT queries and internal lookups.
     * Usage Example:
     *   If the table schema is [id, name, age] and this row contains [1, "Kratos", 40]:
     *     getValue(0) → 1
     *     getValue(1) → "Kratos"
     *     getValue(2) → 40
     * Edge Cases:
     *  - If the provided index is out of bounds, this will throw an
     *    IndexOutOfBoundsException — the caller is responsible for ensuring
     *    that the index is valid by resolving column names to indices first.
     *  - Can return `null` if the row’s value at that column is missing or unset.
     * @param index The index of the column whose value is being requested.
     * @return The value at the given column index (maybe null).
     */

    public Object getValue(int index) {
        return values.get(index);
    }

}
