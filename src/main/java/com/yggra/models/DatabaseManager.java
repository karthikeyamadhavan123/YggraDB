package com.yggra.models;

import com.yggra.common_models.Condition;
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
        return currentDatabase.tables.stream().filter(t -> t.tableName.equals(tablename)).findFirst().orElseThrow(() -> new RuntimeException("🔥 [FLAMES OF CONFLICT] Table '" + tablename + "' does not exist!\n" + "🛡️ Forge it first with: CREATE TABLE " + tablename + "!"));
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
            throw new RuntimeException("⚡ [BIFROST SHATTERED] Realm names cannot contain '.' — " + "you cannot forge nested realms!");
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
     *
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
            throw new RuntimeException("⚡ [BIFROST LOCKED] You cannot rename realms while inside one!\n" + "🛡️ First exit with: USE NONE;");
        }

        // 2. Validate names
        if (oldName == null || oldName.trim().isEmpty()) {
            throw new RuntimeException("🌌 [VOID WHISPER] The old realm name is empty!\n" + "⚔️ Speak the name of the realm to be reshaped!");
        }

        if (newName == null || newName.trim().isEmpty()) {
            throw new RuntimeException("🌀 [FATE UNWRITTEN] The new realm name is empty!\n" + "⚒️ Whisper a name worthy of Yggdrasil's branches!");
        }

        // 3. Thread-safe rename operation
        synchronized (this) {
            if (!databases.containsKey(oldName)) {
                throw new RuntimeException("❌ [REALM UNKNOWN] No realm named '" + oldName + "' exists!\n" + "🌍 Available realms: " + String.join(", ", databases.keySet()));
            }
            // Fetch and remove the old realm from the cosmic ledger
            Database dbToRename = databases.remove(oldName);
            // Update the realm’s own name so it knows its new identity
            dbToRename.setName(newName);
            databases.put(newName, dbToRename);

            // 🎉 Announce the transformation
            System.out.println("🌠 [YGGDRASIL'S WILL] Realm '" + oldName + "' is now known as '" + newName + "'!");
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
                throw new RuntimeException("🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>");
            }
            boolean tableExists = currentDatabase.tables.stream().anyMatch(table -> table.tableName.equals(tablename));

            if (tableExists) {
                throw new RuntimeException("🔥 [FLAMES OF CONFLICT] Table '" + tablename + "' already exists!\n" + "🛡️ Choose a name worthy of Valhalla!");

            } else {
                Table table = new Table(tablename, columns);
                currentDatabase.tables.add(table);
                System.out.println("🛠️ [TABLE FORGED] Table '" + tablename + "' rises in " + currentDatabase.getName() + "!");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("⚔️ [RAGE OF THE GODS] " + e.getMessage() + "🏺 The Fates weave a tangled web... Consult the Norns for wisdom!");
        }
    }

    /**
     * ✍️ [RUNIC INSCRIPTION] ✍️
     * Inscribes new data into a table (INSERT operation).
     *
     * @param tableName       Target table name
     * @param providedColumns List of column definitions
     * @param values          List of values to insert
     * @throws RuntimeException for various validation failures
     */

    public void insertIntoTable(String tableName, List<String> providedColumns, List<ValueDefinition> values) {
        try {
            if (!hasCurrentDatabase()) {
                throw new RuntimeException("""
                        🔥 [ODIN'S WRATH] No realm is bound to your command!\s
                        ⚡ First, claim a domain with: USE <database_name>\s
                        The All-Father watches... and finds you wanting.""");
            }
            Table table = getTable(tableName);

            // 🛡️ [SHIELD OF VALHALLA] - Ensuring no null pointers breach our defenses
            if (table.columnList == null || providedColumns == null) {
                throw new RuntimeException("🌑 [GINNUNGAGAP'S VOID] - You offer nothingness where substance is demanded!");
            }
            // Validate column existence
            for (String providedCol : providedColumns) {
                if (table.columnList.stream().noneMatch(col -> col.columnName.equals(providedCol))) {
                    throw new RuntimeException("🗡️  [VALKYRIE'S DENIAL] Column '" + providedCol + "' is not worthy!\n" + "   No such warrior stands among Odin's chosen.\n" + "   Check your runes, mortal.");
                }
            }

            // Check for duplicate columns in the provided list
            Set<String> uniqueColumns = new HashSet<>(providedColumns);
            if (uniqueColumns.size() != providedColumns.size()) {
                throw new RuntimeException("""
                        🔄 [ECHO OF CONFUSION] You speak the same column name twice!
                        Even Loki's tricks cannot make one column hold two values.
                        Remove the duplicate and try again.""");
            }
            // Validate column names (order matters)
            List<String> tableColumns = table.columnList.stream().map(col -> col.columnName).toList();
            List<TokenType> typesOfColumn = table.columnList.stream().map(type -> type.type).toList();
            List<Integer> lengths = table.columnList.stream().map(length -> length.length).toList();

            // Your validation logic here...
            List<ValueDefinition> expandedRowValues = table.expandRow(providedColumns, values, table.columnList);

            List<Object> returnedValue = table.validateRow(expandedRowValues, typesOfColumn, lengths, tableColumns);
            //Actually insert the row into the table storage
            table.addRow(new Row(returnedValue));

        } catch (Exception e) {
            throw new RuntimeException("⚡ [RAGNARÖK'S ECHO] The Valkyries deny your INSERT! \n" + "Mimir says: \"" + e.getMessage() + "\" \n" + "Return when you are worthy, mortal.");
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
                throw new RuntimeException("🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>");
            }

            // 🔍 Check if table truly exists in the current realm

            boolean tableExists = currentDatabase.tables.stream().anyMatch(table -> table.tableName.equals(tableName));
            if (!tableExists) {
                throw new RuntimeException("❌ [PHANTOM TABLE] Table '" + tableName + "' does not exist in this realm!\n" + "🧭 Seek it in other lands or summon it anew with CREATE TABLE.");
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
            throw new RuntimeException("🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>");
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
            throw new RuntimeException("🌌 [VOID OF REALMS] No database bound to your will! ⚡ First, summon a realm with: USE <database_name>");
        }
        // 2. 🕵️ Search for the ancient table by its old name
        Table table = getTable(oldTableName);

        // The ritual is complete; leave the hall in silence
        table.setTableName(newTableName);
        // 4. 📜 Announce the completion of the renaming ritual
        System.out.println("🏛️ [REALM SHIFT] Table '" + oldTableName + "' has been reborn as '" + newTableName + "'.");
    }

    /**
     * Alters the structure of an existing table by adding one or more new columns.
     * This method will:
     * Verify that a database (realm) is currently selected.
     * Locate the target table by name.
     * Validate that at least one column definition is provided.
     * Add each column to the table’s schema in the given order.
     * Populate each existing row with its corresponding default value (if provided),
     * or with the type’s fallback value (e.g., 0 for INT, NULL for VARCHAR).
     * If fewer defaults are provided than columns, the remaining columns will receive
     * fallback values instead of null entries.
     *
     * @param toAddColumns  The ordered list of column definitions to add to the table.
     * @param tableName     The name of the table whose schema is being altered.
     * @param defaultValues A list of default values to assign to each new column’s existing rows.
     *                      May be {@code null} or shorter than the number of columns; missing
     *                      defaults are replaced with safe fallback values.
     * @throws RuntimeException if no database is selected, the table does not exist, or the column list is empty.
     */


    public void alterColumnsofTable(List<ColumnDefinition> toAddColumns, String tableName, List<ValueDefinition> defaultValues) {
        // 1. 🔮 Ensure the Bifrost is aligned with a realm (database is selected)
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("🌌 [VOID OF REALMS] No database bound to your will! ⚡ " + "First, summon a realm with: USE <database_name>");
        }

        // 2. 🕵️ Seek out the ancient table by name
        Table table = getTable(tableName);
        if (table == null) {
            throw new RuntimeException("🪨 [TABLE LOST IN THE MISTS] The table '" + tableName + "' could not be found in this realm! " + "Ensure it exists before attempting to alter it.");
        }

        // 3. 📜 Ensure columns to add are not empty
        if (toAddColumns == null || toAddColumns.isEmpty()) {
            throw new RuntimeException("⚠️ [EMPTY OFFERING] No columns were provided for addition. " + "The gods demand at least one new column!");
        }

        // 4. 🏗️ Add each new column to the table
        for (int i = 0; i < toAddColumns.size(); i++) {
            ValueDefinition givenDefault = null;
            if (defaultValues != null && i < defaultValues.size()) {
                givenDefault = defaultValues.get(i);
            }
            table.addColumnsToExistingTable(toAddColumns.get(i), givenDefault);
            System.out.println("⚒️ [FORGE SUCCESS] Column '" + toAddColumns.get(i).getColumnName() + "' has been bestowed upon table '" + tableName + "'!");
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
     *                          <p>
     *                          💀 "The cycle ends here. We must be better than this." - Kratos 💀
     */

    public void truncateTable(String tableName) {

        // 🔮 STEP I: ENSURE THE BIFROST IS ALIGNED WITH A REALM
        // Just as Kratos cannot channel his rage without a target realm,
        // we cannot cleanse tables without a database connection
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("""
                    ⚡🌊 [WRATH OF THE VOID] ⚡🌊
                    KRATOS ROARS: 'You dare attempt to cleanse the tables without first \
                    choosing your battlefield?!' 🗡️
                    💀 The Ghost of Sparta demands: First bind yourself to a realm with: USE <database_name>
                    🔥 'Face me when you are prepared for war!' - Kratos""");
        }

        // ⚔️ STEP II: HUNT THE TARGET TABLE LIKE A GOD-SLAYER
        // Kratos seeks his prey with unwavering determination
        // The table must exist before it can face annihilation
        Table table = getTable(tableName);

        // 🏛️ STEP III: VALIDATE THE TARGET EXISTS IN THIS REALM
        // Even the God of War cannot destroy what does not exist
        if (table == null) {
            throw new RuntimeException("🌪️💀 [FURY OF THE LOST HUNT] 🌪️💀\n" + "KRATOS BELLOWS: 'The table '" + tableName + "' hides from my blades like a coward!' ⚔️\n" + "🏛️ This realm holds no such vessel for my wrath to consume!\n" + "🔥 'Show yourself, or be deemed unworthy of destruction!' - Ghost of Sparta\n" + "💡 Ensure the table exists before invoking the cleansing fire!");
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
     * 1. Ensures a database ("realm") is currently selected.
     * 2. Validates that the target table exists in the current database.
     * 3. Iterates over each column name provided and removes it from the table schema
     * and all associated data rows.
     * 4. Prints a confirmation message upon successful removal.
     * The behavior is equivalent to multiple "ALTER TABLE <tableName> DROP COLUMN <columnName>" commands.
     *
     * @param deletedColumns List of column names to remove.
     * @param tableName      The name of the table from which to drop the columns.
     * @throws RuntimeException if no database is selected or the table does not exist.
     */

    public void dropColumnsofTable(List<String> deletedColumns, String tableName) {
        // STEP I: Ensure a database ("realm") is currently selected
        // Without a target realm, operations cannot proceed.
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("""
                    ⚡🌊 [WRATH OF THE VOID] ⚡🌊
                    KRATOS ROARS: 'You dare attempt to purge the columns without first
                    declaring your battlefield?!' 🗡️
                    💀 The Ghost of Sparta demands: "USE <database_name>" before you strike!
                    🔥 'Face me when you are prepared for war!' - Kratos
                    """);
        }

        // STEP II: Retrieve the target table from the current database
        Table table = getTable(tableName);

        // STEP III: Validate that the table exists in this database
        if (table == null) {
            throw new RuntimeException("🌪️💀 [FURY OF THE LOST HUNT] 🌪️💀\n" + "KRATOS BELLOWS: 'The table \"" + tableName + "\" hides from my blades like a coward!' ⚔️\n" + "🏛️ This realm holds no such vessel for my wrath to consume!\n" + "🔥 'Show yourself, or be deemed unworthy of destruction!' - Ghost of Sparta\n" + "💡 Ensure the table exists before invoking the cleansing fire!");
        }

        // STEP IV: Remove each specified column from the table
        for (String deleteColumnName : deletedColumns) {
            table.removeColumnFromTable(deleteColumnName);
        }

        // STEP V: Confirm success to the player
        System.out.println("🗡️ [COLUMNS VANQUISHED] The following columns have been erased from '" + tableName + "': " + deletedColumns);
    }

    /**
     * ⚔️ Renames a column in a specified table within the currently selected database ("realm").
     * 🛠️ FUNCTIONAL STEPS:
     * 1️⃣ Validate that a database is currently selected — operations cannot proceed without a battlefield.
     * 2️⃣ Retrieve the specified table from the current database.
     * 3️⃣ Verify that the table exists; if not, summon Kratos’ wrath.
     * 4️⃣ Invoke the table’s column renaming logic to replace `oldName` with `newName`.
     * 5️⃣ Announce the successful transformation.
     *
     * @param oldName   The current column name to be replaced.
     * @param tableName The name of the table containing the column.
     * @param newName   The new name to grant the column.
     * @throws RuntimeException if:
     *                          - No database is currently in use.
     *                          - The target table does not exist in the current database.
     */

    public void renameColumns(String oldName, String tableName, String newName) {
        // STEP I: Ensure a database ("realm") is currently selected
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("""
                    ⚡🌊 [WRATH OF THE VOID] ⚡🌊
                    KRATOS ROARS: 'You dare attempt to purge the columns without first
                    declaring your battlefield?!' 🗡️
                    💀 The Ghost of Sparta demands: "USE <database_name>" before you strike!
                    🔥 'Face me when you are prepared for war!' - Kratos
                    """);
        }

        // STEP II: Retrieve the target table from the current database
        Table table = getTable(tableName);

        // STEP III: Validate that the table exists
        if (table == null) {
            throw new RuntimeException("🌪️💀 [FURY OF THE LOST HUNT] 🌪️💀\n" + "KRATOS BELLOWS: 'The table \"" + tableName + "\" hides from my blades like a coward!' ⚔️\n" + "🏛️ This realm holds no such vessel for my wrath to consume!\n" + "🔥 'Show yourself, or be deemed unworthy of destruction!' - Ghost of Sparta\n" + "💡 Ensure the table exists before invoking the cleansing fire!");
        }

        // STEP IV: Command the table to rename the column
        table.renameColumnFromTable(oldName, newName);

        // STEP V: Announce the victory
        System.out.println("Changed column name from " + oldName + " to " + newName);
    }

    /**
     * Modifies the datatypes of specified columns in a given table.
     * Flow:
     * 1. Ensure a database is currently selected before performing any table operations.
     * 2. Retrieve the target table object from the active database.
     * 3. Validate that the table exists; throw an error if not found.
     * 4. Delegate the datatype modification to the Table class's
     * `modifyDataTypeColumnsFromTable()` method.
     * Behavior:
     * - Throws thematic (God of War–style) RuntimeExceptions if preconditions are not met.
     * - Relies on Table to handle column-level validation and atomic updates.
     *
     * @param tableName Name of the table in which the columns will be modified.
     * @param columns   List of ColumnDefinition objects containing column names,
     *                  new datatypes, and optional lengths.
     */

    public void modifyDataTypecolumns(String tableName, List<ColumnDefinition> columns) {

        // STEP I: Ensure a database ("realm") is currently selected before proceeding.
        // Without an active database context, table-level operations cannot be performed.
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("""
                    ⚡🌊 [WRATH OF THE VOID] ⚡🌊
                    KRATOS ROARS: 'You dare attempt to purge the columns without first
                    declaring your battlefield?!' 🗡️
                    💀 The Ghost of Sparta demands: "USE <database_name>" before you strike!
                    🔥 'Face me when you are prepared for war!' - Kratos
                    """);
        }

        // STEP II: Retrieve the target table from the currently active database.
        Table table = getTable(tableName);

        // STEP III: Validate that the table exists.
        // If no table by this name exists in the current database, abort with thematic error.
        if (table == null) {
            throw new RuntimeException("🌪️💀 [FURY OF THE LOST HUNT] 🌪️💀\n" + "KRATOS BELLOWS: 'The table \"" + tableName + "\" hides from my blades like a coward!' ⚔️\n" + "🏛️ This realm holds no such vessel for my wrath to consume!\n" + "🔥 'Show yourself, or be deemed unworthy of destruction!' - Ghost of Sparta\n" + "💡 Ensure the table exists before invoking the cleansing fire!");
        }

        // STEP IV: Delegate the datatype modification task to the Table object.
        // This ensures all column-level validation and updates are handled at the table level.
        table.modifyDataTypeColumnsFromTable(columns);
    }

    /**
     * Sets a default value for a specific column in a given table.
     *
     * @param tableName    The name of the table where the column resides.
     * @param columnName   The name of the column to assign the default value to.
     * @param defaultValue The default value being declared for the column.
     *                     Saga Flow:
     *                     1. Ensure a database is active.
     *                     2. Retrieve the target table from the current database.
     *                     3. Verify that the table exists.
     *                     4. Retrieve and validate the column.
     *                     5. Perform type compatibility checks.
     *                     6. If all conditions are met, assign the default value.
     */

    public void setDefaultValue(String tableName, String columnName, ValueDefinition defaultValue) {
        // STEP I: Verify that a database context exists.
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("""
                    ⚡🌊 [WRATH OF THE VOID] ⚡🌊
                    KRATOS ROARS: 'You dare forge defaults in the abyss,
                    without first declaring your battlefield?!' 🗡️
                    
                    💀 The Ghost of Sparta DEMANDS:
                    "USE <database_name>" before you strike!
                    
                    🔥 'Face me only when you are prepared for war!' - Kratos
                    """);
        }

        // STEP II: Retrieve the target table from the currently active database.
        Table table = getTable(tableName);

        // STEP III: Validate that the table exists.
        if (table == null) {
            throw new RuntimeException("🌪️💀 [FURY OF THE LOST HUNT] 🌪️💀\n" + "KRATOS BELLOWS: 'The table \"" + tableName + "\" skulks in shadows, " + "fleeing from my blades like a coward!' ⚔️\n" + "🏛️ No such vessel stands in this realm for my wrath to consume!\n" + "🔥 'Reveal yourself, or be deemed unworthy of my destruction!' - Ghost of Sparta");
        }

        // STEP IV: Retrieve the target column definition.
        ColumnDefinition column = table.getColumn(columnName);

        // STEP V: Validate that the column exists.
        if (column == null) {
            throw new RuntimeException("🩸 [SYMBOL LOST] 🩸\n" + "KRATOS SNARLS: 'The column \"" + columnName + "\" is but dust in the wind!'\n" + "⚔️ The Norns decree: No such symbol lives within table \"" + tableName + "\".\n" + "🔥 'Name it true, or suffer the wrath of misremembered fate!' - Kratos");
        }

        // STEP VI: Check type compatibility between value and column.
        if (!table.checkValueTypes(defaultValue, column.type)) {
            throw new RuntimeException("⚔️🔥 [TYPE JUDGMENT] ⚔️🔥\n" + "The Allfather thunders: 'The column \"" + columnName + "\" demands offerings of type " + column.type + "!'\n" + "💀 You dared present: " + defaultValue + "\n" + "🌋 'Foolish mortal — your gift is unworthy!' - Kratos");
        }

        // STEP VII: All conditions met — set the default value.
        column.setDefault(defaultValue);
        System.out.println("🛠️ [DECREE CARVED] Default value bound to column '" + columnName + "' in table '" + tableName + "'!");
    }

    /**
     * ⚔️ DROP DEFAULT VALUE
     * Purpose:
     * - Removes the default value from a given column inside a specified table.
     * - Ensures the database, table, and column all exist before attempting removal.
     * - Validates that the column indeed carries a default before "banishing" it.
     * Usage:
     * - Invoked when the parser encounters:
     * DROP DEFAULT FOR COLUMN <columnName> IN TABLE <tableName>;
     * Lore:
     * - 🌌 Kratos growls: "Defaults are carved runes of fate.
     * To erase one is to unbind destiny itself."
     */

    public void dropDefaultValue(String tableName, String columnName) {
        // 🛡️ Step 1: Ensure a database is selected
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("🌌 [ABYSS OF NOTHINGNESS] Kratos growls: 'You dare strike defaults when no realm is chosen?!' " + "👉 Use `USE <database>` first!");
        }

        // 🏛️ Step 2: Fetch the target table
        Table table = getTable(tableName);
        if (table == null) {
            throw new RuntimeException("🌀 [TABLE VANISHED] The Norns whisper: 'No table named " + tableName + " dwells here!'");
        }

        // 🪓 Step 3: Fetch the target column
        ColumnDefinition column = table.getColumn(columnName);
        if (column == null) {
            throw new RuntimeException("💀 [PHANTOM COLUMN] The Allfather roars: 'Column \"" + columnName + "\" is but an illusion!'");
        }

        // 🔍 Step 4: Ensure the column actually has a default before removing
        if (!column.hasDefaultValue) {
            throw new RuntimeException("⚔️ [NO DEFAULT TO SLAY] Kratos snarls: 'Column \"" + columnName + "\" bears no default rune to shatter!'");
        }

        // 🔥 Step 5: Remove the default value
        column.dropDefault();

        // 🎉 Step 6: Confirm the operation
        System.out.println("🔥 [DEFAULT BANISHED] The default for column '" + columnName + "' has been shattered!");
    }

    /**
     * Executes a SELECT query on a given table inside the currently active database.
     * Supports:
     * - Selecting all columns (via SELECT ALL).
     * - Selecting a subset of columns in any order.
     * - Proper error handling with saga-inspired error messages.
     * - Printing results in a tabular ASCII format.
     *
     * @param tableName the name of the table to fetch rows from
     * @param columns   list of columns requested in the SELECT statement
     * @throws RuntimeException if no database is selected, the table does not exist,or any requested column is missing from the schema.
     */

    public void selectCommand(String tableName, List<String> columns, List<Condition> conditions) {
        // 🛡️ Step 1: Ensure a database is currently active
        // Without a selected database (via USE <dbname>), a SELECT has no context.
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("🌌 [ABYSS OF NOTHINGNESS] Kratos growls: 'You dare strike defaults when no realm is chosen?!' " +
                    "👉 Use `USE <database>` first!");
        }

        // 🏛️ Step 2: Retrieve the target table object
        // If the table name is invalid or does not exist, the SELECT cannot proceed.
        Table table = getTable(tableName);
        if (table == null) {
            throw new RuntimeException("🌀 [TABLE VANISHED] The Norns whisper: 'No table named " + tableName + " dwells here!'");
        }

        // 🌐 Step 3: Handle SELECT ALL
        // If the user writes `SELECT ALL`, we bypass column-specific handling
        // and just print the table directly with its full schema.
        table.validateSelectCommand(table,columns,conditions);

    }

    public void deleteCommand(String tableName,Condition condition){
        if (!hasCurrentDatabase()) {
            throw new RuntimeException("🌌 [ABYSS OF NOTHINGNESS] Kratos growls: 'You dare strike defaults when no realm is chosen?!' " +
                    "👉 Use `USE <database>` first!");
        }

        // 🏛️ Step 2: Retrieve the target table object
        // If the table name is invalid or does not exist, the SELECT cannot proceed.
        Table table = getTable(tableName);
        if (table == null) {
            throw new RuntimeException("🌀 [TABLE VANISHED] The Norns whisper: 'No table named " + tableName + " dwells here!'");
        }
    }

}

