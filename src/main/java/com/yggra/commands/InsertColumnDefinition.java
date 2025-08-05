package com.yggra.commands;

public class InsertColumnDefinition {
    public String columnName;

    public InsertColumnDefinition(String columnName) {
        this.columnName = columnName;
    }
    @Override
    public String toString(){
        return columnName;
    }

}
