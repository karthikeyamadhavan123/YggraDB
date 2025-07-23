package com.yggra.commands;

import java.util.List;

public class InsertCommand extends SQLCommand {
    public String tableName;
    public List<InsertColumnDefinition> columns;
    public List<InsertValueDefinition> values;

    public InsertCommand(String tableName, List<InsertColumnDefinition> columns, List<InsertValueDefinition> values) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT_INTO_STATEMENT\n");
        sb.append("TABLE_NAME:").append(tableName).append("\n");
        sb.append("COLUMN_DEFINITIONS\n");

        for (InsertColumnDefinition column : columns) {
            sb.append("  COLUMN\n");
            sb.append("    COLUMN_NAME:").append(column.columnName).append("\n");
        }

        sb.append("VALUES\n");
        for (InsertValueDefinition value : values) {
            sb.append("  VALUE\n");
            sb.append("    TYPE:").append(value.type).append("\n");
            sb.append("    VALUE:").append(value.value).append("\n");
        }

        return sb.toString();
    }
}
