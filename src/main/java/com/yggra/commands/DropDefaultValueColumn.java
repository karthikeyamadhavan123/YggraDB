package com.yggra.commands;

/**
 * 🏛️ Command Representation: DROP DEFAULT VALUE
 * Function:
 *   - Encapsulates the parsed SQL instruction:
 *         DROP DEFAULT FOR COLUMN <columnName> IN TABLE <tableName>;
 * Usage:
 *   - Created by the SQL parser when it encounters a DROP DEFAULT statement.
 *   - Passed into the SQLExecutor, which routes it to DatabaseManager.
 * Fields:
 *   - tableName  → The target table whose column's default value should be removed.
 *   - columnName → The specific column inside that table.

 */

public class DropDefaultValueColumn extends SQLCommand {
    public final String tableName;
    public final String columnName;

    /**
     * ⚒️ Constructor: Forge a DROP DEFAULT command
     * Purpose:
     *   - Binds the name of the target table and column at the moment of parsing.
     *   - These values are immutable (final) — once set, they cannot be altered.
     * Parameters:
     *   @param tableName  The table that holds the column whose default value is to be dropped.
     *   @param columnName The column from which the default value will be purged.
     * Lore:
     *   - 🌌 Kratos declares: "Once chosen, the battlefield and victim cannot be changed!"
     */

    public DropDefaultValueColumn(String tableName, String columnName) {
        this.tableName = tableName;   // 🏛️ Lock in the table target
        this.columnName = columnName; // ⚔️ Lock in the column victim
    }

}
