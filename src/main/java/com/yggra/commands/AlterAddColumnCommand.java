package com.yggra.commands;

import java.util.List;

public class AlterAddColumnCommand extends SQLCommand{
    public final List<ColumnDefinition> toAddColumns;
    public final String tableName;
    public final List<ValueDefinition> defaultValues;


    public AlterAddColumnCommand(String tableName, List<ColumnDefinition> toAddColumns, List<ValueDefinition> defaultValues) {
        this.tableName = tableName;
        this.toAddColumns = toAddColumns;
        this.defaultValues=defaultValues;
    }
}
