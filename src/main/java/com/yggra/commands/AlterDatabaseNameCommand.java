package com.yggra.commands;

/**
 * 🌌 [REALM REFORGER]
 * This command carries the divine decree to reshape the identity of a realm.
 * From its old name—etched in the roots of Yggdrasil—to its new destiny,
 * the DatabaseManager shall honor this transformation.
 * Warriors wield this when the winds of fate demand a realm’s rebirth.
 * Once invoked, the old name fades into legend, and the new name shines
 * in the halls of the gods.
 */
public class AlterDatabaseNameCommand extends SQLCommand {
    public final String oldDatabaseName; // 🪶 Name bound in the past
    public final String newDatabaseName; // 🔥 Name that forges the future

    public AlterDatabaseNameCommand(String oldDatabaseName, String newDatabaseName) {
        this.oldDatabaseName = oldDatabaseName;
        this.newDatabaseName = newDatabaseName;
    }
}
