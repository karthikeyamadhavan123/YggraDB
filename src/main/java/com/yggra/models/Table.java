package com.yggra.models;

import com.yggra.commands.ColumnDefinition;
import com.yggra.commands.ValueDefinition;
import com.yggra.parser.TokenType;

import java.util.ArrayList;
import java.util.List;


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
        if (!validateDefaultValue(column, defaultValue)) {
            throw new RuntimeException(
                    "⚡ [WRONG POWER] The default value '" + defaultValue +
                            "' defies the laws of Midgard! Match its might to the column's true type."
            );

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

        // add the default value is it is null else add defaultValue.
        for (Row row : rowList) {
            //check the datatypes of default row values
            row.addDefaultValues(defaultValue.value);
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

    // Method: getColumnIndexByName(String name)
}
