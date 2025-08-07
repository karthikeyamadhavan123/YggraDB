package com.yggra.commands;

/**
 * 🔥 [RUNIC INSCRIPTION] 🔥
 * Channels the destructive force of the gods to obliterate an entire realm (DROP DATABASE).
 * Once executed, the database shall be cast into the void — its tables, rows, and knowledge lost to time.
 * This command must be wielded with caution, for its power is absolute.
 */

public class DropDatabaseCommand extends SQLCommand {
    // ☠️ The name of the database to be destroyed — erased from the world-tree of Yggra.
    public final String databaseName;

    /**
     * 💀 [HAMMER OF DELETION] 💀
     * Constructs a DropDatabaseCommand for the specified database, marking it for destruction.
     * @param databaseName The name of the database to be dropped from existence
     */

    public DropDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

}

