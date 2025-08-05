package com.yggra.commands;

public class UseDatabaseCommand extends SQLCommand {
    public String databaseName;

    public UseDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }
    //later we will define methods

}
