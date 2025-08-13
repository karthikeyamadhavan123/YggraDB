package com.yggra.commands;

import java.util.List;

/**
 * Represents a DROP COLUMNS operation in YggraDB.
 * This command is forged during SQL parsing when the user issues a statement like:
 *     REMOVE FROM TABLE <tableName> (<col1>, <col2>, ...);
 * Responsibilities:
 *  - Holds the list of column names destined for removal.
 *  - Holds the target table name.
 *  - Serves as a payload for execution by the DatabaseManager.
 * This is a simple, immutable data container — no execution logic here.
 * Execution happens in the SQLExecutor/command dispatch phase.
 */

public class DropColumnsCommand extends SQLCommand {

    /** The names of the columns to be purged from the table. */
    public final List<String> tobeDeletedColumns;

    /** The name of the table from which columns will be dropped. */
    public final String tableName;

    /**
     * Forges a new DropColumnsCommand with the target table and columns.
     *
     * @param tobeDeletedColumns List of columns to remove.
     * @param tableName Name of the table to modify.
     */
    public DropColumnsCommand(List<String> tobeDeletedColumns, String tableName) {
        this.tobeDeletedColumns = tobeDeletedColumns;
        this.tableName = tableName;
    }
}
