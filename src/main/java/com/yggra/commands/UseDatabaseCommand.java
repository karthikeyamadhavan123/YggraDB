package com.yggra.commands;


/**
 * 🧭 [RUNIC INSCRIPTION] 🧭
 * The realm-walker’s chant — invokes the gods to shift focus into a chosen database realm (USE DATABASE).
 * This command changes the active domain in which all subsequent commands shall be executed,
 * like stepping through a Bifröst into a new world of tables, records, and purpose.
 */

public class UseDatabaseCommand extends SQLCommand {
    // 🌍 The name of the database realm to traverse into

    public String databaseName;

    /**
     * 🚪 [PORTAL CREATION] 🚪
     * Constructs the command to activate a new realm (database) for all future operations.
     * @param databaseName The chosen realm of execution
     */

    public UseDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    // ✨ More divine behavior shall be etched here in future iterations...

}
