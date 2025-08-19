package com.yggra.models;

import com.yggra.commands.ColumnDefinition;
import com.yggra.commands.ValueDefinition;
import com.yggra.parser.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * ⚔️ [TABLE OF THE GODS] ⚔️
 * A sacred tablet inscribed with column definitions and rows of data.
 * Each Table represents a structured realm where data is stored according to
 * the strict laws of Yggra's database system.
 * Like the great halls of Asgard, it maintains order among its columns and rows.
 */


public class Table {
    public final List<ColumnDefinition> columnList;
    public final List<Row> rowList;
    public String tableName;

    /**
     * 🏗️ [TABLE FORGING] 🏗️
     * Creates a new table with the given name and column definitions.
     *
     * @param tableName  Name of the table (must be unique in its database)
     * @param columnList List of column definitions (the table's sacred structure)
     */

    public Table(String tableName, List<ColumnDefinition> columnList) {
        this.tableName = tableName;
        this.columnList = columnList;
        this.rowList = new ArrayList<>();
    }

    //gets the table name of the current table
    public String getTableName() {
        return tableName;
    }

    //sets the table name on trigger of alter table command
    public void setTableName(String newName) {
        this.tableName = newName;
    }

    private Object getTypeDefault(TokenType type) {
        return switch (type) {
            case INT -> 0;
            case VARCHAR -> "";
            default -> null;
        };
    }

    /**
     * Adds a single new column to an existing table's schema.
     *
     * @param column The new column to be embedded into the table.
     */

    public void addColumnsToExistingTable(ColumnDefinition column, ValueDefinition defaultValue) {
        if (column == null) {
            throw new RuntimeException(
                    "❌ [NULL FORGE] Cannot add a column forged from nothingness. " +
                            "Provide a valid column definition!"
            );
        }

        if (defaultValue != null) {
            if (!validateDefaultValue(column, defaultValue)) {
                throw new RuntimeException(
                        "⚡ [WRONG POWER] The default value '" + defaultValue +
                                "' defies the laws of Midgard! Match its might to the column's true type."
                );

            }
        }

        for (ColumnDefinition existingColumn : columnList) {
            if (existingColumn.getColumnName().equalsIgnoreCase(column.getColumnName())) {
                throw new RuntimeException(
                        "⚔️ [DUPLICATE NAME] The column '" + column.getColumnName() + "' already stands within the table. " +
                                "Choose a new name or alter the existing one."
                );
            }
        }

        //add columns to the existing columnList.
        columnList.add(column);

        // Determine the actual value to insert for existing rows
        Object valueToInsert;
        if (defaultValue != null) {
            valueToInsert = defaultValue.value;
        } else {
            valueToInsert = getTypeDefault(column.getType());
        }
        // Add the value to each existing row
        for (Row row : rowList) {
            row.addDefaultValues(valueToInsert);
        }

    }

    /**
     * 🔮 [TABLE VISUALIZATION] 🔮
     * Returns a beautifully formatted string representation of the table,
     * showing its structure and all contained rows.
     *
     * @return Formatted table display with divine decorations
     */

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Divine Table Header
        builder.append("\n⚔️ ").append(tableName.toUpperCase()).append(" ⚔️\n");

        // Column Names
        builder.append("🛡️ ");
        for (ColumnDefinition col : columnList) {
            builder.append(String.format("| %-15s ", col.columnName));
        }
        builder.append("|\n");

        // Runic Separator
        builder.append("📜").append("+-----------------".repeat(columnList.size()));
        builder.append("+\n");

        // Display Rows with values
        if (rowList.isEmpty()) {
            builder.append("| ").append("🕳️  The void consumes this table...".repeat(columnList.size() / 2))
                    .append(" |\n");
        } else {
            for (Row row : rowList) {
                builder.append("| ");
                for (Object value : row.values) {
                    String displayValue = (value == null || (value instanceof String && ((String) value).isEmpty())) ? "NULL" : value.toString();
                    builder.append(String.format("%-15s | ", displayValue));
                }
                builder.append("\n");
            }
        }

