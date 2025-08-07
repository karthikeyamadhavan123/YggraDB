package com.yggra.models;

import java.util.ArrayList;
import java.util.List;

/**
 *DATABASE CLASS - Handles the complete creation of databases statement parsing
 * Expected format: CREATE DATABASE database_name;
 * Validates database_name and semicolon termination
 * throws RuntimeException for various CREATE DATABASE syntax errors
 */

public class Database {
    public String databaseName;
    public List<Table> tables;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.tables =new ArrayList<>();
    }
    public String getName(){
        return databaseName;
    }

    @Override
    public String toString(){
        return databaseName + " " + (tables.isEmpty() ? "(0 tables)": tables);
    }
}
