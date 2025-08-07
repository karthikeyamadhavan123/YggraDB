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

    /**
     * 🛠️ [FORGING THE COLUMN] 🛠️
     * Constructs a fully defined column with its name, type, and proper length.
     * The constructor invokes the ancient validation ritual to ensure the column abides by Yggra’s decree.
     * @param columnName The name bestowed upon this column
     * @param type The datatype rune (INT, VARCHAR, etc.)
     * @param length The size of the VARCHAR scroll; INTs require -1 by edict
     * @throws IllegalArgumentException If the laws of datatype and length are broken
     */

    public ColumnDefinition(String columnName, TokenType type, int length) {
        this.columnName = columnName;
        this.type = type;
        this.length = length;
        validateColumn();
    }


    /**
     * 🪶 [LIGHTWEIGHT VARIANT] 🪶
     * For INSERT commands, only the column name need be invoked.
     * The type and length are not inscribed in this instance.
     * @param columnName The name of the column to accept new data
     */

    public ColumnDefinition(String columnName){
        this.columnName = columnName;
    }

    /**
     * ⚖️ [TRIAL OF VALIDATION] ⚖️
     * Ensures that all columns are defined in harmony with Yggra’s rules:
     * - VARCHAR must possess a length worthy of its form (greater than 0).
     * - INT must walk without a length (-1 as default).
     * Violators of this order shall be cast into the runtime void!
     */

    private void validateColumn(){
        if(type== TokenType.VARCHAR){
            if(length<=0){
                throw new IllegalArgumentException("💀 [FORBIDDEN LENGTH] The gods reject a VARCHAR of length " + length + ". Provide a positive length or be cast into the void!");
            }

        } else if (type==TokenType.INT) {
            if(length!=-1){
                throw new IllegalArgumentException("⚔️ [UNNECESSARY ATTRIBUTE] INT columns do not wield length. Use -1, as decreed by Yggra!");
            }
        }
    }

    /**
     * 🪞 [MIRROR TO MORTALS] 🪞
     * Returns a string that reflects the column’s essence in SQL form.
     * Used for both debugging and scribe output.
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
}
