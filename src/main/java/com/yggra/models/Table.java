package com.yggra.models;

import com.yggra.commands.ColumnDefinition;
import com.yggra.commands.ValueDefinition;
import com.yggra.common_models.Condition;
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
            throw new RuntimeException("❌ [NULL FORGE] Cannot add a column forged from nothingness. " + "Provide a valid column definition!");
        }

        if (defaultValue != null) {
            if (!validateDefaultValue(column, defaultValue)) {
                throw new RuntimeException("⚡ [WRONG POWER] The default value '" + defaultValue + "' defies the laws of Midgard! Match its might to the column's true type.");

            }
        }

        for (ColumnDefinition existingColumn : columnList) {
            if (existingColumn.getColumnName().equalsIgnoreCase(column.getColumnName())) {
                throw new RuntimeException("⚔️ [DUPLICATE NAME] The column '" + column.getColumnName() + "' already stands within the table. " + "Choose a new name or alter the existing one.");
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
            builder.append("| ").append("🕳️  The void consumes this table...".repeat(columnList.size() / 2)).append(" |\n");
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
     * Resolves a list of requested column names into their index positions
     * within the given table's schema.
     * Example:
     * Table schema: [id, name, age]
     * Request: SELECT name,id
     * Output: [1, 0]  (name is column 1, id is column 0)
     * Order is preserved so the user’s SELECT statement is honored exactly.
     *
     * @param columns list of requested column names
     * @param table   table containing the schema (columnList)
     * @return list of indices corresponding to the requested columns
     * @throws RuntimeException if a requested column is missing (defensive check).
     */

    public List<Integer> getIntegers(List<String> columns, Table table) {
        List<Integer> columnIndices = new ArrayList<>();
        for (String column : columns) {
            boolean found = false;

            // Linear scan over table schema (O(n) per lookup).
            // For large schemas, this could be optimized with a hashmap
            // mapping columnName -> index.
            for (int j = 0; j < table.columnList.size(); j++) {
                if (column.equals(table.columnList.get(j).columnName)) {
                    columnIndices.add(j); // capture index
                    found = true;
                    break; // stop once match is found
                }
            }

            // Defensive guard: if column not found, raise descriptive error.
            if (!found) {
                throw new RuntimeException("❌ [COLUMN CURSED] The Fates declare: 'No such column: " + column + "'");
            }
        }
        return columnIndices;
    }

    /**
     * Prints the result set of a SELECT query in ASCII table format.
     * Each column is left-aligned, and column widths are adjusted
     * based on the longest value in that column (including header).
     * Example output for SELECT id, name:
     * | id | name    |
     * +----+---------+
     * | 3  | nitish  |
     * | 1  | karthik |
     * | 2  | veeru   |
     * Null values are printed as the literal string "NULL".
     *
     * @param columns       the column names requested in the SELECT
     * @param table         the table object containing schema + rows
     * @param columnIndices resolved indices for each requested column
     */

    private void printTable(List<String> columns, Table table, List<Integer> columnIndices) {
        // 🔍 Step 1: Compute column widths
        // Each column must be wide enough for both the header and the longest value.
        List<Integer> colWidths = new ArrayList<>();
        for (int idx : columnIndices) {
            int maxWidth = table.columnList.get(idx).columnName.length();

            for (Row row : table.rowList) {
                Object val = row.getValue(idx);
                if (val != null) {
                    maxWidth = Math.max(maxWidth, val.toString().length());
                }
            }

            colWidths.add(maxWidth);
        }

        // 📝 Step 2: Print header row with column names
        StringBuilder header = new StringBuilder("|");
        for (int i = 0; i < columns.size(); i++) {
            header.append(" ").append(String.format("%-" + colWidths.get(i) + "s", columns.get(i))).append(" |");
        }
        System.out.println(header);

        // 🪓 Step 3: Print separator line for readability
        StringBuilder sep = new StringBuilder("+");
        for (int w : colWidths) {
            sep.append("-".repeat(w + 2)).append("+");
        }
        System.out.println(sep);

        // 📊 Step 4: Print each row’s values, aligned by column widths
        for (Row row : table.rowList) {
            StringBuilder rowStr = new StringBuilder("|");
            for (int i = 0; i < columnIndices.size(); i++) {
                Object val = row.getValue(columnIndices.get(i));
                rowStr.append(" ").append(String.format("%-" + colWidths.get(i) + "s", val != null ? val : "NULL")).append(" |");
            }
            System.out.println(rowStr);
        }
    }

    /**
     * ⚒️ [VALUE CONVERSION RITUAL] ⚒️
     * Transmutes a raw ValueDefinition into its destined Java type,
     * ensuring it aligns with the column's declared essence.
     * 📜 Accepted conversions:
     * - INT columns → NUMBER_LITERAL or NULL
     * - VARCHAR columns → STRING_LITERAL or NULL
     * ⚔️ Behavior:
     * - Validates the offered token against the expected column type.
     * - Converts NUMBER_LITERAL → Integer, STRING_LITERAL → String.
     * - Preserves NULL as Java null (absence of value).
     * - Rejects invalid offerings with mythic judgment.
     * 🌌 Outcome:
     * Returns a properly typed Java object (Integer, String, or null).
     *
     * @param valDef     The parsed value definition (from INSERT/DEFAULT/NULL).
     * @param targetType The column’s declared TokenType (INT or VARCHAR).
     * @return The converted Java object, or null if NULL is accepted.
     * @throws RuntimeException if:
     *                          - The value type doesn’t match the column type.
     *                          - A forbidden/unknown target type is encountered.
     *                          - Number conversion fails for INT values.
     */

    private Object convertValue(ValueDefinition valDef, TokenType targetType) {
        try {
            return switch (targetType) {
                case INT -> {
                    if (valDef.type == TokenType.NUMBER_LITERAL) {
                        yield Integer.parseInt(valDef.value);
                    } else if (valDef.type == TokenType.NULL) {
                        yield null;
                    } else {
                        throw new RuntimeException("⚔️ [TYPE JUDGMENT] INT columns accept only numbers or NULL.\n" + "You dared offer: " + valDef.type + " '" + valDef.value + "'");
                    }
                }
                case VARCHAR -> {
                    if (valDef.type == TokenType.STRING_LITERAL) {
                        yield valDef.value;
                    } else if (valDef.type == TokenType.NULL) {
                        yield null;
                    } else {
                        throw new RuntimeException("📜 [RUNIC MISMATCH] VARCHAR columns accept only text or NULL.\n" + "You dared chant: " + valDef.type + " '" + valDef.value + "'");
                    }
                }
                default ->
                        throw new RuntimeException("🌌 [FORBIDDEN KNOWLEDGE] Unknown column type: " + targetType + " — the gods have not inscribed this essence.");
            };
        } catch (NumberFormatException e) {
            throw new RuntimeException("💢 [CONVERSION WRATH] Failed to shape '" + valDef.value + "' into INT.\n" + "The Norns whisper: " + e.getMessage());
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
     * ⚔️ [RITUAL OF ROW VALIDATION] ⚔️
     * Transforms and validates a single row of data before it is etched into Yggra's eternal tables.
     * 🔮 Behavior:
     * - Iterates through each column in the row, aligned by position with the table schema.
     * - Handles special offerings:
     * • DEFAULT → Automatically retrieves the column's default value.
     * • NULL → Preserved as Java null.
     * - Converts raw ValueDefinition tokens into proper Java objects via {@link #convertValue}:
     * • INT → Integer or null
     * • VARCHAR → String or null
     * - Enforces VARCHAR length constraints; overly mighty strings trigger mythic wrath.
     * - Wraps individual column errors with column name and position for precise judgment.
     * 🏛️ Outcome:
     * Returns a list of Java-typed objects representing the row, ready for insertion into the table.
     * ⚡ Exceptions:
     * - Throws RuntimeException if:
     * • A value cannot be converted to its expected type.
     * • A VARCHAR exceeds its defined length.
     * • Any other violation occurs in the sanctum of row validation.
     *
     * @param row         List of parsed {@link ValueDefinition} objects from the INSERT command.
     * @param columnTypes List of {@link TokenType} representing each column's declared type.
     * @param lengths     List of Integer defining max length for VARCHAR columns (-1 for INT).
     * @param columnNames List of column names corresponding to the table schema.
     * @return List of Objects (Integer/String/null) converted and validated for table insertion.
     */

    public List<Object> validateRow(List<ValueDefinition> row, List<TokenType> columnTypes, List<Integer> lengths, List<String> columnNames) {
        // ⚱️ [VESSEL OF TRANSFORMATION] - Prepare the sacred container for converted values
        List<Object> convertedValues = new ArrayList<>();

        // 🌀 [RITUAL PROCESSION] - Walk through each column in the ordained order
        for (int i = 0; i < columnTypes.size(); i++) {
            // 📜 [GATHERING THE SACRED TOKENS] - Extract the elements needed for this column's judgment
            ValueDefinition valDef = row.get(i);        // The raw offering from mortal hands
            TokenType expectedType = columnTypes.get(i);  // The divine type demanded by the schema
            Integer maxLength = lengths.get(i);          // The boundary set by the gods
            String columnName = columnNames.get(i);      // The name by which this column is known
            ColumnDefinition colDef = getColumn(columnName); // The full divine definition

            // 🔱 [FORK IN THE PATH] - Handle DEFAULT tokens with divine intervention
            if (colDef.hasDefaultValue && valDef.type == TokenType.DEFAULT) {
                // 🎭 [DIVINE SUBSTITUTION] - Replace DEFAULT with the column's blessed value
                ValueDefinition defaultValue = colDef.getDefaultValue();
                Object convertedValue = convertValue(defaultValue, expectedType);
                convertedValues.add(convertedValue);
            } else {
                // ⚡ [TRIAL BY FIRE] - Subject the mortal value to divine judgment
                try {
                    // 🔮 [ALCHEMICAL TRANSFORMATION] - Convert the raw value to its destined form
                    Object convertedValue = convertValue(valDef, expectedType);

                    // 📏 [MEASURING THE MIGHTY STRING] - Special judgment for VARCHAR warriors
                    if (expectedType == TokenType.VARCHAR && convertedValue instanceof String strValue) {
                        // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                        if (strValue.length() > maxLength) {
                            throw new RuntimeException("🛡️ [STRING TOO MIGHTY] Column '" + columnName + "' can only hold " + maxLength + " runes\n" + "You wield " + strValue.length() + ": " + (strValue.length() > 20 ? strValue.substring(0, 20) + "..." : strValue));
                        }
                    }

                    // ✅ [BLESSING OF ACCEPTANCE] - The value has proven worthy
                    convertedValues.add(convertedValue);

                } catch (RuntimeException e) {
                    // 🔥 [WRATH OF THE VALIDATORS] - Wrap failure in contextual divine judgment
                    throw new RuntimeException("🔥 [ROW REJECTED] At column '" + columnName + "' (position " + (i + 1) + ")\n" + e.getMessage());
                }
            }
        }

        // 🏆 [TRIUMPHANT RETURN] - Present the sanctified row to the caller
        return convertedValues;
    }

    /**
     * 🌊 [RITUAL OF ROW EXPANSION] 🌊
     * Expands a partial row offering into the full schema required by the divine table.
     * 🔮 Sacred Purpose:
     * When mortals offer only partial tribute (INSERT with subset of columns),
     * this ritual fills the gaps according to divine law and cosmic balance.
     * 🏛️ Divine Process:
     * - Walks through the complete table schema in ordained order
     * - For each column, seeks the mortal's offering in their provided list
     * - If found: Preserves their offering exactly as given
     * - If missing with DEFAULT: Creates a DEFAULT token to invoke column's blessed value
     * - If missing without DEFAULT: Creates a NULL token, accepting the void
     * ⚡ Cosmic Law:
     * The returned row always matches the table schema length and order,
     * ensuring harmony between mortal intent and divine structure.
     * 🎯 Divine Wisdom:
     * This allows mortals to INSERT partial data while maintaining table integrity,
     * letting the gods fill what mortals cannot provide.
     *
     * @param insertColumns List of column names the mortal dares to specify
     * @param insertValues  List of values offered for the specified columns
     * @param schemaColumns The complete divine schema defining table structure
     * @return Expanded row matching full schema order with DEFAULTS/NULLs for missing columns
     */

    public List<ValueDefinition> expandRow(List<String> insertColumns, List<ValueDefinition> insertValues, List<ColumnDefinition> schemaColumns) {
        // 🏺 [VESSEL OF EXPANSION] - Prepare container for the complete row
        List<ValueDefinition> expandedRow = new ArrayList<>();

        // 🚶‍♂️ [PILGRIMAGE THROUGH SCHEMA] - Walk the sacred path of table structure
        for (ColumnDefinition columnDefinition : schemaColumns) {

            // 🔍 [SEEKING THE MORTAL OFFERING] - Search for this column in their tribute
            int idx = insertColumns.indexOf(columnDefinition.getColumnName());

            // 🎭 [THE GREAT DECISION] - Three paths diverge in the divine wood
            if (idx != -1) {
                // 🎯 [PATH OF THE PROVIDED] - Mortal has offered tribute for this column
                expandedRow.add(insertValues.get(idx));

            } else if (columnDefinition.hasDefaultValue) {
                // 🌟 [PATH OF DIVINE DEFAULT] - The column bears blessed default value
                expandedRow.add(new ValueDefinition(TokenType.DEFAULT));

            } else {
                // 🌑 [PATH OF THE VOID] - Neither mortal offering nor divine default exists
                // Accept the null, for even emptiness has its place in the cosmic order
                expandedRow.add(new ValueDefinition(TokenType.NULL, null));
            }
        }

        // 🏆 [COSMIC HARMONY ACHIEVED] - Return the row that satisfies both mortal and divine
        return expandedRow;
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
                throw new RuntimeException("🛡️ [STRING TOO MIGHTY] Column '" + column.columnName + "' can only hold " + column.length + " runes\n" + "You wield " + strValue.length() + ": " + (strValue.length() > 20 ? strValue.substring(0, 20) + "..." : strValue));
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
            if (columnList.stream().anyMatch(columnDefinition -> columnDefinition.columnName.equals(oldName))) {

                // ⚒️ STEP II: Change the column’s identity — rebirth in the fires of war
                columnList.get(i).setColumnName(newName);
                return; //🛡️ STEP III: Mission complete — retreat from battle
            }
        }
        // 💀 If no match is found, the enemy is nowhere to be seen
        throw new RuntimeException("🌪️ [LOST IN THE MISTS] Kratos searches for column '" + oldName + "' but finds only silence in the void!");
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
        Map<String, ColumnDefinition> columnMap = columnList.stream().collect(Collectors.toMap(col -> col.columnName, col -> col));

        // Step 2: Validate all requested modifications BEFORE applying any changes.
        // Ensures atomicity: the operation will fail entirely if even one column doesn't exist.
        for (ColumnDefinition definition : modifiedDataTypesColumn) {
            if (!columnMap.containsKey(definition.columnName)) {
                throw new RuntimeException("⚠️ [BROKEN RUNE] The Valkyries refuse your request!\n" + "🛡️ No such column exists in this realm: '" + definition.columnName + "'\n" + "🌌 The MODIFY COLUMN ritual has been abandoned — no changes applied.");
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
        System.out.println("⚡ [VALHALLA'S BLESSING] All columns transformed successfully!\n" + "🔥 The Norns have rewritten the fate of your table.");
    }

    /**
     * Retrieves the definition of a column by name from the table's column list.
     *
     * @param columnName The name of the column being searched for.
     * @return The matching ColumnDefinition if found, otherwise null.
     * <p>
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
     *
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

    public void validateSelectCommand(Table table, List<String> columns, List<Condition> conditions) {
        if (conditions == null && columns.size() == 1 && columns.getFirst().equals("ALL")) {
            System.out.println(table);
        } else if (conditions == null && !columns.getFirst().equals("ALL")) {
            for (String column : columns) {
                if (table.columnList.stream().noneMatch(c -> c.columnName.equals(column))) {
                    throw new RuntimeException("💥 [COLUMN LOST] Mimir mutters: 'The column '" + column + "' does not exist in table '" + tableName + "'!'");
                }
            }
            // 📜 Step 5: Resolve requested column names into indices
            // Column indices are used internally for quick access into row values.
            // The returned list preserves the order of the requested columns,
            // so SELECT name,id behaves differently from SELECT id,name.
            List<Integer> columnIndices = getIntegers(columns, table);
            // 🖼️ Step 6: Render results in ASCII tabular format
            // Dynamically sizes each column so values and headers align neatly.
            printTable(columns, table, columnIndices);
        } else if (conditions != null && columns.size() == 1 && columns.getFirst().equals("ALL")) {
//             do here the where condition
            TokenType condition = conditions.getFirst().condition;
            String columnName = conditions.getFirst().columnName;//
            ValueDefinition conditionValue = conditions.getFirst().conditionValue;

            if (condition == TokenType.EQUALS) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i) == convertedValue) {
                                    System.out.println(row);
                                }
                            }
                        } else if (table.columnList.get(i).getType() == TokenType.VARCHAR && convertedValue instanceof String strValue) {
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i).equals(convertedValue)) {
                                    System.out.println(row);
                                }
                            }
                        } else {
                            throw new RuntimeException("No other datatype is currently available");
                        }
                    }
                }
            } else if (condition == TokenType.GREATER_THAN) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal > intValue) {
                                        System.out.println(row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            } else if (condition == TokenType.GREATER_THAN_EQUAL) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal >= intValue) {
                                        System.out.println(row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            } else if (condition == TokenType.LESS_THAN_EQUAL) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal <= intValue) {
                                        System.out.println(row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }

                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            } else if (condition == TokenType.LESS_THAN) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal < intValue) {
                                        System.out.println(row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            } else if (condition == TokenType.NOT_EQUALS) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (!rowVal.equals(intValue)) {
                                        System.out.println(row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }

                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            }
        } else if (conditions != null) {
//            for (Condition value : conditions) {
//                TokenType condition = value.condition;
//                String columnName = value.columnName;
//                ValueDefinition conditionValue = value.conditionValue;
//                System.out.println(condition);
//                System.out.println(columnName);
//                System.out.println(conditionValue);
//            } do it when u have multiple conditions for now SELECT name,age FROM user WHERE age=28; I would like this to run.
            List<Integer> columnIndices = getIntegers(columns, table);
            TokenType condition = conditions.getFirst().condition;
            String columnName = conditions.getFirst().columnName;//
            ValueDefinition conditionValue = conditions.getFirst().conditionValue;
            if (condition == TokenType.EQUALS) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i) == convertedValue) {
                                    for (Integer columnIndex : columnIndices) {
                                        System.out.println(row.getValue(columnIndex));
                                    }
                                } else {
                                    System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                }
                            }

                        } else if (table.columnList.get(i).getType() == TokenType.VARCHAR && convertedValue instanceof String strValue) {
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i).equals(convertedValue)) {
                                    for (Integer columnIndex : columnIndices) {
                                        System.out.println(row.getValue(columnIndex));
                                    }
                                } else {
                                    System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                }
                            }
                        }
                    }
                }
            } else if (condition == TokenType.GREATER_THAN) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal > intValue) {
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println(row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }

                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            } else if (condition == TokenType.GREATER_THAN_EQUAL) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal >= intValue) {
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println(row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }

                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            } else if (condition == TokenType.LESS_THAN_EQUAL) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal <= intValue) {
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println(row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }

                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            } else if (condition == TokenType.LESS_THAN) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal < intValue) {
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println(row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            } else if (condition == TokenType.NOT_EQUALS) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            // ⚖️ [SCALES OF JUSTICE] - Does this string exceed its ordained bounds?
                            // get the column index then get all row where the row values are =2;
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (!rowVal.equals(intValue)) {
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println(row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Query returned 0 rows.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("For only INT it is possible");
                        }
                    }
                }
            }
        }
    }


    // Method: getColumnIndexByName(String name)
}
