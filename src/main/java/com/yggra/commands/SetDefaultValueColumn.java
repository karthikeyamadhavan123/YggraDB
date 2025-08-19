package com.yggra.commands;

/**
 * 📜 Command Object: SET DEFAULT VALUE
 * Purpose:
 * - Represents a parsed SQL command to set a default value on a specific column
 * within a given table.
 * - Encapsulates the table name, column name, and the value to be assigned
 * as the column's new default.
 * Usage:
 * - Forged during parsing when the user invokes:
 * SET COLUMN <columnName> IN TABLE <tableName> DEFAULT <value>;
 */

public class SetDefaultValueColumn extends SQLCommand {
    public final ValueDefinition defaultValue; // 🎯 The default value to be bound
    public final String columnName;            // 🪓 The target column
    public final String tableName;             // 🏛️ The target table

    /**
     * ⚒️ Constructor: Forge a SET DEFAULT VALUE command
     * Purpose:
     * - Locks in the target table, column, and the new default value at parse-time.
     * - These values are immutable (final) — once chosen, the gods forbid change.
     * Parameters:
     *
     * @param tableName    The table that holds the column to receive the new default.
     * @param columnName   The column that shall be gifted with the default value.
     * @param defaultValue The value definition (type + literal) to set as default.
     *                     Lore:
     *                     - ⚔️ Kratos warns: "The column accepts no gifts it cannot bear.
     *                     Choose wisely, mortal, for mistyped runes invite Ragnarok!"
     */

    public SetDefaultValueColumn(String tableName, String columnName, ValueDefinition defaultValue) {
        this.tableName = tableName;       // 🏛️ Lock in the table
        this.columnName = columnName;     // 🪓 Lock in the column
        this.defaultValue = defaultValue; // 🎯 Lock in the value
    }
}
