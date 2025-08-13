package com.yggra.models;

import com.yggra.commands.ColumnDefinition;
import com.yggra.commands.ValueDefinition;
import com.yggra.parser.TokenType;

import java.util.*;


/**
 * ⚔️ [DATABASE REALM GATEKEEPER] ⚔️
 * The mighty DatabaseManager oversees all realms (databases) in the Yggdrasil system.
 * This singleton class wields the power to create, destroy, and navigate between realms,
 * while enforcing the sacred rules of data integrity.
 * Like Heimdall guarding the Bifrost, it controls access to all database realms.
 */

public class DatabaseManager {
    private static volatile DatabaseManager instance;
    public Map<String, Database> databases = new HashMap<>();
    private Database currentDatabase;

    /**
     * 🌌 [COSMIC CONSTRUCTOR] 🌌
     * Forges the initial connection to the World Tree (database system).
     * Only Odin (the JVM) may invoke this sacred creation ritual.
     */

    private DatabaseManager() {
        System.out.println("🌌 [COSMIC GATEWAY] The World Tree binds its roots — connection to the realm is forged!");
    }

    /**
     * ⚡ [DOUBLE-LOCKED SINGLETON RETRIEVAL] ⚡
     * Retrieves the one true DatabaseManager instance using double-checked locking,
     * ensuring thread-safe access across all Nine Realms.
     *
     * @return The almighty DatabaseManager instance
     */

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    /**
     * 🔍 [REALM BINDING CHECK] 🔍
     * Determines if a realm (database) is currently bound to the warrior's will.
     *
     * @return true if a realm is bound, false otherwise
     */

    public boolean hasCurrentDatabase() {
        return currentDatabase != null && !currentDatabase.getName().isEmpty();
    }

    /**
     * 🏰 [TABLE RETRIEVAL] 🏰
     * Summons a table from the current bound realm.
     *
     * @param tablename Name of the table to retrieve
     * @return The requested Table object
     * @throws RuntimeException if table doesn't exist
     */

    public Table getTable(final String tablename) {
        return currentDatabase.tables.stream().filter(t -> t.tableName.equals(tablename)).findFirst().orElseThrow(() -> new RuntimeException(
                "🔥 [FLAMES OF CONFLICT] Table '" + tablename + "' does not exist!\n" +
                        "🛡️ Forge it first with: CREATE TABLE " + tablename + "!"
        ));
    }

    /**
     * 🌟 [REALM CREATION] 🌟
     * Forges a new database realm in the cosmos of Yggdrasil.
     *
     * @param dbName Name of the new realm to create
     * @throws RuntimeException if name is empty or realm exists You cant create a database inside a database
     */

    public void createDatabase(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new RuntimeException("🌀 [VOID WHISPER] The realm name cannot be empty — even chaos needs form!");
        }
        // Prevent nested names (e.g., "Valhalla.Asgard")
        if (dbName.contains(".")) {
            throw new RuntimeException(
                    "⚡ [BIFROST SHATTERED] Realm names cannot contain '.' — " +
                            "you cannot forge nested realms!"
            );
        }

