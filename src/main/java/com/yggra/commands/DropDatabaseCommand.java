package com.yggra.commands;

public class DropDatabaseCommand extends SQLCommand {
    public final String databaseName;

    public DropDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

}

