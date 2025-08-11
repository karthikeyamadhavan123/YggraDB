package com.yggra.commands;

/**
 * ⚔️ The TruncateTableCommand represents the sacred act of cleansing a realm.
 * In the chronicles of YggraDB, this command wipes every record from the
 * chosen table — leaving its mighty columns standing tall, ready for new tales.
 * This is no mere mortal deletion. It is the reset of an entire realm’s history,
 * called forth by the TRUNCATE TABLE ritual in the SQL scroll.
 * Example invocation in the scroll:
 *   TRUNCATE TABLE Valhalla;
 * The Bifrost of logic shall carry this command to the execution halls,
 * where the realm shall be purged in the blink of Odin’s eye.
 */


public class TruncateTableCommand extends SQLCommand {

    /** 🏰 The name of the realm (table) destined for cleansing. */
    public final String tableName;

    /**
     * ⚡ Summons a TRUNCATE TABLE command into existence.
     *
     * @param tableName The name of the realm to purge of all its inhabitants.
     */
    public TruncateTableCommand(String tableName) {
        this.tableName = tableName;
    }
}
