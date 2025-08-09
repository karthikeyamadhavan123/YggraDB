package com.yggra.commands;

import java.util.List;

public class AlterAddColumnCommand extends SQLCommand{
    public final List<ColumnDefinition> toAddColumns;
    public final String tableName;
    public final List<Object> defaultValue;


    public AlterAddColumnCommand(String tableName, List<ColumnDefinition> toAddColumns, List<Object> defaultValue) {
        this.tableName = tableName;
        this.toAddColumns = toAddColumns;
        this.defaultValue=defaultValue;
    }
}
