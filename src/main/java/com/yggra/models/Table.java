package com.yggra.models;

import com.yggra.commands.ColumnDefinition;
import com.yggra.commands.ValueDefinition;
import com.yggra.common_models.Condition;
import com.yggra.parser.TokenType;

import java.util.*;
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

    /**
     * 🔍 Validates and executes a SELECT command on the specified table.
     * 📝 Supported syntax patterns:
     * - SELECT ALL FROM table;                    → Returns all columns, all rows
     * - SELECT col1, col2 FROM table;            → Returns specific columns, all rows
     * - SELECT ALL FROM table WHERE condition;   → Returns all columns, filtered rows
     * - SELECT col1, col2 FROM table WHERE condition; → Returns specific columns, filtered rows
     * ⚙️ Execution flow:
     * 1. 🎯 Determines query type (ALL vs specific columns, with/without WHERE)
     * 2. ✅ Validates column names exist in the table schema
     * 3. 🔍 Applies WHERE condition filtering (supports =, >, >=, <, <=, !=)
     * 4. 🖼️ Renders results in formatted output
     *
     * @param table      The target table object containing schema and row data
     * @param columns    List of column names to select, or ["ALL"] for all columns
     * @param conditions List of WHERE conditions to filter rows (null if no WHERE clause)
     * @throws RuntimeException if:
     *                          ❌ Column name does not exist in table schema
     *                          ❌ Data type mismatch in WHERE condition
     *                          ❌ Unsupported comparison operator for given data type
     *                          🎭 Supported operators:
     *                          - EQUALS (=)           → Works with INT and VARCHAR
     *                          - GREATER_THAN (>)     → Works with INT only
     *                          - GREATER_THAN_EQUAL (>=) → Works with INT only
     *                          - LESS_THAN (<)        → Works with INT only
     *                          - LESS_THAN_EQUAL (<=) → Works with INT only
     *                          - NOT_EQUALS (!=)      → Works with INT only
     *                          ⚠️ Current limitations:
     *                          - Only first condition in list is processed (multi-condition support planned)
     *                          - Comparison operators (>, <, >=, <=, !=) only support INT type
     *                          💡 Example usage:
     *                          SELECT ALL FROM users;              → Show all user data
     *                          SELECT name, age FROM users WHERE age = 28; → Show specific columns with filter
     */

    public void validateSelectCommand(Table table, List<String> columns, List<Condition> conditions) {
        // 🏛️ [PATH OF THE SCHOLAR] - No conditions, show all that exists
        if (conditions == null && columns.size() == 1 && columns.getFirst().equals("ALL")) {
            System.out.println("⚡ [REALM UNVEILED] The Allfather grants you sight of all records!");
            System.out.println(table);
        }
        // 📜 [CHOSEN COLUMNS] - Select specific columns without filtering
        else if (conditions == null && !columns.getFirst().equals("ALL")) {
            // ✅ Validate each requested column exists in the table
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
            System.out.println("⚡ [CHRONICLES REVEALED] Behold the data you seek, mortal!");
            printTable(columns, table, columnIndices);
        }
        // 🔥 [JUDGMENT WITH CONDITIONS] - SELECT ALL but filter by WHERE clause
        else if (conditions != null && columns.size() == 1 && columns.getFirst().equals("ALL")) {
            System.out.println("⚔️ [THE HUNT BEGINS] Searching the Nine Realms for worthy records...");

            // Extract the WHERE condition components
            TokenType condition = conditions.getFirst().condition;
            String columnName = conditions.getFirst().columnName;
            ValueDefinition conditionValue = conditions.getFirst().conditionValue;

            // ⚖️ [SCALES OF EQUALITY] - Filter by exact match
            if (condition == TokenType.EQUALS) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());

                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🔨 [MJOLNIR'S JUDGMENT] Seeking rows where " + columnName + " equals " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i) == convertedValue) {
                                    System.out.println("✨ [WORTHY SOUL FOUND] " + row);
                                }
                            }
                        } else if (table.columnList.get(i).getType() == TokenType.VARCHAR && convertedValue instanceof String strValue) {
                            System.out.println("📖 [RUNES DECIPHERED] Seeking rows where " + columnName + " equals '" + strValue + "'...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i).equals(convertedValue)) {
                                    System.out.println("✨ [WORTHY SOUL FOUND] " + row);
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [FORBIDDEN MAGIC] The Norns whisper: 'This datatype is beyond mortal comprehension!'");
                        }
                    }
                }
            }
            // 🗡️ [GREATER POWER] - Filter by greater than
            else if (condition == TokenType.GREATER_THAN) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("⚡ [SURPASSING STRENGTH] Seeking rows where " + columnName + " exceeds " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal > intValue) {
                                        System.out.println("💪 [MIGHTY WARRIOR FOUND] " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] The Valkyries found none worthy.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [INVALID COMPARISON] Baldur roars: 'Only numbers can be measured by strength!'");
                        }
                    }
                }
            }
            // 🛡️ [EQUAL OR GREATER] - Filter by greater than or equal
            else if (condition == TokenType.GREATER_THAN_EQUAL) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("⚔️ [EQUAL OR MIGHTIER] Seeking rows where " + columnName + " equals or surpasses " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal >= intValue) {
                                        System.out.println("🏆 [CHAMPION DISCOVERED] " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] Not even Atreus qualifies.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [INVALID COMPARISON] Freya warns: 'Only integers may be weighed on these scales!'");
                        }
                    }
                }
            }
            // ⬇️ [LESSER OR EQUAL] - Filter by less than or equal
            else if (condition == TokenType.LESS_THAN_EQUAL) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🌊 [BENEATH THE THRESHOLD] Seeking rows where " + columnName + " falls to or below " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal <= intValue) {
                                        System.out.println("🎯 [HUMBLE SOUL FOUND] " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] All exceeded Odin's limit.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [INVALID COMPARISON] Brok grumbles: 'Can't measure words with numbers, ya daft!'");
                        }
                    }
                }
            }
            // ⬇️ [STRICTLY LESSER] - Filter by less than
            else if (condition == TokenType.LESS_THAN) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🌑 [BELOW THE MARK] Seeking rows where " + columnName + " falls short of " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal < intValue) {
                                        System.out.println("🌟 [LESSER BEING FOUND] " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] None fell beneath Hell's threshold.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [INVALID COMPARISON] Sindri sighs: 'Numbers only, please. Standards matter!'");
                        }
                    }
                }
            }
            // ≠ [THE OUTCASTS] - Filter by not equals
            else if (condition == TokenType.NOT_EQUALS) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🚫 [THE EXCLUDED] Seeking rows where " + columnName + " differs from " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (!rowVal.equals(intValue)) {
                                        System.out.println("🔮 [DIFFERENT PATH TAKEN] " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] All matched Týr's number.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [INVALID COMPARISON] Kratos grunts: 'Inequality requires numbers, not words!'");
                        }
                    }
                }
            }
        }
        // 🎯 [PRECISION STRIKE] - SELECT specific columns WITH WHERE condition
        else if (conditions != null) {
            System.out.println("🏹 [AIMED SHOT] Atreus draws his bow—targeting specific columns with precision...");

            List<Integer> columnIndices = getIntegers(columns, table);
            TokenType condition = conditions.getFirst().condition;
            String columnName = conditions.getFirst().columnName;
            ValueDefinition conditionValue = conditions.getFirst().conditionValue;

            // ⚖️ [EQUALS CONDITION] - Exact match with column selection
            if (condition == TokenType.EQUALS) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());

                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🔨 [PRECISION STRIKE] Hunting rows where " + columnName + " = " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i) == convertedValue) {
                                    System.out.println("🎯 [TARGET ACQUIRED] Selected columns:");
                                    for (Integer columnIndex : columnIndices) {
                                        System.out.println("   → " + row.getValue(columnIndex));
                                    }
                                } else {
                                    System.out.println("☢️ [NO MORTALS FOUND] The hunt yields nothing.");
                                }
                            }
                        } else if (table.columnList.get(i).getType() == TokenType.VARCHAR && convertedValue instanceof String strValue) {
                            System.out.println("📜 [RUNE MATCH] Hunting rows where " + columnName + " = '" + strValue + "'...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i).equals(convertedValue)) {
                                    System.out.println("🎯 [TARGET ACQUIRED] Selected columns:");
                                    for (Integer columnIndex : columnIndices) {
                                        System.out.println("   → " + row.getValue(columnIndex));
                                    }
                                } else {
                                    System.out.println("☢️ [NO MORTALS FOUND] Mimir finds no match in his tales.");
                                }
                            }
                        }
                    }
                }
            }
            // 🗡️ [GREATER THAN] - With column selection
            else if (condition == TokenType.GREATER_THAN) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("⚡ [SUPERIOR FORCE] Hunting rows where " + columnName + " > " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal > intValue) {
                                        System.out.println("💪 [STRONGER FOUND] Selected columns:");
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println("   → " + row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] None surpass Thor's might.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [TYPE MISMATCH] Kratos warns: 'Comparisons demand integer strength!'");
                        }
                    }
                }
            }
            // 🛡️ [GREATER OR EQUAL] - With column selection
            else if (condition == TokenType.GREATER_THAN_EQUAL) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("⚔️ [WORTHY OR GREATER] Hunting rows where " + columnName + " >= " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal >= intValue) {
                                        System.out.println("🏆 [QUALIFIED WARRIOR] Selected columns:");
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println("   → " + row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] The Einherjar find none worthy.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [TYPE MISMATCH] The World Serpent hisses: 'Only numbers may be compared!'");
                        }
                    }
                }
            }
            // ⬇️ [LESS OR EQUAL] - With column selection
            else if (condition == TokenType.LESS_THAN_EQUAL) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🌊 [AT OR BELOW] Hunting rows where " + columnName + " <= " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal <= intValue) {
                                        System.out.println("🎯 [WITHIN BOUNDS] Selected columns:");
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println("   → " + row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] All exceeded Yggdrasil's limit.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [TYPE MISMATCH] Brok shouts: 'Ya can't weigh text, ya blockhead!'");
                        }
                    }
                }
            }
            // ⬇️ [STRICTLY LESS] - With column selection
            else if (condition == TokenType.LESS_THAN) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🌑 [BENEATH THE LINE] Hunting rows where " + columnName + " < " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal < intValue) {
                                        System.out.println("🌟 [BELOW THRESHOLD] Selected columns:");
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println("   → " + row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] None dwelt in Niflheim's depths.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [TYPE MISMATCH] Sindri explains: 'Precision requires integers, not strings!'");
                        }
                    }
                }
            }
            // ≠ [NOT EQUALS] - With column selection
            else if (condition == TokenType.NOT_EQUALS) {
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🚫 [THE DIFFERENT] Hunting rows where " + columnName + " != " + intValue + "...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (!rowVal.equals(intValue)) {
                                        System.out.println("🔮 [DIVERGENT PATH] Selected columns:");
                                        for (Integer columnIndex : columnIndices) {
                                            System.out.println("   → " + row.getValue(columnIndex));
                                        }
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] All followed Ragnarok's path.");
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("⚠️ [TYPE MISMATCH] Freya decrees: 'Difference is measured in numbers alone!'");
                        }
                    }
                }
            }
        }
    }

    /**
     * 🗑️ Validates and executes a DELETE command on the specified table.
     * <p>
     * 📝 Supported syntax patterns:
     * - DELETE FROM table;                  → Deletes ALL rows (clears table)
     * - DELETE FROM table WHERE condition;  → Deletes rows matching condition
     * <p>
     * ⚙️ Execution flow:
     * 1. 🎯 Checks if condition is null (delete all) or specific (filtered delete)
     * 2. 🔍 Locates target column in table schema
     * 3. 🔄 Iterates through rows and evaluates condition
     * 4. 🗑️ Removes matching rows from table.rowList
     * 5. 📊 Reports deletion results to user
     *
     * @param table     The target table object from which rows will be deleted
     * @param condition The WHERE condition to filter which rows to delete (null = delete all)
     * @throws RuntimeException if:
     *                          ❌ Data type mismatch in WHERE condition
     *                          ❌ Unsupported comparison operator for given data type
     *                          ❌ Column referenced in condition doesn't exist
     *                          🎭 Supported operators:
     *                          - EQUALS (=)  → Works with INT and VARCHAR
     *                          - GREATER_THAN (>) → Works with INT only
     *                          - GREATER_THAN_EQUAL (>=) → Works with INT only
     *                          - LESS_THAN (<) → Works with INT only
     *                          - LESS_THAN_EQUAL (<=) → Works with INT only
     *                          - NOT_EQUALS (!=) → Works with INT only
     *                          ⚠️ CRITICAL WARNING:
     *                          - Modifies list while iterating (potential ConcurrentModificationException risk)
     *                          - Consider using Iterator.remove() or collecting indices first
     *                          💡 Example usage:
     *                          DELETE FROM users;  → Removes all users
     *                          DELETE FROM users WHERE age < 18; → Removes underage users
     */

    public void validateDeleteCommand(Table table, Condition condition) {
        // 💀 [RAGNARÖK MODE] - Delete all rows if no condition specified
        if (condition == null) {
            System.out.println("☠️ [APOCALYPSE UNLEASHED] Kratos roars: 'All shall fall! Every record... DELETED!'");
            table.rowList.clear();
            System.out.println("🌋 [DESTRUCTION COMPLETE] " + rowList.size() + " souls remain in the void.");
        } else {
            System.out.println("⚔️ [SELECTIVE PURGE] The Blades of Chaos seek their targets...");

            // 🎯 Extract WHERE condition components for evaluation
            TokenType conditionType = condition.condition;
            String columnName = condition.columnName;
            ValueDefinition conditionValue = condition.conditionValue;

            // ⚖️ [EXACT JUSTICE] - Delete rows with exact match (EQUALS operator)
            if (conditionType == TokenType.EQUALS) {
                // 🔍 Loop through all columns to find the target column by name
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        // 🔄 Convert the condition value to match column's data type
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());

                        // 🔢 Handle INTEGER type comparison
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🔨 [MJOLNIR'S WRATH] Striking down rows where " + columnName + " = " + intValue + "...");
                            // ⚖️ [SCALES OF JUSTICE] - Does this integer match our target?
                            // Iterate through all rows and check if column value equals target
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i) == convertedValue) {
                                    table.rowList.remove(row);
                                    System.out.println("💀 [MORTAL SLAIN] Row vanquished: " + row);
                                }
                            }
                        }
                        // 📝 Handle VARCHAR (String) type comparison
                        else if (table.columnList.get(i).getType() == TokenType.VARCHAR && convertedValue instanceof String strValue) {
                            System.out.println("📜 [RUNE JUDGMENT] Erasing rows where " + columnName + " = '" + strValue + "'...");
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                if (row.getValue(i).equals(convertedValue)) {
                                    table.rowList.remove(row);
                                    System.out.println("💀 [SOUL BANISHED] Row erased: " + row);
                                }
                            }
                        }
                        // ❌ Unsupported data type for EQUALS operator
                        else {
                            throw new RuntimeException("⚠️ [UNKNOWN POWER] The Norns gasp: 'This datatype is forbidden in deletion rituals!'");
                        }
                    }
                }
            }
            // 🗡️ [PURGE THE MIGHTY] - Delete rows greater than value (GREATER_THAN operator)
            else if (conditionType == TokenType.GREATER_THAN) {
                // 🔍 Locate the target column in the table schema
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        // 🔄 Convert condition value to appropriate type
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());

                        // 🔢 Only INTEGER types support comparison operators
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("⚡ [CULLING THE STRONG] Eliminating rows where " + columnName + " > " + intValue + "...");
                            // ⚖️ [SCALES OF JUSTICE] - Does this value exceed our threshold?
                            // Compare each row's value against the target integer
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal > intValue) {
                                        table.rowList.remove(row);
                                        System.out.println("☢️ [TITAN FALLS] Row deleted: " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] None exceeded the limit. Zero rows deleted.");
                                    }
                                }
                            }
                        }
                        // ❌ VARCHAR and other types don't support GREATER_THAN
                        else {
                            throw new RuntimeException("⚠️ [INVALID RITUAL] Baldur screams: 'Only numbers can be compared for deletion!'");
                        }
                    }
                }
            }
            // 🛡️ [REMOVE THE WORTHY] - Delete rows >= value (GREATER_THAN_EQUAL operator)
            else if (conditionType == TokenType.GREATER_THAN_EQUAL) {
                // 🔍 Find the column index by matching column name
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        // 🔄 Type conversion for safe comparison
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());

                        // 🔢 Validate column is INT type for comparison
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("⚔️ [EQUAL OR GREATER DOOM] Purging rows where " + columnName + " >= " + intValue + "...");
                            // ⚖️ [SCALES OF JUSTICE] - Does this value meet or exceed the threshold?
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal >= intValue) {
                                        table.rowList.remove(row);
                                        System.out.println("💀 [WARRIOR DEFEATED] Row eliminated: " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] None met Valhalla's threshold. Zero rows deleted.");
                                    }
                                }
                            }
                        }
                        // ❌ Only integers support >= comparison
                        else {
                            throw new RuntimeException("⚠️ [FORBIDDEN MAGIC] Freya warns: 'Only integers may be judged for deletion!'");
                        }
                    }
                }
            }
            // ⬇️ [SMITE THE WEAK] - Delete rows <= value (LESS_THAN_EQUAL operator)
            else if (conditionType == TokenType.LESS_THAN_EQUAL) {
                // 🔍 Scan column list to find matching column name
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        // 🔄 Convert string/token value to actual data type
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());

                        // 🔢 Ensure we're working with integer data
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🌊 [DROWNING THE LESSER] Removing rows where " + columnName + " <= " + intValue + "...");
                            // ⚖️ [SCALES OF JUSTICE] - Does this value fall at or below our limit?
                            // Check each row value against the upper bound
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal <= intValue) {
                                        table.rowList.remove(row);
                                        System.out.println("💀 [WEAK ONE PERISHES] Row deleted: " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] All survived the cut. Zero rows deleted.");
                                    }
                                }
                            }
                        }
                        // ❌ Text cannot be compared with <= operator
                        else {
                            throw new RuntimeException("⚠️ [TYPE ERROR] Brok yells: 'Ya can't delete based on text comparisons, ya daft!'");
                        }
                    }
                }
            }
            // ⬇️ [PURGE THE INFERIOR] - Delete rows < value (LESS_THAN operator)
            else if (conditionType == TokenType.LESS_THAN) {
                // 🔍 Locate column by iterating through table schema
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        // 🔄 Transform condition value into column's native type
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());

                        // 🔢 Validate integer type for numeric comparison
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🌑 [CASTING INTO HEL] Banishing rows where " + columnName + " < " + intValue + "...");
                            // ⚖️ [SCALES OF JUSTICE] - Does this value fall short of our minimum?
                            // Evaluate each row to see if it's below threshold
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (rowVal < intValue) {
                                        table.rowList.remove(row);
                                        System.out.println("💀 [SOUL CLAIMED BY HEL] Row deleted: " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] None fell below Niflheim's depths. Zero rows deleted.");
                                    }
                                }
                            }
                        }
                        // ❌ Strings and other types can't use < operator
                        else {
                            throw new RuntimeException("⚠️ [TYPE ERROR] Sindri mutters: 'Numerical deletion only, please!'");
                        }
                    }
                }
            }
            // ≠ [EXILE THE DIFFERENT] - Delete rows != value (NOT_EQUALS operator)
            else if (conditionType == TokenType.NOT_EQUALS) {
                // 🔍 Search for target column in table's column list
                for (int i = 0; i < table.columnList.size(); i++) {
                    if (table.columnList.get(i).columnName.equals(columnName)) {
                        // 🔄 Convert value to match column data type
                        Object convertedValue = convertValue(conditionValue, table.columnList.get(i).getType());

                        // 🔢 Integer type required for inequality check
                        if (table.columnList.get(i).getType() == TokenType.INT && convertedValue instanceof Integer intValue) {
                            System.out.println("🚫 [BANISHING THE OUTCASTS] Erasing rows where " + columnName + " != " + intValue + "...");
                            // ⚖️ [SCALES OF JUSTICE] - Does this value differ from our target?
                            // Remove all rows that don't match the specified value
                            for (int j = 0; j < table.rowList.size(); j++) {
                                Row row = table.rowList.get(j);
                                Object val = row.getValue(i);
                                if (val instanceof Integer rowVal) {
                                    if (!rowVal.equals(intValue)) {
                                        table.rowList.remove(row);
                                        System.out.println("💀 [OUTCAST REMOVED] Row deleted: " + row);
                                    } else {
                                        System.out.println("☢️ [NO MORTALS FOUND] All matched Ragnarok's number. Zero rows deleted.");
                                    }
                                }
                            }
                        }
                        // ❌ Only integers support != operator currently
                        else {
                            throw new RuntimeException("⚠️ [TYPE ERROR] Kratos growls: 'Inequality requires integer power!'");
                        }
                    }
                }
            }

            // 🏆 Report final status after deletion operation completes
            System.out.println("🏆 [PURGE COMPLETE] The battlefield is cleared. Rows remaining: " + table.rowList.size());
        }
    }

    /**
     * ⚡ Validates and executes an UPDATE command on the specified table.
     * 📝 Supported syntax patterns:
     * - UPDATE table SET col1=val1, col2=val2;                    → Updates ALL rows (requires confirmation)
     * - UPDATE table SET col1=val1, col2=val2 WHERE condition;   → Updates rows matching condition
     * ⚙️ Execution flow:
     * 1. 🎯 Checks if condition is null (update all rows - dangerous operation)
     * 2. ⚠️ Prompts user for confirmation if updating all rows
     * 3. ✅ Validates all column names exist in table schema
     * 4. 🔄 Validates data types match between values and columns
     * 5. 🔍 Resolves column names to indices for efficient access
     * 6. 🔨 Applies updates to all matching rows
     *
     * @param table      The target table object where rows will be updated
     * @param map        HashMap containing column names as keys and new values as ValueDefinition objects
     * @param conditions The WHERE condition to filter which rows to update (null = update all with confirmation)
     * @throws RuntimeException if:
     *                          ❌ Column name in SET clause doesn't exist in table
     *                          ❌ Value data type doesn't match column data type
     *                          ❌ Type conversion fails
     *                          🎭 Update behavior:
     *                          - Without WHERE: Updates ALL rows (asks for Y/N confirmation)
     *                          - With WHERE: Updates only rows matching condition (planned feature)
     *                          ⚠️ Current limitations:
     *                          - WHERE clause support is not yet implemented
     *                          - User must confirm mass updates with 'Y' or 'y'
     *                          - Scanner remains open if user declines (potential resource leak)
     *                          💡 Example usage:
     *                          UPDATE users SET age=30, status='active';           → Updates all users (with confirmation)
     *                          UPDATE users SET age=30 WHERE id=5;                 → Updates specific user (planned)
     */

    public void validateUpdateCommand(Table table, HashMap<String, ValueDefinition> map, Condition conditions) {
        List<String> columns = new ArrayList<>();
        List<ValueDefinition> values = new ArrayList<>();
        // ⚠️ [DANGEROUS TERRITORY] - No WHERE clause means ALL rows will be modified
        if (conditions == null) {
            System.out.println("🔥 [POINT OF NO RETURN] This will modify EVERY row in the table '" + table.tableName + "'!");
            System.out.println("⚡ Kratos asks: 'Are you certain, boy?' Press Y to proceed, N to retreat:");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();

            // ✅ [COURAGE CONFIRMED] - User accepts the consequences
            if (input.equalsIgnoreCase("Y")) {
                System.out.println("💪 [DETERMINATION ACKNOWLEDGED] Kratos nods: 'Then let us reshape destiny itself!'");

                // 📋 Prepare lists to store column names and their new values
                for (String columnName : map.keySet()) {
                    // ❌ Throw error if column doesn't exist in table
                    if (table.columnList.stream().noneMatch(columnDefinition -> columnDefinition.columnName.equals(columnName))) {
                        throw new RuntimeException("💥 [COLUMN VANISHED] The Norns cry out: 'Column '" + columnName +
                                "' does not exist in table '" + table.tableName + "'! Check your runes!'");
                    } else {
                        // ✅ Column found - add to processing lists
                        columns.add(columnName);
                        values.add(map.get(columnName));
                    }
                }

                for (int i = 0; i < columns.size(); i++) {
                    ColumnDefinition colDef = getColumn(columns.get(i));
                    ValueDefinition valDef = values.get(i);

                    // 🔄 Attempt type conversion - will throw exception if types incompatible
                    Object convertedValue = convertValue(valDef, colDef.type);
                }

                // 📍 [INDEX RESOLUTION] - Convert column names to their integer indices.
                // Indices allow direct access to row values without name lookups
                List<Integer> getIndexes = getIntegers(columns, table);

                for (int i = 0; i < getIndexes.size(); i++) {
                    int currIndex = getIndexes.get(i);
                    String val = values.get(i).value;

                    // 🔄 Iterate through every single row and update the column value
                    for (int j = 0; j < table.rowList.size(); j++) {
                        Row row = table.rowList.get(j);
                        row.setNewValue(currIndex, val);
                    }
                }
                System.out.println("⚡ Kratos grunts: 'It is done. The table bends to your will.'");
            }
            // 🛡️ [WISDOM PREVAILS] - User chooses not to proceed with mass update
            else {
                System.out.println("💭 The table '" + table.tableName + "' remains untouched, like Jötunheim's frozen wastes.");
                sc.close();
            }
        }
        // 🎯 [CONDITIONAL UPDATE] - WHERE clause provided (not yet implemented)
        else {
// 🎯 [CONDITIONAL UPDATE] - WHERE clause provided
            System.out.println("🏹 [AIMED STRIKE] Kratos narrows his eyes — only select rows shall be changed...");

// Validate and collect columns and their new values
            for (String columnName : map.keySet()) {
                boolean exists = table.columnList.stream()
                        .anyMatch(col -> col.columnName.equals(columnName));

                if (!exists) {
                    throw new RuntimeException("💥 [COLUMN VANISHED] The Norns cry out: 'Column '" + columnName +
                            "' does not exist in table '" + table.tableName + "'!'");
                }
                columns.add(columnName);
                values.add(map.get(columnName));
            }

// Extract condition details
            TokenType condition = conditions.condition;
            String condColumn = conditions.columnName;
            ValueDefinition condValue = conditions.conditionValue;

// Get index of the column used in WHERE condition
            int condColumnIndex = -1;
            for (int i = 0; i < table.columnList.size(); i++) {
                if (table.columnList.get(i).columnName.equals(condColumn)) {
                    condColumnIndex = i;
                    break;
                }
            }
            if (condColumnIndex == -1)
                throw new RuntimeException("❌ [CONDITION ERROR] The column '" + condColumn + "' doesn't exist in table '" + table.tableName + "'!");

// Prepare update column indices
            List<Integer> updateIndexes = getIntegers(columns, table);

// Convert condition value to proper type
            Object convertedCondValue = null;
            try { convertedCondValue = convertValue(condValue, table.columnList.get(condColumnIndex).type);
            }
            catch (Exception e) { System.out.println("⚠️ [VALUE CONVERSION FAILED] The fates refuse: " + e.getMessage());
            }

            int updatedCount = 0;

            // Iterate through rows to apply condition
            for (Row row : table.rowList) {
                Object currentVal = row.getValue(condColumnIndex);

                boolean conditionMatched = switch (condition) {
                    case EQUALS -> currentVal.equals(convertedCondValue);
                    case NOT_EQUALS -> !currentVal.equals(convertedCondValue);
                    case GREATER_THAN -> (currentVal instanceof Integer cv && cv > (Integer) convertedCondValue);
                    case GREATER_THAN_EQUAL -> (currentVal instanceof Integer cv && cv >= (Integer) convertedCondValue);
                    case LESS_THAN -> (currentVal instanceof Integer cv && cv < (Integer) convertedCondValue);
                    case LESS_THAN_EQUAL -> (currentVal instanceof Integer cv && cv <= (Integer) convertedCondValue);
                    default ->
                            throw new RuntimeException("⚠️ [UNKNOWN CONDITION] Freya cannot decipher this rune: " + condition);
                };

                // Apply update if condition matches
                if (conditionMatched) {
                    for (int i = 0; i < updateIndexes.size(); i++) {
                        int colIndex = updateIndexes.get(i);
                        String newVal = values.get(i).value;
                        row.setNewValue(colIndex, newVal);
                    }
                    updatedCount++;
                }
            }

// 🏁 Final outcome
            if (updatedCount > 0) {
                System.out.println("⚡ [DESTINY REWRITTEN] " + updatedCount + " row(s) reshaped under Kratos' command.");
            } else {
                System.out.println("☢️ [NO FATE CHANGED] No rows matched the condition — the Norns remain silent.");
            }

        }

    }
//     Method: getColumnIndexByName(String name)
}