        synchronized (this) {
            if (databases.containsKey(dbName)) {
                throw new RuntimeException("🔥 [FLAMES OF KRATOS] The realm '" + dbName + "' already exists — the gods do not permit duplicate worlds!");
            }
            databases.put(dbName, new Database(dbName));
            System.out.println("⚒️ [REALM FORGED] The realm '" + dbName + "' has been created — a new domain awaits your command!");
        }
    }

    /**
     * 🌈 [REALM ENTRY] 🌈
     * Crosses the Bifrost to enter a specific database realm.
     * @param dbName Name of the realm to enter
     * @throws RuntimeException if realm doesn't exist
     */

    public void useDatabase(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new RuntimeException("🌫️ [MIST OF NIFLHEIM] You must name a realm to enter!");
        }
        synchronized (this) {
            if (!databases.containsKey(dbName)) {
                throw new RuntimeException("❌ [REALM UNKNOWN] No such realm '" + dbName + "' exists — the bifrost to that world is shattered!");
            }
            currentDatabase = databases.get(dbName);
            System.out.println("🛡️ [REALM ENTERED] You now tread upon the land of '" + dbName + "' — let the saga unfold!");
        }

    }

    /**
     * 💀 [REALM DESTRUCTION] 💀
     * Unleashes Ragnarök upon a database realm, erasing it from existence.
     *
     * @param dbName Name of the realm to destroy
     * @throws RuntimeException if realm doesn't exist
     */

    public void dropDatabase(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new RuntimeException("🌪️ [HOWLING CHAOS] You must name a realm to destroy!");
        }
        synchronized (this) {
            if (!databases.containsKey(dbName)) {
                throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] Cannot strike down '" + dbName + "' — this realm was never born!");
            }
            if (currentDatabase != null && currentDatabase.getName().equals(dbName)) {
                currentDatabase = null;
            }
            databases.remove(dbName);
            System.out.println("💀 [REALM DESTROYED] The realm '" + dbName + "' has been cast into the void — its history erased forever!");

        }
    }

    /**
     * 🔮 [CURRENT REALM DIVINATION] 🔮
     * Reveals the name of the currently bound realm.
     *
     * @return Name of the current database
     * @throws RuntimeException if no realm is bound
     */

    public String getCurrentDatabase() {
        if (currentDatabase == null) {
            throw new RuntimeException("🌌 [COSMIC CHAOS] No realm is currently bound to your will — invoke 'USE <realm>' to command your world!");
        } else {
            return currentDatabase.getName();
        }
    }

    /**
     * 🌍 [REALM CATALOG] 🌍
     * Displays all existing realms in the Yggdrasil system.
     *
     * @throws RuntimeException if no realms exist
     */

    public void getAllDatabases() {
        Set<String> allDatabases = databases.keySet();
        if (allDatabases.isEmpty()) {
            throw new RuntimeException("🌑 [VOID OF REALMS] No realms have yet been forged — summon creation with 'CREATE DATABASE <name>'!");
        }
        System.out.println("🌍 [REALMS IN EXISTENCE] Behold the worlds bound to Yggdrasil:");
        for (String dbName : allDatabases) {
            System.out.println("   ⚔️ " + dbName);
        }
    }


    /**
     * 🌉 [BIFROST RENAME RITUAL] 🌉
     * Reshapes a realm’s destiny by changing its name within the World Tree’s registry.
     * This will:
     * 1️⃣ Ensure you are not standing inside any realm before the renaming begins.
     * 2️⃣ Verify both old and new names are worthy and valid.
     * 3️⃣ Update the cosmic ledger (HashMap key) and the realm’s own soul (Database.name).
     *
     * @param oldName The name of the realm before transformation
     * @param newName The name it shall bear after the Bifrost’s blessing
     */


    public void renameDatabase(String oldName, String newName) {
        // 1. Check if inside a database
        if (hasCurrentDatabase()) {
            throw new RuntimeException(
                    "⚡ [BIFROST LOCKED] You cannot rename realms while inside one!\n" +
                            "🛡️ First exit with: USE NONE;"
            );
        }

        // 2. Validate names
        if (oldName == null || oldName.trim().isEmpty()) {
            throw new RuntimeException(
                    "🌌 [VOID WHISPER] The old realm name is empty!\n" +
                            "⚔️ Speak the name of the realm to be reshaped!"
            );
        }

        if (newName == null || newName.trim().isEmpty()) {
            throw new RuntimeException(
                    "🌀 [FATE UNWRITTEN] The new realm name is empty!\n" +
                            "⚒️ Whisper a name worthy of Yggdrasil's branches!"
            );
        }

        // 3. Thread-safe rename operation
        synchronized (this) {
            if (!databases.containsKey(oldName)) {
                throw new RuntimeException(
                        "❌ [REALM UNKNOWN] No realm named '" + oldName + "' exists!\n" +
                                "🌍 Available realms: " + String.join(", ", databases.keySet())
                );
            }
            // Fetch and remove the old realm from the cosmic ledger
            Database dbToRename = databases.remove(oldName);
            // Update the realm’s own name so it knows its new identity
            dbToRename.setName(newName);
            databases.put(newName, dbToRename);

            // 🎉 Announce the transformation
            System.out.println(
                    "🌠 [YGGDRASIL'S WILL] Realm '" + oldName +
                            "' is now known as '" + newName + "'!"
            );
        }

    }


    /**
     * 🚪 [REALM EXIT] 🚪
     * Closes the Bifrost and returns the warrior to the void between realms.
     * Purpose:
     * - Allows the adventurer to step outside the currently bound database realm.
     * - Sets the `currentDatabase` to null, leaving the warrior unbound.
     * Behavior:
     * - If no realm is currently entered, warns the adventurer that there is no door to walk through.
     * - If a realm is bound, the bond is severed, and the warrior returns to the cosmic gateway (YggraDB prompt).
     * Example:
     * USE NONE;
     * -- You are now outside all realms.
     */


    public void exitDatabase() {
        // 🛡️ Check if warrior is even inside a realm before attempting to leave
        if (!hasCurrentDatabase()) {
            System.out.println("You have to be inside a db to perform this operation");
        }
        // ⚔️ Sever the bond with the current realm
        currentDatabase = null;
    }


    /**
     * 🏗️ [TABLE FORGING] 🏗️
     * Crafts a new table in the current realm.
     *
     * @param tablename Name of the table to create
     * @param columns   List of column definitions
     * @throws RuntimeException if no realm is bound or table exists
     */

    public void addTable(final String tablename, List<ColumnDefinition> columns) {
        try {
            if (!hasCurrentDatabase()) {
                throw new RuntimeException(
                        "🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>"
                );
            }
            boolean tableExists = currentDatabase.tables.stream().anyMatch(table -> table.tableName.equals(tablename));

            if (tableExists) {
                throw new RuntimeException(
                        "🔥 [FLAMES OF CONFLICT] Table '" + tablename + "' already exists!\n" +
                                "🛡️ Choose a name worthy of Valhalla!"
                );

            } else {
                Table table = new Table(tablename, columns);
                currentDatabase.tables.add(table);
                System.out.println("🛠️ [TABLE FORGED] Table '" + tablename + "' rises in " + currentDatabase.getName() + "!");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "⚔️ [RAGE OF THE GODS] " + e.getMessage() + "🏺 The Fates weave a tangled web... Consult the Norns for wisdom!"
            );
        }
    }

    /**
     * ✍️ [RUNIC INSCRIPTION] ✍️
     * Inscribes new data into a table (INSERT operation).
     *
     * @param tableName Target table name
     * @param columns   List of column definitions
     * @param values    List of values to insert
     * @throws RuntimeException for various validation failures
     */

    public void insertIntoTable(String tableName, List<ColumnDefinition> columns, List<ValueDefinition> values) {
        try {
            if (!hasCurrentDatabase()) {
                throw new RuntimeException(
                        "🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>"
                );
            }
            Table table = getTable(tableName);

            if (columns.size() != table.columnList.size()) {
                throw new RuntimeException(
                        "💢 [BOY!] \n" +
                                "Column count mismatch! " + table.columnList.size() + " needed,\n" +
                                "but you gave " + columns.size() + "!\n\n" +
                                "Fix this. Now."
                );
            }

            // 4. Validate column names (order matters)
            List<String> tableColumns = table.columnList.stream()
                    .map(col -> col.columnName)
                    .toList();
            List<String> providedColumns = columns.stream()
                    .map(col -> col.columnName)
                    .toList();
            List<TokenType> typesOfColumn = table.columnList.stream().map(type -> type.type).toList();
            List<Integer> lengths = table.columnList.stream().map(length -> length.length).toList();
            List<String> columnNames = table.columnList.stream().map(column -> column.columnName).toList();


            if (!tableColumns.equals(providedColumns)) {
                throw new RuntimeException(
                        "⚔️ [COLUMN NAMES REJECTED] \n" +
                                "Table columns: " + tableColumns + "\n" +
                                "Your columns : " + providedColumns + "\n\n" +
                                "🛡️ The Allfather demands perfect alignment!"
                );
            }
            if (table.validateRow(values, typesOfColumn, lengths, columnNames) != null) {
                List<Object> returnedValue = table.validateRow(values, typesOfColumn, lengths, columnNames);
                table.addRow(new Row(returnedValue));
                System.out.println(table);
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "⚔️ [RAGE OF THE GODS] The Valkyries deny your INSERT!\n" + e.getMessage() + '\n'
            );
        }
    }

    /**
     * ❌ dropTable – Removes a table from the currently selected database (realm).
     * This function checks if a table with the given name exists in the current database.
     * If found, it deletes the table and updates the database's table list.
     * If no tables remain after deletion, a message is shown.
     * Throws an error if:
     * - No table name is provided
     * - No database is currently in use
     * - The specified table does not exist
     *
     * @param tableName The name of the table to be dropped from the current database.
     */


    public void dropTable(String tableName) {
        try {
            // 🛡️ Null check – no name, no blade

            if (tableName == null) {
                System.out.println("⚠️ [LOST INCANTATION] No table name was provided.");
                return;
            }

            // 🌌 Check if warrior is bound to a realm

            if (!hasCurrentDatabase()) {
                throw new RuntimeException(
                        "🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>"
                );
            }

            // 🔍 Check if table truly exists in the current realm

            boolean tableExists = currentDatabase.tables.stream().anyMatch(table -> table.tableName.equals(tableName));
            if (!tableExists) {
                throw new RuntimeException(
                        "❌ [PHANTOM TABLE] Table '" + tableName + "' does not exist in this realm!\n" +
                                "🧭 Seek it in other lands or summon it anew with CREATE TABLE."
                );
            }

            // ⚔️ Purge the table from the list of known relics

            List<Table> modifiedTableList = currentDatabase.tables.stream().filter(table -> !table.tableName.equals(tableName)).toList();

            // ⚗️ Reforge the list of tables in the current realm
            currentDatabase.tables = new ArrayList<>(modifiedTableList);

            // ☠️ If no relics remain in the realm, warn the summoner
            if (modifiedTableList.isEmpty()) {
                System.out.println("📜 [EMPTY TOMES] All tables have been purged from this realm. Use CREATE TABLE to write new destiny.");
            }
            // ✅ Success message

            System.out.println("🧨 [TABLE VANISHED] Table '" + tableName + "' has been erased from the scrolls of YggraDB.");
        } catch (Exception e) {
            throw new RuntimeException("💥 [CRITICAL ERROR] An error occurred while dropping the table: " + e.getMessage());
        }
    }


    /**
     * ⚔️ [SUMMON THE ARCHIVES] Executes the 'SHOW TABLES' command by unveiling all
     * ancient realms (tables) bound to the current database.
     * 🛡️ If no realm has been chosen (i.e., no database selected), the fates shall halt
     * your journey with a warning.
     * 🔮 If no tables have been forged in this realm, the void shall echo with a call to CREATE.
     *
     * @throws RuntimeException if no realm (database) has been summoned via USE <database_name>.
     */

    public void showAllTables() {
        if (!hasCurrentDatabase()) {
            throw new RuntimeException(
                    "🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>"
            );
        }
        List<Table> allTables = currentDatabase.tables;
        if (currentDatabase.tables.isEmpty()) {
            System.out.println("🪦 [REALM SILENT] No tables dwell in this realm. Forge one with: CREATE TABLE <table_name>");
            return;
        }
        // 📜 [TABLES UNVEILED] Display all mighty constructs forged in this realm
        for (Table table : allTables) {
            System.out.println("🏺 " + table.tableName);
        }
    }

    /**
     * ⚔️ [BLADE OF RENAME] Alters the name of a sacred table within the current realm.
     * Functional Saga:
     * 1. 🔮 Verify that you stand within a bound realm (a database in use).
     * - Without this, the Bifrost cannot bridge your will to the table.
     * 2. 🕵️ Seek the ancient table by its old name among the stones of the realm.
     * - If it is but a phantom, cry out in warning and end the ritual.
     * 3. 🛡️ Traverse the hall of tables:
     * - When the old name is found, shatter it from the annals and forge the new name in its place.
     * - End your march the moment the deed is done — no need to disturb the others.
     * 4. 📜 Declare to the Nine Realms that the renaming has been sealed.
     *
     * @param oldTableName The forsaken name of the table to be replaced.
     * @param newTableName The new name, worthy of the gods.
     * @throws RuntimeException if no realm is bound or the table lies not within this realm.
     */

    public void alterTableName(String oldTableName, String newTableName) {
        // 1. 🔮 Ensure the Bifrost is aligned with a realm
        if (!hasCurrentDatabase()) {
            throw new RuntimeException(
                    "🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>"
            );
        }
        // 2. 🕵️ Search for the ancient table by its old name
        Table table = getTable(oldTableName);

        // The ritual is complete; leave the hall in silence
        table.setTableName(newTableName);
        // 4. 📜 Announce the completion of the renaming ritual
        System.out.println("🏛️ [REALM SHIFT] Table '" + oldTableName + "' has been reborn as '" + newTableName + "'.");
    }

    /**
     * Alters the structure of an existing table by adding new columns to it.
     *
     * @param toAddColumns The list of new columns to forge into the table.
     * @param tableName    The name of the table whose fate is about to change.
     * @param defaultValues default value is null.
     */

    public void alterColumnsofTable(List<ColumnDefinition> toAddColumns, String tableName, List<ValueDefinition> defaultValues) {
        // 1. 🔮 Ensure the Bifrost is aligned with a realm (database is selected)
        if (!hasCurrentDatabase()) {
            throw new RuntimeException(
                    "🌌 [VOID OF REALMS] No database bound to your will! ⚡ " +
                            "First, summon a realm with: USE <database_name>"
            );
        }

        // 2. 🕵️ Seek out the ancient table by name
        Table table = getTable(tableName);
        if (table == null) {
            throw new RuntimeException(
                    "🪨 [TABLE LOST IN THE MISTS] The table '" + tableName + "' could not be found in this realm! " +
                            "Ensure it exists before attempting to alter it."
            );
        }

        // 3. 📜 Ensure columns to add are not empty
        if (toAddColumns == null || toAddColumns.isEmpty()) {
            throw new RuntimeException(
                    "⚠️ [EMPTY OFFERING] No columns were provided for addition. " +
                            "The gods demand at least one new column!"
            );
        }
        // 4. 🏗️ Add each new column to the table
        for (int i = 0; i < toAddColumns.size(); i++) {
            table.addColumnsToExistingTable(toAddColumns.get(i),defaultValues.get(i)!= null ? defaultValues.get(i):null);
            System.out.println("⚒️ [FORGE SUCCESS] Column '" + toAddColumns.get(i).getColumnName() +
                    "' has been bestowed upon table '" + tableName + "'!");
        }
    }

    /**
     * ⚡ Kratos's WRATH: TABLE ANNIHILATION ⚡
     * Like the Ghost of Sparta cleansing the halls of Olympus of all life,
     * this method purges every soul (row) from the chosen table while leaving
     * its structure intact - as Zeus left the empty throne of Olympus.
     * 🔥 DIVINE FURY UNLEASHED:
     * - Validates the realm (database) connection with the fury of a thousand suns
     * - Seeks the target table like Kratos hunting the gods
     * - Obliterates all records with the Blades of Chaos
     * - Displays the empty table as a testament to divine wrath
     *
     * @param tableName The name of the table to be cleansed by divine fire
     * @throws RuntimeException When the realms are not aligned or the table eludes our grasp

     * 💀 "The cycle ends here. We must be better than this." - Kratos 💀
     */

    public void truncateTable(String tableName) {

        // 🔮 STEP I: ENSURE THE BIFROST IS ALIGNED WITH A REALM
        // Just as Kratos cannot channel his rage without a target realm,
        // we cannot cleanse tables without a database connection
        if (!hasCurrentDatabase()) {
            throw new RuntimeException(
                    """
                            ⚡🌊 [WRATH OF THE VOID] ⚡🌊
                            KRATOS ROARS: 'You dare attempt to cleanse the tables without first \
                            choosing your battlefield?!' 🗡️
                            💀 The Ghost of Sparta demands: First bind yourself to a realm with: USE <database_name>
                            🔥 'Face me when you are prepared for war!' - Kratos"""
            );
        }

        // ⚔️ STEP II: HUNT THE TARGET TABLE LIKE A GOD-SLAYER
        // Kratos seeks his prey with unwavering determination
        // The table must exist before it can face annihilation
        Table table = getTable(tableName);

        // 🏛️ STEP III: VALIDATE THE TARGET EXISTS IN THIS REALM
        // Even the God of War cannot destroy what does not exist
        if (table == null) {
            throw new RuntimeException(
                    "🌪️💀 [FURY OF THE LOST HUNT] 🌪️💀\n" +
                            "KRATOS BELLOWS: 'The table '" + tableName + "' hides from my blades like a coward!' ⚔️\n" +
                            "🏛️ This realm holds no such vessel for my wrath to consume!\n" +
                            "🔥 'Show yourself, or be deemed unworthy of destruction!' - Ghost of Sparta\n" +
                            "💡 Ensure the table exists before invoking the cleansing fire!"
            );
        }

        // ⚡ STEP IV: UNLEASH THE BLADES OF CHAOS - TOTAL ANNIHILATION
        // With the fury of a thousand storms, every row falls to Kratos's wrath
        // Like the cleansing of Olympus, none shall survive this divine purge
        table.rowList.clear();

        // 🏆 STEP V: DISPLAY THE AFTERMATH OF DIVINE WRATH
        // Behold the empty table - a monument to the power of the God Slayer
        // The structure remains, but all life has been extinguished
        System.out.println("💀 Kratos speaks: 'The table '" + tableName + "' has been purged of all records.' 💀");
        System.out.println("🏛️ The structure remains, but the souls within have been claimed by Hades...\n");
        System.out.println("\n⚔️ 'Another victory for the Ghost of Sparta.' - Kratos ⚔️");
    }

    /**
     * Drops one or more columns from the specified table in the current database.
     * This method:
     *  1. Ensures a database ("realm") is currently selected.
     *  2. Validates that the target table exists in the current database.
     *  3. Iterates over each column name provided and removes it from the table schema
     *     and all associated data rows.
     *  4. Prints a confirmation message upon successful removal.
     * The behavior is equivalent to multiple "ALTER TABLE <tableName> DROP COLUMN <columnName>" commands.
     * @param deletedColumns List of column names to remove.
     * @param tableName The name of the table from which to drop the columns.
     * @throws RuntimeException if no database is selected or the table does not exist.
     */

    public void dropColumnsofTable(List<String> deletedColumns, String tableName) {
        // STEP I: Ensure a database ("realm") is currently selected
        // Without a target realm, operations cannot proceed.
        if (!hasCurrentDatabase()) {
            throw new RuntimeException(
                    """
                            ⚡🌊 [WRATH OF THE VOID] ⚡🌊
                            KRATOS ROARS: 'You dare attempt to purge the columns without first
                            declaring your battlefield?!' 🗡️
                            💀 The Ghost of Sparta demands: "USE <database_name>" before you strike!
                            🔥 'Face me when you are prepared for war!' - Kratos
                            """
            );
        }

        // STEP II: Retrieve the target table from the current database
        Table table = getTable(tableName);

        // STEP III: Validate that the table exists in this database
        if (table == null) {
            throw new RuntimeException(
                    "🌪️💀 [FURY OF THE LOST HUNT] 🌪️💀\n" +
                            "KRATOS BELLOWS: 'The table \"" + tableName + "\" hides from my blades like a coward!' ⚔️\n" +
                            "🏛️ This realm holds no such vessel for my wrath to consume!\n" +
                            "🔥 'Show yourself, or be deemed unworthy of destruction!' - Ghost of Sparta\n" +
                            "💡 Ensure the table exists before invoking the cleansing fire!"
            );
        }

        // STEP IV: Remove each specified column from the table
        for (String deleteColumnName : deletedColumns) {
            table.removeColumnFromTable(deleteColumnName);
        }

        // STEP V: Confirm success to the player
        System.out.println("🗡️ [COLUMNS VANQUISHED] The following columns have been erased from '"
                + tableName + "': " + deletedColumns);
    }

}

