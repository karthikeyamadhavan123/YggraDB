package com.yggra.commands;


public class CreateDatabaseCommand extends SQLCommand{
    public String databaseName;

    public CreateDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

}
