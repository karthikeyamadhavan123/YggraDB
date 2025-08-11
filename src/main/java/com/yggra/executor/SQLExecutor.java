package com.yggra.executor;

import com.yggra.commands.*;
import com.yggra.models.Database;
import com.yggra.models.DatabaseManager;

/**
 * 🧙‍♂️ [RUNIC INSCRIPTION] 🧙‍♂️
 * The Invoker of Commands — `SQLExecutor` breathes life into the sacred scrolls of YggraDB.
 * Each command passed into this class is examined, interpreted, and executed in accordance with the divine rules of the realm.
 * It stands as the bridge between human intent (SQL) and the realm’s memory (Database).
 */

public class SQLExecutor {
    /**
     * ⚔️ [THE EXECUTION RITUAL] ⚔️
     * Examines the passed SQLCommand and routes its intent to the appropriate keeper function in `DatabaseManager`.
     *
     * @param command The SQLCommand to be executed — forged in parsing, executed in might
     * @throws RuntimeException If the command is unknown or null, it shall be cast into the void
     */

    public void execute(SQLCommand command) {
        switch (command) {

            // 🌍 [CREATE DATABASE] – Forges a new realm in the tree of Yggra

            case CreateDatabaseCommand createDatabaseCommand ->
                    DatabaseManager.getInstance().createDatabase(createDatabaseCommand.databaseName);
            case UseDatabaseCommand useDatabaseCommand -> {
                // 🧭 [USE DATABASE] – Shifts the user’s focus to a new realm of operation
                Database db = DatabaseManager.getInstance().useDatabase(useDatabaseCommand.databaseName);
                // Future logics may bless this context
            }
            // ☠️ [DROP DATABASE] – Obliterates a realm from existence

            case DropDatabaseCommand dropDatabaseCommand ->
                    DatabaseManager.getInstance().dropDatabase(dropDatabaseCommand.databaseName);
            // 📜 [SHOW DATABASES] – Reveals all known realms under Yggra’s dominion

            case ShowDatabaseCommand showDatabaseCommand -> DatabaseManager.getInstance().getAllDatabases();

            // 👁️ [GET CURRENT DATABASE] – Reveals the current realm of execution

            case GetCurrentDatabaseCommand getCurrentDatabase -> DatabaseManager.getInstance().getCurrentDatabase();

            // 🏛️ [CREATE TABLE] – Forges a new schema within the current realm

            case CreateTableCommand createTableCommand ->
                    DatabaseManager.getInstance().addTable(createTableCommand.tableName, createTableCommand.columns);

            // 🍯 [INSERT INTO] – Offers data into the sacred tables of the current realm

            case InsertCommand insertCommand ->
                    DatabaseManager.getInstance().insertIntoTable(insertCommand.tableName, insertCommand.columns, insertCommand.values);

            // ⚔️ [DECREE OF DELETION] Executes the DROP TABLE command to remove a table from the current database realm.

            case DropTableCommand dropTableCommand ->
                    DatabaseManager.getInstance().dropTable(dropTableCommand.tableName);

            // 📜 [REVELATION OF TABLES] Executes the SHOW TABLES command to unveil all tables within the current database realm.
            case ShowTablesCommand showTablesCommand -> DatabaseManager.getInstance().showAllTables();

            // ⚔️ [REALM SHAPING] The gods command a realm to change its name,
            //    forging a new identity within the roots of Yggdrasil.
            case AlterDatabaseNameCommand alterDatabaseNameCommand -> DatabaseManager.getInstance().renameDatabase(
                    alterDatabaseNameCommand.oldDatabaseName,
                    alterDatabaseNameCommand.newDatabaseName
            );

            // 🚪 [BIFROST CLOSED] The warrior steps out from the current realm,
            // returning to the cosmic gateway, unbound to any land.
            case ExitDatabaseCommand exitDatabaseCommand -> DatabaseManager.getInstance().exitDatabase();

            // ⚔️ [EXECUTION OF THE RITUAL]
            // The command scroll has been forged by the parser and now lies in the hands of the Executor.
            // Step 1: Summon the DatabaseManager — the All-Father of realms — through the sacred singleton.
            // Step 2: Pass unto it the old and new table names carried in the scroll.
            // Step 3: The DatabaseManager will wield the Blade of Rename, altering the realm’s annals forever.

            case AlterTableNameCommand alterTableNameCommand ->
                    DatabaseManager.getInstance().alterTableName(alterTableNameCommand.oldTableName, alterTableNameCommand.newTableName);

            // ⚡ When the realms call for the forging of new columns into an ancient table
            case AlterAddColumnCommand alterAddColumnCommand ->
                // Summon the DatabaseManager singleton and channel the power to alter the table:
                //  - toAddColumns  → The blueprints of the new columns to forge
                //  - tableName     → The sacred name of the table to be reforged
                //  - defaultValue  → The divine essence to be placed in every existing row for these new columns
                    DatabaseManager.getInstance().alterColumnsofTable(
                            alterAddColumnCommand.toAddColumns,
                            alterAddColumnCommand.tableName,
                            alterAddColumnCommand.defaultValue
                    );


            // ⚔️ When the scroll bears the TRUNCATE TABLE rune...
            case TruncateTableCommand truncateTableCommand ->
                // 🛡️ Call upon the DatabaseManager to purge the realm,
                // wiping every mortal record from its lands, yet leaving its pillars intact.
                    DatabaseManager.getInstance().truncateTable(truncateTableCommand.tableName);


            // ❌ [UNKNOWN COMMAND] – All invalid or null invocations are smitten
            case null, default ->
                    throw new RuntimeException("⚡ [CHAOS UNLEASHED] The command you utter holds no power in these realms — speak a known incantation!");
        }
    }

}
