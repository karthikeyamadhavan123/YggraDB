package com.yggra.commands;


import com.yggra.parser.Token;

import java.util.List;

public class CreateTableCommand extends SQLCommand {
    //Object Structure for returning ast
    public String tableName;
    public List<ColumnDefinition> columns;

    public CreateTableCommand(List<ColumnDefinition> columns, String tableName) {
        this.columns = columns;
        this.tableName = tableName;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE_TABLE_STATEMENT").append("\n").append("TABLE_NAME:").append(tableName).append("\n").append("COLUMN_DEFINITIONS").append("\n");
        for (ColumnDefinition column : columns) {
            sb.append("COLUMN");
            sb.append("\n");
            sb.append("COLUMN_NAME:");
            sb.append(column.value);
            sb.append("\n");
            sb.append("DATA_TYPE:");
            if(column.type.toString().equals("VARCHAR")){
                sb.append("VARCHAR");
                sb.append("(").append(column.length).append(")").append("\n");
            }
            else{
                sb.append("INT");
                sb.append("\n");
            }

        }
        return sb.toString();
    }
}
