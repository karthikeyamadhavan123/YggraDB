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
     * @throws RuntimeException if name is empty or realm exists
     */

    public void createDatabase(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new RuntimeException("🌀 [VOID WHISPER] The realm name cannot be empty — even chaos needs form!");
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
     * @return The Database object entered
     * @throws RuntimeException if realm doesn't exist
     */

    public Database useDatabase(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new RuntimeException("🌫️ [MIST OF NIFLHEIM] You must name a realm to enter!");
        }
        synchronized (this) {
            if (!databases.containsKey(dbName)) {
                throw new RuntimeException("❌ [REALM UNKNOWN] No such realm '" + dbName + "' exists — the bifrost to that world is shattered!");
            }
            currentDatabase = databases.get(dbName);
            System.out.println("🛡️ [REALM ENTERED] You now tread upon the land of '" + dbName + "' — let the saga unfold!");
            return currentDatabase;
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
                System.out.println("🌪️ [REALM SEVERED] 'Asgard' was your bound realm — you now drift between worlds!");
                currentDatabase = null;
            }
            databases.remove(dbName);
            System.out.println("💀 [REALM DESTROYED] The realm '" + dbName + "' has been cast into the void — its history erased forever!");

        }
    }

    /**
     * 🔮 [CURRENT REALM DIVINATION] 🔮
     * Reveals the name of the currently bound realm.
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

}
