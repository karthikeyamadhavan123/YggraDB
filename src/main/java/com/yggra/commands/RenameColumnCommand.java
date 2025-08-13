package com.yggra.commands;

/**
 * Represents a parsed SQL command to rename a column in a table.
 * Example YGGDRASIL:
 *   RENAME COLUMN old_column_name TO new_column_name IN TABLE table_name;
 * This command object holds the details needed by the SQLExecutor
 * to carry out the rename operation on the specified table.
 */
public class RenameColumnCommand extends SQLCommand {

    /** The current/old name of the column to be renamed. */
    public final String oldColumnName;

    /** The name of the table in which the column exists. */
    public final String tableName;

    /** The new name to assign to the column. */
    public final String newName;

    /**
     * Creates a new command for renaming a column in a table.
     *
     * @param oldColumnName the existing column name to rename
     * @param tableName     the name of the table containing the column
     * @param newName       the new column name to assign
     */
    public RenameColumnCommand(String oldColumnName, String tableName, String newName) {
        this.oldColumnName = oldColumnName;
        this.tableName = tableName;
        this.newName = newName;
    }
}