        // Footer
        builder.append("🌊").append("+-----------------".repeat(columnList.size()));
        builder.append("+\n");
        builder.append("Rows: ").append(rowList.size()).append(" | Forged in the fires of Yggdrasil ");

        return builder.toString();
    }

    /**
     * ⚒️ [VALUE CONVERSION] ⚒️
     * Converts a raw ValueDefinition into the proper Java type
     * based on the column's expected data type.
     *
     * @param valDef     The raw value definition from the parser
     * @param targetType The expected TokenType (INT/VARCHAR)
     * @return The converted Java object (Integer/String)
     * @throws RuntimeException if type conversion fails
     */

    private Object convertValue(ValueDefinition valDef, TokenType targetType) {
        try {
            return switch (targetType) {
                case INT -> {
                    if (valDef.type != TokenType.NUMBER_LITERAL) {
                        throw new RuntimeException(
                                "⚔️ [TYPE JUDGMENT] The Allfather demands NUMBER_LITERAL for INT columns!\n" +
                                        "You offered: " + valDef.type + " '" + valDef.value + "'"
                        );
                    }
                    yield Integer.parseInt(valDef.value);
                }
                case VARCHAR -> {
                    if (valDef.type != TokenType.STRING_LITERAL) {
                        throw new RuntimeException(
                                "📜 [RUNIC MISMATCH] The Valkyries require STRING_LITERAL for VARCHAR!\n" +
                                        "You chanted: " + valDef.type + " '" + valDef.value + "'"
                        );
                    }
                    yield valDef.value;
                }
                default -> throw new RuntimeException(
                        "🌌 [FORBIDDEN KNOWLEDGE] The gods know not of type: " + targetType
                );
            };
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "💢 [CONVERSION WRATH] Failed to convert '" + valDef.value + "' to " + targetType +
                            "\nThe Norns whisper: " + e.getMessage()
            );
        }
    }

    /**
     * 📜 [ROW INSCRIPTION] 📜
     * Adds a new row to the table's sacred records.
     *
     * @param row The Row object to add (must match table structure)
     */

    public void addRow(Row row) {
        rowList.add(row);
        System.out.println("⚡ [VALHALLA'S BOUNTY] Row added to table '" + tableName + "'");
    }

    /**
     * 🔍 [ROW VALIDATION] 🔍
     * Validates and converts a list of ValueDefinitions into proper Java objects
     * according to the table's schema.
     *
     * @param row         List of raw value definitions
     * @param columnTypes Expected types for each column
     * @param lengths     Length constraints for VARCHAR columns
     * @param columNames  Column names for error reporting
     * @return List of converted values ready for insertion
     * @throws RuntimeException if validation fails at any column
     */

    public List<Object> validateRow(List<ValueDefinition> row, List<TokenType> columnTypes, List<Integer> lengths, List<String> columNames) {
        List<Object> convertedValues = new ArrayList<>();
        for (int i = 0; i < columnTypes.size(); i++) {
            ValueDefinition valDef = row.get(i); // gets whether it numberLiteral or String and values
            TokenType tokenDef = columnTypes.get(i);
            Integer length = lengths.get(i);
            String columnName = columNames.get(i);
            //INT OR VARCHAR
            try {
                Object convertedValue = convertValue(valDef, tokenDef);
                if (tokenDef == TokenType.VARCHAR && convertedValue instanceof String strValue) {
                    if (strValue.length() > length) {
                        throw new RuntimeException(
                                "🛡️ [STRING TOO MIGHTY] Column '" +
                                        "' can only hold " + length + " runes\n" +
                                        "You wield " + strValue.length() + ": " +
                                        (strValue.length() > 20 ? strValue.substring(0, 20) + "..." : strValue)
                        );
                    }
                }
                convertedValues.add(convertedValue);
            } catch (RuntimeException e) {
                throw new RuntimeException(
                        "🔥 [ROW REJECTED] At column '" + columnName + "' (position " + (i + 1) + ")\n" +
                                e.getMessage()
                );
            }
        }
        return convertedValues;
    }

    /**
     * ⚔️ Tests the might of a column's default value.
     * This function ensures that the given default value is worthy of the column it seeks to inhabit.
     * It calls upon the forge of `convertValue` to reshape the value into its true form,
     * then measures its strength against the column's constraints.
     *
     * @param column The column whose law we must uphold.
     * @param value  The default value offered for judgment.
     * @return true if the value is strong and honorable enough to join the column's ranks.
     * @throws RuntimeException if the value dares exceed the limits set by the column's type.
     */

    public boolean validateDefaultValue(ColumnDefinition column, ValueDefinition value) {
        // 🔮 Transform the offered value into the shape demanded by the column's type
        Object convertedValue = convertValue(value, column.type);

        // 🛡️ If the column demands runes (VARCHAR), test the length of the given inscription
        if (column.type == TokenType.VARCHAR && convertedValue instanceof String strValue) {
            // 📏 If the runes overflow the column's sacred limit, cast the intruder into the void
            if (strValue.length() > column.length) {
                throw new RuntimeException(
                        "🛡️ [STRING TOO MIGHTY] Column '" + column.columnName +
                                "' can only hold " + column.length + " runes\n" +
                                "You wield " + strValue.length() + ": " +
                                (strValue.length() > 20 ? strValue.substring(0, 20) + "..." : strValue)
                );
            }
        }

        // ✅ The value is honorable — let it pass into the column's ranks
        return true;
    }

    /**
     * Removes a column from the table schema and deletes all data associated with it in every row.
     * This method behaves like a SQL "ALTER TABLE <tableName> DROP COLUMN <columnName>" operation.
     * It:
     * 1. Locates the column in the table's schema.
     * 2. Removes the column definition from the schema.
     * 3. Removes the corresponding value from every row in the table to maintain column alignment.
     *
     * @param columnName The name of the column to remove.
     * @throws RuntimeException if the column does not exist in the schema.
     */

    public void removeColumnFromTable(String columnName) {
        // Step 1: Find the index of the column in the schema
        int colIndex = -1;
        for (int i = 0; i < columnList.size(); i++) {
            if (columnList.get(i).columnName.equals(columnName)) {
                colIndex = i;
                break; // Column found, exit loop
            }
        }

        // If column not found, throw an error
        if (colIndex == -1) {
            throw new RuntimeException("Column not found: " + columnName);
        }

        // Step 2: Remove the column definition from the schema
        columnList.remove(colIndex);

        // Step 3: Remove the corresponding value from each row
        for (Row row : rowList) {
            // Each row stores values in positional order matching the schema,
            // so we remove the value at the same index as the removed column.
            row.values.remove(colIndex);
        }
    }

    /**
     * ⚔️ Renames a column within the table’s schema
     * 🛠️ FUNCTIONAL STEPS:
     * 1️⃣ Search for the column in the schema whose name matches `oldName`.
     * 2️⃣ If a match is found, update its name to `newName`.
     * 3️⃣ Does not alter any row data — only the column metadata is changed.
     * ⚠️ NOTE:
     * - If multiple columns have the same name (which ideally should not happen),
     * all matching columns will be renamed.
     * - No validation for reserved keywords or duplicate column names is done here.
     *
     * @param oldName The current name of the column to be changed.
     * @param newName The new name to bestow upon the column.
     */

    public void renameColumnFromTable(String oldName, String newName) {
        for (int i = 0; i < columnList.size(); i++) {
            // 🔍 STEP I: Seek the target column — Kratos hunts his prey
            if (columnList.stream()
                    .anyMatch(columnDefinition -> columnDefinition.columnName.equals(oldName))) {

                // ⚒️ STEP II: Change the column’s identity — rebirth in the fires of war
                columnList.get(i).setColumnName(newName);
                return; //🛡️ STEP III: Mission complete — retreat from battle
            }
        }
        // 💀 If no match is found, the enemy is nowhere to be seen
        throw new RuntimeException(
                "🌪️ [LOST IN THE MISTS] Kratos searches for column '" + oldName +
                        "' but finds only silence in the void!"
        );
    }

    /**
     * Alters the datatypes of one or more existing columns in a table.
     * This method performs the operation in an "all-or-nothing" manner:
     * - If ANY column specified for modification does not exist, the operation is aborted.
     * - If all columns exist, their datatypes are updated.
     * Uses a HashMap for O(n) performance, making it efficient for bulk modifications.
     *
     * @param modifiedDataTypesColumn List of ColumnDefinition objects containing:
     *                                - columnName (String): The name of the column to modify
     *                                - type (TokenType): The new datatype token
     *                                - length (int): Optional length/size for the datatype
     * @throws RuntimeException if any specified column does not exist in the current table schema.
     */

    public void modifyDataTypeColumnsFromTable(List<ColumnDefinition> modifiedDataTypesColumn) {

        // Step 1: Build a fast lookup map (column name → existing column definition)
        // This avoids repeated list scanning and ensures O(1) column existence checks.
        Map<String, ColumnDefinition> columnMap = columnList.stream()
                .collect(Collectors.toMap(col -> col.columnName, col -> col));

        // Step 2: Validate all requested modifications BEFORE applying any changes.
        // Ensures atomicity: the operation will fail entirely if even one column doesn't exist.
        for (ColumnDefinition definition : modifiedDataTypesColumn) {
            if (!columnMap.containsKey(definition.columnName)) {
                throw new RuntimeException(
                        "⚠️ [BROKEN RUNE] The Valkyries refuse your request!\n" +
                                "🛡️ No such column exists in this realm: '" + definition.columnName + "'\n" +
                                "🌌 The MODIFY COLUMN ritual has been abandoned — no changes applied."
                );
            }
        }

        // Step 3: Apply all datatype modifications now that validation has passed.
        // Each column definition in the table is updated with its new type and length.
        for (ColumnDefinition definition : modifiedDataTypesColumn) {
            ColumnDefinition existing = columnMap.get(definition.columnName);
            existing.setNewDataTypeColumn(definition.type, definition.length);
        }

        // Step 4: Announce success in God of War style.
        // No individual column logs are shown here, only a single confirmation message.
        System.out.println(
                "⚡ [VALHALLA'S BLESSING] All columns transformed successfully!\n" +
                        "🔥 The Norns have rewritten the fate of your table."
        );
    }

    /**
     * Retrieves the definition of a column by name from the table's column list.
     *
     * @param columnName The name of the column being searched for.
     * @return The matching ColumnDefinition if found, otherwise null.
     *
     * Saga Note: If null is returned, it means the column is lost in the void of Ginnungagap.
     */

    public ColumnDefinition getColumn(String columnName) {
        // 🔍 Search through all columns in the table
        for (ColumnDefinition columnDefinition : columnList) {
            // ⚔️ If the column name matches, return its definition
            if (columnDefinition.columnName.equals(columnName)) {
                return columnDefinition;
            }
        }
        // 🌑 No such column exists — return null to signal absence
        throw new RuntimeException("🩸 [SYMBOL LOST] Column '" + columnName + "' does not exist in this realm!");
    }

    /**
     * Checks whether a provided default value can be safely cast
     * or converted to the type of the target column.
     * @param defaultValue The value being proposed as the default.
     * @param columnType   The expected type of the column (e.g., INT, VARCHAR).
     * @return true if the value can be converted, false if incompatible.
     * Saga Note: Returns false if the value defies the laws of type judgment,
     * and the Allfather rejects it.
     */

    public boolean checkValueTypes(ValueDefinition defaultValue, TokenType columnType) {
        // 🔮 Attempt to convert the given value to the column's type
        Object convertValue = convertValue(defaultValue, columnType);

        // 🛡️ If conversion succeeds (not null), the type is compatible
        return convertValue != null;
    }

    // Method: getColumnIndexByName(String name)
}
