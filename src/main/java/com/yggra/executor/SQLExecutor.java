package com.yggra.executor;

import com.yggra.commands.*;
import com.yggra.models.DatabaseManager;

public class SQLExecutor {
    public void execute(SQLCommand command) {
        switch (command) {
            case CreateDatabaseCommand createDatabaseCommand ->
                    DatabaseManager.getInstance().createDatabase(createDatabaseCommand.databaseName);
            case UseDatabaseCommand useDatabaseCommand ->
                    DatabaseManager.getInstance().useDatabase(useDatabaseCommand.databaseName);
            case DropDatabaseCommand dropDatabaseCommand ->
                    DatabaseManager.getInstance().dropDatabase(dropDatabaseCommand.databaseName);
            case ShowDatabaseCommand showDatabaseCommand ->
                    DatabaseManager.getInstance().getAllDatabases();
            case GetCurrentDatabaseCommand  getCurrentDatabase->
                DatabaseManager.getInstance().getCurrentDatabase();
            case null, default -> throw new RuntimeException("⚡ [CHAOS UNLEASHED] The command you utter holds no power in these realms — speak a known incantation!");

        }
    }

}
