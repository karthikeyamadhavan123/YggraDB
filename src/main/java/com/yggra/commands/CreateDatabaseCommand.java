package com.yggra.commands;

/**
 * ⚔️ [RUNIC INSCRIPTION] ⚔️
 * Summons the power to forge a new realm of knowledge (CREATE DATABASE operation).
 * This command embodies the will to create a new database within Yggra's domain.
 * Once invoked, it signals the birth of a fresh database — a new plane where tables,
 * rows, and data shall one day dwell. Wielded by the SQLExecutor in the name of the All-Father.
 */
public class CreateDatabaseCommand extends SQLCommand{
    // 📜 The name of the new realm (database) to be forged.
    public String databaseName;

    /**
     * 🛠️ Constructor of Creation 🛠️
     * Carves the database name into the command stone, preparing it for execution by the SQLExecutor.
     * @param databaseName The name of the database to be created, destined to hold future wisdom.
     */

    public CreateDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

}
