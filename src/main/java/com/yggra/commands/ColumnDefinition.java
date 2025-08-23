package com.yggra.commands;

import com.yggra.parser.TokenType;

/**
 * 🏛️ [RUNIC INSCRIPTION] 🏛️
 * Defines a column's sacred attributes — its name, type, and size — within the schema of a table.
 * This structure is the blueprint for every field of data to be summoned and stored in Yggra’s halls.
 */

public class ColumnDefinition {
    // 📜 The sacred name of the column, as declared by the scribe.
    public String columnName;
    // 🔮 The nature of the column’s essence (INT, VARCHAR, etc.)
    public TokenType type;
    // 🧭 The specified length for VARCHAR columns. For INT, -1 is the law of the realm.
    public int length;

    //checks for column has default value or not default is set to false
    public boolean hasDefaultValue;

    //contains the default value of a column while setting

    public ValueDefinition defaultValue;

    /**
     * 🛠️ [FORGING THE COLUMN] 🛠️
     * Constructs a fully defined column with its name, type, and proper length.
     * The constructor invokes the ancient validation ritual to ensure the column abides by Yggra’s decree.
     *
     * @param columnName The name bestowed upon this column
     * @param type       The datatype rune (INT, VARCHAR, etc.)
     * @param length     The size of the VARCHAR scroll; INTs require -1 by edict
     * @throws IllegalArgumentException If the laws of datatype and length are broken
     */

    public ColumnDefinition(String columnName, TokenType type, int length) {
        this.columnName = columnName;
        this.type = type;
        this.length = length;
        this.hasDefaultValue = false;
        this.defaultValue = null;
        validateColumn();
    }

//    /**
//     * 🪶 [LIGHTWEIGHT VARIANT] 🪶
//     * For INSERT commands, only the column name need be invoked.
//     * The type and length are not inscribed in this instance.
//     *
//     * @param columnName The name of the column to accept new data
//     */
//
//    public ColumnDefinition(String columnName) {
//        this.columnName = columnName;
//    }

    /**
     * Constructs a ColumnDefinition with full metadata, including optional default value.
     * This constructor is typically used when the column definition includes
     * both a type and a default value specification.
     *
     * @param columnName     The name of the column being defined.
     * @param type           The SQL data type of the column (e.g., INT, VARCHAR).
     * @param length         The size constraint for variable-length types
     *                       (e.g., VARCHAR(50)), or -1 if not applicable.
     * @param hasDefaultValue Indicates whether a DEFAULT clause is present for this column.
     * @param defaultValue   The default value definition for the column, if specified.
     *                       Should be null if {@code hasDefaultValue} is false.
     */

    public ColumnDefinition(String columnName, TokenType type, int length, boolean hasDefaultValue, ValueDefinition defaultValue) {
        this.defaultValue = defaultValue;
        this.hasDefaultValue = hasDefaultValue;
        this.length = length;
        this.type = type;
        this.columnName = columnName;
        validateColumn();
    }


    /**
     * Assigns a DEFAULT value to this column.
     * Calling this method marks the column as having a default value
     * and stores the provided {@link ValueDefinition}.
     * @param defaultValue The default value to be set for the column.
     *                     Must match the column's data type.
     */

    public void setDefault(ValueDefinition defaultValue) {
        this.hasDefaultValue = true;
        this.defaultValue = defaultValue;
    }

    /**
     * Removes the DEFAULT clause from this column definition.
     * Calling this method resets the column's default state, meaning
     * no default value will be applied during INSERT operations if
     * the column is omitted.
     */

    public void dropDefault() {
        this.hasDefaultValue = false;
        this.defaultValue = null;
    }

    /**
     * 🪙 [RUNIC RESERVE] 🪙
     * Retrieves the default value rune etched upon this column, if it exists.
     * If no default was decreed by the scribe, null shall be returned.
     * @return The {@link ValueDefinition} bound to this column’s DEFAULT clause,
     *         or null if the column carries no such rune.
     */

    public ValueDefinition getDefaultValue(){
        return this.defaultValue;
    }

    /**
     * ⚖️ [TRIAL OF VALIDATION] ⚖️
     * Ensures that all columns are defined in harmony with Yggra’s rules:
     * - VARCHAR must possess a length worthy of its form (greater than 0).
     * - INT must walk without a length (-1 as default).
     * Violators of this order shall be cast into the runtime void!
     */

    private void validateColumn() {
        if (type == TokenType.VARCHAR) {
            if (length <= 0 || length > 255) {
                throw new IllegalArgumentException(
                        "💀 The gods reject VARCHAR(" + length +
                                "). Choose a length between 1 and 255 or face divine wrath!"
                );
            }

        } else if (type == TokenType.INT) {
            if (length != -1) {
                throw new IllegalArgumentException("⚔️ [UNNECESSARY ATTRIBUTE] INT columns do not wield length. Use -1, as decreed by Yggra!");
            }
        }
    }

    /**
     * 🪞 [MIRROR TO MORTALS] 🪞
     * Returns a string that reflects the column’s essence in SQL form.
     * Used for both debugging and scribe output.
     *
     * @return String representing the column declaration
     */


    @Override
    public String toString() {
        if (type == TokenType.INT) {
            return columnName + " INT";
        } else if (type == TokenType.VARCHAR) {
            return columnName + " VARCHAR(" + length + ")";
        }
        return columnName + " UNKNOWN_TYPE";
    }

    /**
     * 📛 [TRUE NAME OF THE COLUMN] 📛
     * Reveals the current sacred name carried by this column in Yggra’s schema.
     *
     * @return The mortal name bound to this column.
     */

    public String getColumnName() {
        return columnName;
    }


    /**
     * 🔨 [RENAMING RITE] 🔨
     * Alters the sacred name of this column, replacing the old rune with a new one.
     * Typically invoked during ALTER TABLE operations.
     * @param newName The new name bestowed upon the column.
     */

    public void setColumnName(String newName) {
        this.columnName = newName;
    }

    /**
     * 🌀 [ESSENCE REFORGING] 🌀
     * Updates this column’s datatype and reshapes its form according to the decree:
     * - If the type is VARCHAR and the provided length is valid (> 0), the column shall
     *   embody VARCHAR with the given length.
     * - In all other cases, the column is reforged into INT, with -1 marking that
     *   it wields no length restriction.
     * @param type   The new datatype rune (e.g., VARCHAR or INT).
     * @param length The scroll length for VARCHAR; ignored for INT.
     */

    public void setNewDataTypeColumn(TokenType type, int length) {

        // Case 1: A VARCHAR type with a valid length (> 0) — set as VARCHAR with the given size.
        if (type == TokenType.VARCHAR && length > 0) {
            this.type = TokenType.VARCHAR;
            this.length = length;
        }
        // Case 2: All other cases — set as INT with no length restriction (-1 marks "no length").
        else {
            this.type = TokenType.INT;
            this.length = -1;
        }
    }

    /**
     * 🔎 [ESSENCE REVELATION] 🔎
     * Returns the raw datatype rune bound to this column (INT, VARCHAR, etc.).
     * A direct way for scribes or executors to query its true form.
     * @return The {@link TokenType} declaring this column’s datatype.
     */

    public TokenType getType() {
        return this.type;
    }

}
