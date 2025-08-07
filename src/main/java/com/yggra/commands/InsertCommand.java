package com.yggra.commands;

import java.util.List;

/**
 * 🍯 [RUNIC INSCRIPTION] 🍯
 * Offers sacred data into the heart of the table (INSERT INTO).
 * The `InsertCommand` is a vessel that carries new values to be etched into Yggra’s eternal memory.
 * Each value aligns with a column — like warriors matching shield to shield — forming a single row of purpose.
 */

public class InsertCommand extends SQLCommand {

    // 🏺 The table into which data is offered
    public String tableName;

    // 📜 The column scrolls defining where each value must land
    public List<ColumnDefinition> columns;
    // 🔮 The values to be embedded — fresh knowledge for the gods to preserve

    public List<ValueDefinition> values;

    /**
     * 🛠️ [OFFERING CONSTRUCTOR] 🛠️
     * Constructs an insert command with designated columns and matching values.
     * @param tableName The name of the table receiving the data
     * @param columns The columns into which data will flow
     * @param values The values offered for insertion
     */

    public InsertCommand(String tableName, List<ColumnDefinition> columns, List<ValueDefinition> values) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
    }

    /**
     * 📜 [RUNIC REVELATION] 📜
     * Reveals the inner soul of the insert command in human-readable form.
     * Used by scribes and seers to interpret the command before it is invoked.
     * @return A string representation of the command’s structure and payload
     */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT_INTO_STATEMENT\n");
        sb.append("TABLE_NAME:").append(tableName).append("\n");
        sb.append("COLUMN_DEFINITIONS\n");

        for (ColumnDefinition column : columns) {
            sb.append("  COLUMN\n");
            sb.append("    COLUMN_NAME:").append(column.columnName).append("\n");
        }

        sb.append("VALUES\n");
        for (ValueDefinition value : values) {
            sb.append("  VALUE\n");
            sb.append("    TYPE:").append(value.type).append("\n");
            sb.append("    VALUE:").append(value.value).append("\n");
        }

        return sb.toString();
    }
}
