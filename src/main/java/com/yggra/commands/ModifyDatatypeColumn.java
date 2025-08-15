package com.yggra.commands;

import java.util.List;

/**
 * Represents a parsed SQL command to modify the datatype of one or more columns in a table.
 * Purpose:
 *  - Acts as a data transfer object (DTO) between the SQL parser and the DatabaseManager.
 *  - Stores the table name and a list of columns with their new datatypes/lengths.
 * Fields:
 * tableName Name of the table whose columns will be modified.
 * columns   List of ColumnDefinition objects containing column names,
 *                   new datatypes, and optional lengths.
 * Usage:
 *  - Created by the SQL parser after successfully parsing a "MODIFY DATATYPE" statement.
 *  - Passed to DatabaseManager (via the REPL or command dispatcher) for execution.
 * Example:
 *  SQL: MODIFY COLUMN (strength INT, name VARCHAR(255)) IN TABLE TABLE_NAME;
 */

public class ModifyDatatypeColumn extends SQLCommand {

    /** Name of the target table where the datatype change will occur. */
    public final String tableName;

    /** List of columns (with their new datatypes and lengths) to be modified. */
    public final List<ColumnDefinition> columns;

    /**
     * Constructs a ModifyDatatypeColumn command object.
     *
     * @param tableName Name of the table.
     * @param columns   List of new column definitions.
     */
    public ModifyDatatypeColumn(String tableName, List<ColumnDefinition> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }
}
