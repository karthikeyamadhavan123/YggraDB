package com.yggra.commands;



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
        sb.append("CREATE_TABLE_STATEMENT\n");
        sb.append("TABLE_NAME:").append(tableName).append("\n");
        sb.append("COLUMN_DEFINITIONS\n");
        for (ColumnDefinition column : columns) {
            sb.append("  COLUMN\n");
            sb.append("  COLUMN_NAME:").append(column.value).append("\n");
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
