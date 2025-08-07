package com.yggra.commands;

/**
 * Represents a SQL command to drop a table from the current database.
 * This command is issued when the user types a DROP TABLE statement.
 */
public class DropTableCommand extends SQLCommand {

    /** The name of the table to be dropped */
    public final String tableName;

    /**
     * Constructs a DropTableCommand with the specified table name.
     *
     * @param tableName The name of the table to drop.
     */
    public DropTableCommand(String tableName) {
        this.tableName = tableName;
    }
}
