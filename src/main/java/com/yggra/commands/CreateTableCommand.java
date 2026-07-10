package com.yggra.commands;

import java.util.List;

/**
 * 🏗️ [RUNIC INSCRIPTION] 🏗️
 * Enacts the divine ritual to conjure a new table within the chosen database realm.
 * The `CreateTableCommand` carries with it the name of the sacred structure (table),
 * and the scrolls (columns) that define its schema — as inscribed by the All-Father’s will.
 * It is summoned by the parser and executed by the SQLExecutor to bring form to chaos.
 */

public class CreateTableCommand extends SQLCommand {

    // 🏺 The name of the table to be forged in the fires of the database.
    public String tableName;

    // 📜 The sacred scrolls describing each column’s nature and structure.
    public List<ColumnDefinition> columns;


    /**
     * 🔨 [FORGING THE TABLE] 🔨
     * Constructs the CreateTable command scroll with a given name and set of column definitions.
     * @param tableName The name of the table, as carved in runes by the user
     * @param columns The list of column definitions, each representing a field of data in the table
     */

    public CreateTableCommand(String tableName,List<ColumnDefinition> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    /**
     * 🪞 [MIRROR TO MORTALS] 🪞
     * Returns a sacred transcript of the CreateTableCommand — useful for debugging or logging
     * by mortal scribes who walk in the footsteps of the gods.
     * Format:
     *   CREATE_TABLE_STATEMENT
     *   TABLE_NAME:<name>
     *   COLUMN_DEFINITIONS
     *     COLUMN
     *     COLUMN_NAME:<name>
     *     DATA_TYPE:<type>
     *
     * @return A string representing the internal essence of this command
     */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE_TABLE_STATEMENT\n");
        sb.append("TABLE_NAME:").append(tableName).append("\n");
        sb.append("COLUMN_DEFINITIONS\n");
        for (ColumnDefinition column : columns) {
            sb.append("  COLUMN\n");
            sb.append("  COLUMN_NAME:").append(column.columnName).append("\n");
            sb.append("  DATA_TYPE:");
            if(column.type.toString().equals("VARCHAR")){
                sb.append("VARCHAR(").append(column.length).append(")\n");
            }
            else{
                sb.append("INT\n");
            }

        }
        return sb.toString();
    }
}
