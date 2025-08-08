package com.yggra.commands;

/**
 * ⚔️ [COMMAND SCROLL] Represents the ALTER TABLE RENAME ritual in the language of YggraDB.
 * Functional Saga:
 * - This sacred scroll carries the old name of the table and the new name it shall bear.
 * - Forged by the parser after reading the runes from the user's incantation.
 * - Delivered to the SQLExecutor, which wields its power to rename the table within the realm.
 * Fields:
 * param @oldTableName — The forsaken name, to be struck from the annals of the realm.
 * param @newTableName — The name reborn, etched into eternity.
 * Immutability:
 * - Both names are final, ensuring the scroll's contents cannot be altered after creation.
 */
public class AlterTableNameCommand extends SQLCommand {
    /** 🏛️ The forsaken name of the table */
    public final String oldTableName;

    /** 🌟 The name reborn in the halls of the realm */
    public final String newTableName;

    /**
     * 📜 Forges the command scroll with the old and new table names.
     *
     * @param oldTableName The current name of the table to be renamed.
     * @param newTableName The new name that will replace the old.
     */
    public AlterTableNameCommand(String oldTableName, String newTableName) {
        this.oldTableName = oldTableName;
        this.newTableName = newTableName;
    }
}
