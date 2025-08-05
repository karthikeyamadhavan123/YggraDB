package com.yggra.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DatabaseManager {
    private static volatile DatabaseManager instance;
    public Map<String, Database> databases = new HashMap<>();
    private Database currentDatabase;

    private DatabaseManager() {
        System.out.println("🌌 [COSMIC GATEWAY] The World Tree binds its roots — connection to the realm is forged!");
    }

    //Double-Checked Locking for better performance
    public static DatabaseManager getInstance(){
        if(instance==null){
            synchronized (DatabaseManager.class){
                if(instance==null){
                    instance=new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public void createDatabase(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new RuntimeException("🌀 [VOID WHISPER] The realm name cannot be empty — even chaos needs form!");
        }
        synchronized (this){
            if (databases.containsKey(dbName)) {
                throw new RuntimeException("🔥 [FLAMES OF KRATOS] The realm '" + dbName + "' already exists — the gods do not permit duplicate worlds!");
            }
            databases.put(dbName, new Database(dbName,new ArrayList<>()));
            System.out.println("⚒️ [REALM FORGED] The realm '" + dbName + "' has been created — a new domain awaits your command!");
        }
    }

    public Database useDatabase(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new RuntimeException("🌫️ [MIST OF NIFLHEIM] You must name a realm to enter!");
        }
        synchronized (this){
            if (!databases.containsKey(dbName)) {
                throw new RuntimeException("❌ [REALM UNKNOWN] No such realm '" + dbName + "' exists — the bifrost to that world is shattered!");
            }
            currentDatabase = databases.get(dbName);
            System.out.println("🛡️ [REALM ENTERED] You now tread upon the land of '" + dbName + "' — let the saga unfold!");
            return currentDatabase;
        }

    }

    public void dropDatabase(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new RuntimeException("🌪️ [HOWLING CHAOS] You must name a realm to destroy!");
        }
        synchronized (this){
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

    public void getCurrentDatabase() {
        if (currentDatabase == null) {
            throw new RuntimeException("🌌 [COSMIC CHAOS] No realm is currently bound to your will — invoke 'USE <realm>' to command your world!");
        } else {
            System.out.println("🛡️ [REALM BOUND] You now wield dominion over '" + currentDatabase.getName() + "' — let the saga continue!");
        }
    }

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

}
