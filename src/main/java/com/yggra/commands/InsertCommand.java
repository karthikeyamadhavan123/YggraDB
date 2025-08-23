package com.yggra.commands;

import java.util.List;

/**
 * ⚔️ [RUNIC INSCRIPTION] ⚔️
 * The INSERT INTO command is a ritual offering to Yggra’s memory.
 * A single row is forged, with each value aligned to its destined column.
 * Columns may be given mortal values, divine defaults, or silence (NULL),
 * and the Executor shall judge what truly enters the eternal table.
 */

public class InsertCommand extends SQLCommand {

    // 🏺 The sacred table where the new row shall be etched
    public String tableName;

    // 📜 The chosen columns receiving the offerings
    public List<String> columns;

    // 🔮 The raw offerings themselves — which may be:
    //     - a literal value (e.g., 2, 'Kratos'),
    //     - the eternal void (NULL),
    //     - or the keyword DEFAULT, whose true meaning
    //       shall be unveiled by the Executor at runtime.

    public List<ValueDefinition> values;

    /**
     * 🛠️ [FORGE OF OFFERINGS] 🛠️
     * Forges a new InsertCommand scroll, binding table name,
     * columns, and raw values into a single incantation.
     *
     * @param tableName The name of the table receiving the row
     * @param columns The columns that shall be filled
     * @param values The raw values (literal, NULL, or DEFAULT) awaiting judgment
     */

    public InsertCommand(String tableName, List<String> columns, List<ValueDefinition> values) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
    }

    /**
     * 🔮 [SCRYING OF THE OFFERING] 🔮
     * Reveals the inner soul of this InsertCommand,
     * allowing mortals and gods alike to read
     * which table shall receive the row,
     * which columns are touched,
     * and what raw offerings are made (literal, NULL, or DEFAULT).
     *
     * @return A sacred scroll representation of the InsertCommand
     */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT_INTO_STATEMENT\n");
        sb.append("TABLE_NAME:").append(tableName).append("\n");
        sb.append("COLUMN_DEFINITIONS\n");

        for (String column : columns) {
            sb.append("  COLUMN\n");
            sb.append("    COLUMN_NAME:").append(column).append("\n");
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
