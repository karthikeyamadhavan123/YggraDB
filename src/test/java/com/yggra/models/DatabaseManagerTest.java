package com.yggra.models;

import com.yggra.commands.CreateDatabaseCommand;
import com.yggra.commands.SQLCommand;
import com.yggra.executor.SQLExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    private DatabaseManager dbManager;

    @BeforeEach
    void setUp(){
        dbManager=DatabaseManager.getInstance();
    }
    @Test
    void getInstance() {
        DatabaseManager db1 = DatabaseManager.getInstance();
        DatabaseManager db2=DatabaseManager.getInstance();

        assertSame(db1, db2, "⚡ [THUNDER OF THOR] The Singleton is broken! Two instances of DatabaseManager exist!");

        System.out.println("🛡️ [GATEWAY SECURED] Only one DatabaseManager instance exists: " + (db1 == db2));
    }

    @Test
    void testGetInstance() {
        System.out.println(DatabaseManager.getInstance()!=null);
    }

    @Test
    void testHasCurrentDatabase_WhenNoDatabaseSelected() {
        assertFalse(dbManager.hasCurrentDatabase(),
                "🌌 [VOID OF REALMS] Should return false when no realm is bound!");
    }
    @Test
    void testHasCurrentDatabase_WhenValidDatabaseExists() {
        dbManager.createDatabase("Valhalla");
        dbManager.useDatabase("Valhalla");
        assertTrue(dbManager.hasCurrentDatabase(),
                "🛡️ [REALM BOUND] Should return true for valid bound realms!");
    }
    @Test
    void getTable() {
    }

    @Test
    void createDatabase() {
    }

    @Test
    void useDatabase() {
    }

    @Test
    void dropDatabase() {
    }

    @Test
    void getCurrentDatabase() {
    }

    @Test
    void getAllDatabases() {
    }

    @Test
    void addTable() {
    }

    @Test
    void insertIntoTable() {
    }

    @Test
    void dropTable() {
    }

    @Test
    void showAllTables() {
    }

    @Test
    void testCreateDatabase() {
    }
}