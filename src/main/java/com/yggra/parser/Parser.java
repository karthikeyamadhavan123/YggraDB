package com.yggra.parser;

import com.yggra.commands.*;
import com.yggra.common_models.Condition;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * SQL Command Parser - The Oracle of Database Commands
 * This parser transforms tokens into Abstract Syntax Trees (AST) for SQL commands.
 * /Currently supports CREATE TABLE and INSERT INTO statements.
 * The parser uses a recursive descent approach with dramatic error messaging
 * inspired by mythological themes.
 */

public class Parser {
    // SQL command parser
    // 1. Check first token: CREATE or INSERT?
    // 2. If CREATE TABLE → build CreateTableCommand AST
    // 3. If INSERT INTO → build InsertCommand AST
    // 4. Return the AST to the caller (REPL or executor)

    public final List<Token> tokens;
    public int position = 0;

    /**
     * Constructor - Initializes the parser with a list of tokens
     *
     * @param tokens List of tokens to parse (must not be null or empty)
     */

    public Parser(List<Token> tokens) {
        if (tokens == null) {
            throw new RuntimeException("🌊 [POSEIDON'S VOID] The token stream cannot be null — the seas of syntax demand substance!");
        }
        if (tokens.isEmpty()) {
            throw new RuntimeException("🏺 [EMPTY AMPHORA] No tokens provided to parse — the vessel of knowledge stands barren!");
        }
        this.tokens = tokens;
    }

    /**
     * Peek - Examines the current token without advancing the position
     * Used for lookahead parsing decisions
     *
     * @return The current token at the position
     * @throws RuntimeException if trying to peek beyond the token stream
     */

    public Token peek() {
        // Look at the current token without consuming it
        if (position < tokens.size()) {
            return tokens.get(position);
        } else {
            throw new RuntimeException("🕱 [END OF SCROLL] No more tokens to read, yet the parser seeks beyond the known realm!");
        }
    }

    /**
     * Advance - Moves the parser position forward by one token
     * Used after successfully parsing a token
     *
     * @throws RuntimeException if trying to advance beyond the last token
     */

    public void advance() {
        // Move to the next token and return the current one
        if (position < tokens.size()) {
            position++;
        } else {
            throw new RuntimeException("🕱 [END OF SCROLL] Tried to advance beyond the last token — the abyss holds no secrets here!");
        }
    }

    /**
     * Consume - Validates and consumes a token of expected type
     * This is the core validation method ensuring token sequence correctness
     *
     * @param expectedType The token type that should be at current position
     * @throws RuntimeException if current token doesn't match expected type
     */

    public void consume(TokenType expectedType) {
        // Consume the current token if it matches the expected type, else throw error
        if (position >= tokens.size()) {
            throw new RuntimeException("🌌 [COSMOS' END] Expected " + expectedType + " but reached the void beyond all tokens — the universe of syntax has ended!");
        }

        Token token = peek();
        if (token.type == expectedType) {
            advance();
        } else {
            throw new RuntimeException("⛓️ [CHAINS OF FATE] Expected " + expectedType + " but found " + token.type + " ('" + token.value + "')");
        }
    }

    /**
     * ⚔️ [FORGE OF COLUMNS] ⚔️
     * Parses a single column definition inside the CREATE TABLE ritual.
     * 🔮 Expected format:
     * column_name column_type [VARCHAR(size) | INT] [DEFAULT literal_or_NULL]
     * 🏛️ Supported column types:
     * - INT
     * • Optional DEFAULT may be:
     * → NUMBER_LITERAL (e.g., DEFAULT 0)
     * → NULL (e.g., DEFAULT NULL)
     * - VARCHAR(n)
     * • Requires a size declaration inside parentheses (e.g., VARCHAR(255))
     * • Optional DEFAULT may be:
     * → STRING_LITERAL (e.g., DEFAULT 'Kratos')
     * → NULL (e.g., DEFAULT NULL)
     * 📜 Behavior:
     * - Requires a valid column name (IDENTIFIER).
     * - Requires a column type (INT or VARCHAR with size).
     * - Validates VARCHAR size syntax (must be NUMBER_LITERAL inside parentheses).
     * - Handles DEFAULT values:
     * • INT → NUMBER_LITERAL or NULL
     * • VARCHAR → STRING_LITERAL or NULL
     * - Differentiates between:
     * • No DEFAULT → hasDefault = false, value = null
     * • DEFAULT NULL → hasDefault = true, value = null
     * ⚡ Outcome:
     * Returns a ColumnDefinition carrying the column name, type,
     * size (if applicable), and default value (literal or NULL).
     *
     * @return ColumnDefinition representing the parsed column metadata.
     * @throws RuntimeException if:
     *                          - Column name is missing or invalid.
     *                          - Type is missing or unsupported.
     *                          - VARCHAR size is missing or invalid.
     *                          - DEFAULT value does not match type rules.
     *                          - Extra tokens appear after DEFAULT value.
     */

    private ColumnDefinition parseColumnDefinition() {
        // Ensure column name is present
        if (position >= tokens.size()) {
            throw new RuntimeException("🌪️ [HERMES' VANISHING] Expected column name but reached end of tokens — the message was lost in transit!");
        }

        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚔️ [NAMELESS ALTAR] Column name was demanded — yet none was offered to the gods.");
        }
        String colName = peek().value;
        consume(TokenType.IDENTIFIER);

        // Check for column type presence
        if (position >= tokens.size()) {
            throw new RuntimeException("🏛️ [ATHENA'S WISDOM LOST] Column name found but type is missing — wisdom demands both name and nature!");
        }

        // Column type parsing
        Token typeToken = peek();
        if (typeToken.type == TokenType.INT) {
            consume(TokenType.INT);

            // Handle default value for INT
            if (peek().type == TokenType.DEFAULT) {
                consume(TokenType.DEFAULT);
                TokenType type = peek().type;
                String value = type == TokenType.NULL ? null : peek().value;

                if (type == TokenType.NUMBER_LITERAL) {
                    consume(TokenType.NUMBER_LITERAL);
                } else if (type == TokenType.NULL) {
                    consume(TokenType.NULL);
                } else {
                    throw new RuntimeException("❌ [BROKEN FORGE] INT may only be tempered with NUMBER or NULL — yet an unfit offering was given: " + value);
                }

                ValueDefinition defaultValue = new ValueDefinition(type, value);

                if (position < tokens.size() && peek().type != TokenType.COMMA && peek().type != TokenType.RIGHT_PAREN) {
                    throw new RuntimeException("⚡ [ODIN'S WRATH] Unexpected token after DEFAULT value: " + peek().value + ". Only ',' or ')' may follow!");
                }

                return new ColumnDefinition(colName, TokenType.INT, -1, true, defaultValue);
            } else {
                return new ColumnDefinition(colName, TokenType.INT, -1);
            }

        } else if (typeToken.type == TokenType.VARCHAR) {
            consume(TokenType.VARCHAR);

            // VARCHAR requires size
            if (position >= tokens.size()) {
                throw new RuntimeException("📏 [BROKEN MEASURE] VARCHAR declared but size specification missing — how vast shall this text domain be?");
            }

            if (peek().type != TokenType.LEFT_PAREN) {
                throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] VARCHAR demands '(' to declare its size — boundaries must be set!");
            }
            consume(TokenType.LEFT_PAREN);

            if (position >= tokens.size()) {
                throw new RuntimeException("🔢 [APOLLO'S COUNT LOST] Expected size number for VARCHAR but found void — the count has been forgotten!");
            }

            if (peek().type != TokenType.NUMBER_LITERAL) {
                throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] VARCHAR size must be a NUMBER_LITERAL, not " + peek().value);
            }
            int size = parseInt(peek().value);
            consume(TokenType.NUMBER_LITERAL);

            if (position >= tokens.size()) {
                throw new RuntimeException("🌉 [BRIDGE UNFINISHED] VARCHAR size given but closing ')' missing — the declaration remains incomplete!");
            }

            if (peek().type != TokenType.RIGHT_PAREN) {
                throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] Expected ')' to close VARCHAR size specification, but found " + peek().value);
            }
            consume(TokenType.RIGHT_PAREN);

            // Handle default value for VARCHAR
            if (peek().type == TokenType.DEFAULT) {
                consume(TokenType.DEFAULT);
                TokenType type = peek().type;
                String value = type == TokenType.NULL ? null : peek().value;

                if (type == TokenType.STRING_LITERAL) {
                    consume(TokenType.STRING_LITERAL);
                } else if (type == TokenType.NULL) {
                    consume(TokenType.NULL);
                } else {
                    throw new RuntimeException("❌ [SHATTERED LUTE] VARCHAR may only echo with STRING or NULL — but a false sound was heard: " + value);
                }

                ValueDefinition defaultValue = new ValueDefinition(type, value);

                if (peek().type != TokenType.COMMA && peek().type != TokenType.RIGHT_PAREN) {
                    throw new RuntimeException("⚡ [ZEUS’ DISPLEASURE] After the DEFAULT gift, nothing else may follow but ',' or ')' — yet " + peek().value + " dares intrude.");
                }

                return new ColumnDefinition(colName, TokenType.VARCHAR, size, true, defaultValue);
            } else {
                return new ColumnDefinition(colName, TokenType.VARCHAR, size);
            }

        } else {
            throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] Expected column type (INT or VARCHAR), but found: " + typeToken.value);
        }
    }

    /**
     * Parse Column Insert Statements - Handles parsing of comma-separated column names or values
     * Used for both column lists and value lists in INSERT statements
     * Format: item1, item2, ..., itemN (where items can be column names or values)
     *
     * @throws RuntimeException for malformed lists (trailing commas, double commas, etc.)
     */

    private List<String> parseColumnInsertStatements() {
        // Parse the first column name or value (at least one is required)
        List<String> columns = new ArrayList<>();
        columns.add(parseColumnInsertStatement());
        // Handle comma-separated additional items
        while (position < tokens.size() && peek().type == TokenType.COMMA) {
            consume(TokenType.COMMA);
            // Check for unexpected end of input after comma
            if (position >= tokens.size()) {
                throw new RuntimeException("🗡️ [ARES' BROKEN SPEAR] Comma found but no following value — the army of data stands incomplete!");
            }
            // Check for double comma error (comma immediately followed by another comma)
            if (peek().type == TokenType.COMMA) {
                throw new RuntimeException("⚡ [DOUBLE LIGHTNING] Two commas in succession — even Zeus strikes but once at a time!");
            }
            if (peek().type == TokenType.RIGHT_PAREN) {
                throw new RuntimeException("🛡️ [SHIELD WITHOUT WARRIOR] Comma demands a value to follow — yet only closure awaits!");
            }
            // Parse the next column name or value
            columns.add(parseColumnInsertStatement());
        }
        return columns;
    }

    /**
     * 🏛️ [COLUMN OFFERINGS FOR INSERT] 🏛️
     * Parses a single column name in the ritual of an INSERT statement.
     * 🔮 Expected format:
     * - IDENTIFIER (column_name)
     * 📜 Behavior:
     * - Ensures a valid token exists at the current parsing position.
     * - Accepts only IDENTIFIER tokens as valid column names.
     * - Rejects invalid or unexpected tokens with mythic clarity.
     * ⚡ Outcome:
     * Returns the name of the column (IDENTIFIER) to be used in INSERT operations.
     *
     * @return The parsed column name as a String.
     * @throws RuntimeException if:
     *                          - No tokens remain when a column name is expected.
     *                          - A token other than IDENTIFIER is encountered.
     */

    private String parseColumnInsertStatement() {
        String columnName;
        // Ensure we have a token to examine
        if (position >= tokens.size()) {
            throw new RuntimeException("📦 [EMPTY OFFERING] Expected value for insertion but found void — the gods demand tribute!");
        }
        // Accept various token types for INSERT operations:
        if (peek().type == TokenType.IDENTIFIER) {
            // Column names are just consumed here
            columnName = peek().value;
            consume(TokenType.IDENTIFIER);
        } else {
            // For extensibility - other data types may be supported in future
            // Currently just logs the occurrence without throwing an error
            throw new RuntimeException("⚔️ [FALSE IDOL] INSERT expects a column name (IDENTIFIER), but a different token was offered — the gods reject this offering!");
        }
        return columnName;
    }

    /**
     * 🔮 [VALUES OFFERINGS FOR INSERT] 🔮
     * Parses a single value token in the sacred INSERT statement.
     * 📜 Expected tokens:
     * - NUMBER_LITERAL (e.g., 42)
     * - STRING_LITERAL (e.g., 'Athena')
     * - DEFAULT (to invoke the table’s preordained value)
     * - NULL (to mark absence, the void embraced by the gods)
     * ⚔️ Behavior:
     * - Ensures a token is available at the current parsing position.
     * - Rejects forbidden or chaotic symbols such as `;` or `--`.
     * - Returns a ValueDefinition encapsulating the token’s essence.
     * 🌌 Outcome:
     * Produces a ValueDefinition representing the given value, prepared to be inscribed into the chosen column.
     *
     * @return A ValueDefinition containing type and value.
     * @throws RuntimeException if:
     *                          - No tokens remain when a value is expected.
     *                          - A forbidden or malformed value is encountered.
     *                          - A token outside NUMBER_LITERAL, STRING_LITERAL, DEFAULT, or NULL is offered.
     */

    private ValueDefinition parseValuesInsertStatement() {
        // Ensure we have a token to examine
        if (position >= tokens.size()) {
            throw new RuntimeException("📦 [EMPTY OFFERING] No value provided for INSERT — the altar cannot stand barren!");
        }

        if (peek().value.contains("--") || peek().value.contains(";") || peek().value.matches(".*\\W&&[^_].*")) {
            throw new RuntimeException("🌪️ [CHAOS STORM] Forbidden runes detected (`;` or `--`) — such chaos cannot be inscribed into Yggra’s memory!");
        }

        // Accept valid INSERT values
        if (peek().type == TokenType.NUMBER_LITERAL) {
            String value = peek().value;
            consume(TokenType.NUMBER_LITERAL);
            return new ValueDefinition(TokenType.NUMBER_LITERAL, value);

        } else if (peek().type == TokenType.STRING_LITERAL) {
            String value = peek().value;
            consume(TokenType.STRING_LITERAL);
            return new ValueDefinition(TokenType.STRING_LITERAL, value);

        } else if (peek().type == TokenType.DEFAULT) {
            consume(TokenType.DEFAULT);
            return new ValueDefinition(TokenType.DEFAULT);

        } else if (peek().type == TokenType.NULL) {
            consume(TokenType.NULL);
            return new ValueDefinition(TokenType.NULL, null);

        } else {
            throw new RuntimeException("⚔️ [REJECTED OFFERING] The gods accept only numbers, strings, DEFAULT, or NULL — yet something alien was offered!");
        }
    }

    /**
     * Parse Values Insert Statements - Handles parsing of comma-separated values
     * Used for value lists in INSERT statements
     * Format: item1, item2, ..., itemN (where items will be values)
     *
     * @throws RuntimeException for malformed lists (trailing commas, double commas, etc.)
     */

    private List<ValueDefinition> parseValuesInsertStatements() {
        // Parse the first column name or value (at least one is required)
        List<ValueDefinition> columns = new ArrayList<>();
        columns.add(parseValuesInsertStatement());
        // Handle comma-separated additional items
        while (position < tokens.size() && peek().type == TokenType.COMMA) {
            consume(TokenType.COMMA);
            // Check for unexpected end of input after comma
            if (position >= tokens.size()) {
                throw new RuntimeException("🗡️ [ARES' BROKEN SPEAR] Comma found but no following value — the army of data stands incomplete!");
            }
            // Check for double comma error (comma immediately followed by another comma)
            if (peek().type == TokenType.COMMA) {
                throw new RuntimeException("⚡ [DOUBLE LIGHTNING] Two commas in succession — even Zeus strikes but once at a time!");
            }
            if (peek().type == TokenType.RIGHT_PAREN) {
                throw new RuntimeException("🛡️ [SHIELD WITHOUT WARRIOR] Comma demands a value to follow — yet only closure awaits!");
            }
            // Parse the next column name or value
            columns.add(parseValuesInsertStatement());
        }
        return columns;
    }

    /**
     * ⚔️ [FORGE COLUMN RUNE]
     * Deciphers the name and @type of a column to be added to a table’s saga.
     * This function reads the next sequence of tokens, expecting:
     * - A column name
     * - A valid data type (INT or VARCHAR with length)
     * - Proper rune seals such as parentheses for VARCHAR lengths
     * Throws lore-inspired errors when the ritual is broken.
     */

    private ColumnDefinition parseAlterColumn() {
        // 🛡️ Step 1: Ensure there are still runes (tokens) left to read
        if (position >= tokens.size()) {
            throw new RuntimeException("❌ [EMPTY FORGE] No column rune has been inscribed — the forge stands cold.");
        }

        // 🛡️ Step 2: Expect the column name rune
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("❌ [NAMELESS RUNE] A column name must be spoken before it can be forged.");
        }
        String columnName = peek().value;
        consume(TokenType.IDENTIFIER);

        // 🛡️ Step 3: Expect the datatype rune
        if (position >= tokens.size()) {
            throw new RuntimeException("❌ [HALF-FORGED] A datatype rune was expected, yet none was offered to the forge.");
        }

        // 🛡️ Step 4: INT column
        if (peek().type == TokenType.INT) {
            consume(TokenType.INT);
            return new ColumnDefinition(columnName, TokenType.INT, -1);
        }
        // 🛡️ Step 5: VARCHAR column with specified length
        else if (peek().type == TokenType.VARCHAR) {
            consume(TokenType.VARCHAR);
            if (position >= tokens.size()) {
                throw new RuntimeException("❌ [BROKEN INCANTATION] Expected ‘(’ to bind the rune’s length, yet the air is silent.");
            }
            if (peek().type != TokenType.LEFT_PAREN) {
                throw new RuntimeException("❌ [MISSING SEAL] The left paren rune is absent — the magic leaks away.");
            }
            consume(TokenType.LEFT_PAREN);

            if (position >= tokens.size()) {
                throw new RuntimeException("❌ [EMPTY VESSEL] No length rune was provided to give this column form.");
            }
            if (peek().type != TokenType.NUMBER_LITERAL) {
                throw new RuntimeException("❌ [MISCAST] Expected a length number rune, yet something else dares to take its place.");
            }
            int length = parseInt(peek().value);
            consume(TokenType.NUMBER_LITERAL);

            if (position >= tokens.size()) {
                throw new RuntimeException("❌ [OPEN GATE] The right paren rune is missing — the ritual remains incomplete.");
            }
            if (peek().type != TokenType.RIGHT_PAREN) {
                throw new RuntimeException("❌ [SEAL BROKEN] The closing right paren rune was expected, yet chaos has entered.");
            }
            consume(TokenType.RIGHT_PAREN);

            return new ColumnDefinition(columnName, TokenType.VARCHAR, length);
        }
        // 🛡️ Step 6: Unknown datatype rune
        else {
            throw new RuntimeException("❌ [UNKNOWN RUNE] The datatype rune is not recognized in the Nine Realms.");
        }
    }

    /**
     * Parses a single default value for a column.
     * Accepts either NUMBER_LITERAL or STRING_LITERAL tokens.
     * If the token type is invalid, logs a warning message.
     * ⚒️ This method is invoked when defining a single default value
     * during column creation or modification.
     */

    private ValueDefinition parseDefaultValue() {
        ValueDefinition defaultValue = null;

        // ⚔️ If the token is a numeric literal, take it as the default value
        if (peek().type == TokenType.NUMBER_LITERAL) {
            String numericalValue = peek().value;
            consume(TokenType.NUMBER_LITERAL);
            defaultValue = new ValueDefinition(TokenType.NUMBER_LITERAL, numericalValue);
            // 🪶 If the token is a string literal, accept it as the default value
        } else if (peek().type == TokenType.STRING_LITERAL) {
            String stringValue = peek().value;
            consume(TokenType.STRING_LITERAL);
            defaultValue = new ValueDefinition(TokenType.STRING_LITERAL, stringValue);
            // 🪓 If the token type is neither number nor string, warn the scribe
        } else {
            System.out.println("🌀 [LOST IN THE REALM] Expected a number or string but found: " + peek().value);
        }
        return defaultValue;
    }

    /**
     * Parses multiple default values, separated by commas.
     * Invokes parseDefaultValue() for each entry.
     * 🏛️ This is typically used when adding multiple columns or entries
     * that each require an initial default value.
     */

    private List<ValueDefinition> parseDefaultValues() {
        List<ValueDefinition> defaultValues = new ArrayList<>();

        // 🎯 Always parse at least one default value
        defaultValues.add(parseDefaultValue());

        // 🛡️ Continue parsing if more comma-separated values exist
        while (position < tokens.size() && peek().type == TokenType.COMMA) {
            consume(TokenType.COMMA);

            // 🩸 If comma is the last token, the statement is incomplete
            if (position >= tokens.size()) {
                throw new RuntimeException("🗡️ [ARES' BROKEN SPEAR] Comma found but no following value — the army of data stands incomplete!");
            }

            // ⚡ Detect two commas in a row without a value in between
            if (peek().type == TokenType.COMMA) {
                throw new RuntimeException("⚡ [DOUBLE LIGHTNING] Two commas in succession — even Zeus strikes but once at a time!");
            }

            // 🛑 Prevent closing parenthesis from appearing right after a comma
            if (peek().type == TokenType.RIGHT_PAREN) {
                throw new RuntimeException("🪓 [AXE WITH NO TARGET] Comma demands a value — yet the gates close too soon!");
            }

            // 🏹 Parse the next default value
            defaultValues.add(parseDefaultValue());
        }

        return defaultValues;
    }


    /**
     * 🪓 Parses the mystical ADD TABLE incantation to add multiple columns.
     * 🔍 Begins by summoning the first column rune via `parseAlterColumn()`.
     * ➰ Then, as long as the sacred COMMA rune is spotted, continues gathering more column runes.
     * 🚫 Throws [UNFINISHED RITUAL] if a comma is followed by nothing.
     * 🛑 Throws [PREMATURE SEAL] if a right parenthesis is found when columns are still awaited.
     * 📜 Returns the scroll of all columns to be forged into the table.
     */

    private List<ColumnDefinition> parseAlterColumns() {
        List<ColumnDefinition> columnsTobeAdded = new ArrayList<>();

        // 🪄 Invoke the forge to craft the first column
        columnsTobeAdded.add(parseAlterColumn());

        // 🔄 Continue summoning columns if a comma rune is present
        while (peek().type == TokenType.COMMA && position < tokens.size()) {
            consume(TokenType.COMMA);

            // 🚫 Check if the ritual ends abruptly after a comma
            if (position >= tokens.size()) {
                throw new RuntimeException("❌ [UNFINISHED RITUAL] After the comma rune, no column incantation follows.");
            }

            // 🛑 Ensure we don't prematurely seal the ritual with a closing parenthesis
            if (peek().type == TokenType.RIGHT_PAREN) {
                throw new RuntimeException("⛔ [PREMATURE SEAL] Encountered a right paren rune when columns were still to be forged.");
            }

            // ⚔️ Add the next forged column to the list
            columnsTobeAdded.add(parseAlterColumn());
        }

        // 📜 Return the sacred scroll of column runes
        return columnsTobeAdded;
    }

    /**
     * Parse Column Definitions - Handles parsing of comma-separated column list
     * for a CREATE TABLE statement.
     * Features:
     * - Ensures at least one column definition is present.
     * - Validates proper comma placement and prevents trailing/duplicate commas.
     * - Delegates to parseColumnDefinition() for each column, supporting:
     * • INT and VARCHAR(n) types
     * • DEFAULT value clauses
     * • NULL as a valid default
     * Format:
     * column_def1, column_def2, ..., column_defN
     * Error Handling:
     * - Throws @RuntimeException for malformed column lists
     * (missing column after comma, double commas, trailing commas, etc.).
     */

    private List<ColumnDefinition> parseColumnDefinitions() {
        List<ColumnDefinition> columns = new ArrayList<>();
        // At least one column definition is required
        columns.add(parseColumnDefinition());
        // Handle comma-separated additional columns
        while (position < tokens.size() && peek().type == TokenType.COMMA) {
            consume(TokenType.COMMA);

            // Check for trailing comma (comma followed by closing paren)
            if (position >= tokens.size()) {
                throw new RuntimeException("🌊 [TIDE'S END] Comma found but no following column — the sea of definitions ends abruptly!");
            }
            if (peek().type == TokenType.COMMA) {
                throw new RuntimeException("⚡ [DOUBLE LIGHTNING] Two commas in succession — even Zeus strikes but once at a time!");
            }

            if (peek().type == TokenType.RIGHT_PAREN) {
                throw new RuntimeException("🪓 [BROKEN CHAIN] A comma was found where no column follows — the list of columns is shattered and incomplete.");
            }

            columns.add(parseColumnDefinition());
        }
        return columns;
    }

    /**
     * Parses a single column name for the DROP/REMOVE COLUMN command.
     * This method ensures the column name is valid and not missing.
     *
     * @return The validated column name as a String.
     * @throws RuntimeException if no column name is provided or token type is invalid.
     */

    private String parseDropColumnDefinition() {
        // [STEP 1] Ensure we haven't run out of tokens
        if (position >= tokens.size()) {
            throw new RuntimeException("🌪️ [HERMES' VANISHING] Expected column name but reached the end of the scroll — " + "the messenger god has fled before delivering the name!");
        }

        // [STEP 2] Ensure the next token is a valid identifier (column name)
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚔️ [NAMELESS ALTAR] A column name was demanded — yet none was offered to the gods! " + "🛡️ Only pure identifiers (no quotes, no chaos) are accepted.");
        }

        // [STEP 3] Claim the column name for removal
        String colName = peek().value;
        consume(TokenType.IDENTIFIER);
        return colName;
    }

    /**
     * Parses a list of column names for the DROP/REMOVE COLUMN command.
     * Supports comma-separated column names and enforces proper syntax.
     *
     * @return A list of column names to be removed.
     * @throws RuntimeException if syntax rules are violated (extra commas, missing names, etc.).
     */

    private List<String> parseDropColumnDefinitions() {
        List<String> columns = new ArrayList<>();

        // [STEP 1] Require at least one column name
        columns.add(parseDropColumnDefinition());

        // [STEP 2] Parse any additional columns separated by commas
        while (position < tokens.size() && peek().type == TokenType.COMMA) {
            consume(TokenType.COMMA);

            // ❌ Guard against trailing commas without a following name
            if (position >= tokens.size()) {
                throw new RuntimeException("🌊 [TIDE'S END] Comma found but no following column — " + "the sea of definitions ends abruptly, leaving the gods displeased!");
            }

            // ❌ Guard against consecutive commas (,,)
            if (peek().type == TokenType.COMMA) {
                throw new RuntimeException("⚡ [DOUBLE LIGHTNING] Two commas in succession — " + "even Zeus strikes only once before the thunder fades!");
            }

            // ❌ Guard against comma directly before closing parenthesis
            if (peek().type == TokenType.RIGHT_PAREN) {
                throw new RuntimeException("🪓 [BROKEN CHAIN] A comma was found where no column follows — " + "the chain of names is shattered and incomplete!");
            }

            // ✅ Add the next valid column
            columns.add(parseDropColumnDefinition());
        }

        return columns;
    }

    private Condition parseCondition() {
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("🏛️ [NAMELESS PILLAR] No column name stands where destiny decrees — found " + peek().type);
        }
        String columnName = peek().value;
        consume(TokenType.IDENTIFIER);
        TokenType currentType = peek().type;
        ValueDefinition conditionValue;
        if (currentType == TokenType.EQUALS || currentType == TokenType.NOT_EQUALS || currentType == TokenType.LESS_THAN || currentType == TokenType.LESS_THAN_EQUAL || currentType == TokenType.GREATER_THAN || currentType == TokenType.GREATER_THAN_EQUAL) {
            consume(currentType);
        }
        if (peek().type == TokenType.NUMBER_LITERAL) {
            conditionValue = new ValueDefinition(TokenType.NUMBER_LITERAL, peek().value);
            consume(TokenType.NUMBER_LITERAL);
        } else if (peek().type == TokenType.STRING_LITERAL) {
            conditionValue = new ValueDefinition(TokenType.STRING_LITERAL, peek().value);
            consume(TokenType.STRING_LITERAL);
        } else {
            throw new RuntimeException("not found datatype");
        }
        if (currentType == null) {
            throw new RuntimeException("unexpected condition provided");
        }
        return new Condition(columnName, currentType, conditionValue);
    }

    private List<Condition> parseConditions() {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(parseCondition());
//        TokenType currentType = peek().type;
//        while (currentType==TokenType.AND || currentType == TokenType.OR){
//
//        }
        return conditions;
    }

    /**
     * Parses a CREATE DATABASE command.
     * Expected syntax: CREATE DATABASE <realm_name>;
     *
     * @return CreateDatabaseCommand with the new realm's name
     * @throws RuntimeException When:
     *                          - No name is given ("🏛️ [CREATE DATABASE UNNAMED] You invoke creation yet grant no name...")
     *                          - Missing semicolon ("⚔️ [SAGA UNFINISHED] The command lingers in limbo...")
     *                          - Extra tokens after ("🏗️ [YGGDRASIL'S WRATH] 'CREATE DATABASE' was forged truly, yet corrupt runes remain...")
     */

    private CreateDatabaseCommand parseCreateDatabase() {
        consume(TokenType.DATABASE);
        if (position >= tokens.size()) {
            throw new RuntimeException("🏛️ [CREATE DATABASE UNNAMED] You invoke creation yet grant no name — a realm cannot rise from the void without identity!");
        }
        String dbName = peek().value;

        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("💀 [PARSING CATASTROPHE] Realm names must be unquoted!⚔️ Example: CREATE DATABASE Valhalla");
        }

        consume(TokenType.IDENTIFIER);
        if (position >= tokens.size()) {
            throw new RuntimeException("⚔️ [SAGA UNFINISHED] The command lingers in limbo — seal its fate with ';'!");
        }

        consume(TokenType.SEMICOLON);

        // 2. Check for extra tokens (e.g., 'EXTRA_JUNK')
        if (position < tokens.size()) {
            throw new RuntimeException("🏗️ [YGGDRASIL'S WRATH] 'CREATE DATABASE' was forged truly, yet corrupt runes remain — purge the unworthy symbols!");
        }
        return new CreateDatabaseCommand(dbName);
    }


    /**
     * Parses a DROP DATABASE command - the Ragnarök of realms.
     * Expected syntax: DROP DATABASE <realm_name>;
     *
     * @return DropDatabaseCommand with the doomed realm's name
     * @throws RuntimeException When:
     *                          - No name given ("🏛️ [DROP UNGUIDED] You call upon destruction, yet name no realm...")
     *                          - Missing semicolon ("🗡️ [ARES' BROKEN OATH] The deletion rite is incomplete...")
     *                          - Extra tokens ("🔥 [SURTR'S ANNIHILATION] 'DROP DATABASE' was invoked, yet chaos runes persist...")
     */

    private DropDatabaseCommand parseDropDatabase() {
        consume(TokenType.DATABASE);
        String dbName = peek().value;
        if (position >= tokens.size()) {
            throw new RuntimeException("🏛️ [DROP DATABASE UNNAMED] You invoke deletion yet grant no name — a realm cannot rise from the void without identity!");
        }
        consume(TokenType.IDENTIFIER);
        if (position >= tokens.size()) {
            throw new RuntimeException("⚔️ [SAGA UNFINISHED] The command lingers in limbo — seal its fate with ';'!");
        }
        consume(TokenType.SEMICOLON);

        // 2. Check for extra tokens (e.g., 'EXTRA_JUNK')

        if (position < tokens.size()) {
            throw new RuntimeException("🔥 [SURTR'S ANNIHILATION] 'DROP DATABASE' was invoked, yet chaos runes persist — only absolute destruction is permitted!");
        }
        return new DropDatabaseCommand(dbName);
    }

    /**
     * Parses SHOW DATABASES - the Allfather's vision of all realms.
     * Expected syntax: SHOW DATABASES;
     *
     * @return ShowDatabaseCommand to list all realms
     * @throws RuntimeException When:
     *                          - Missing semicolon ("🌑 [VISION INTERRUPTED] 'SHOW DATABASES' spoken, yet the command ends in chaos...")
     *                          - Extra tokens ("⚔️ [FENRIR'S CHAOS] 'SHOW DATABASES' was spoken truly, yet dark runes linger...")
     */

    private ShowDatabaseCommand parseShowDatabase() {
        consume(TokenType.DATABASES);
        if (position >= tokens.size()) {
            throw new RuntimeException("🌑 [VISION INTERRUPTED] 'SHOW DATABASES' spoken, yet the command ends in chaos — the saga demands a closing ';'!");
        }
        consume(TokenType.SEMICOLON);

        // 2. Check for extra tokens (e.g., 'EXTRA_JUNK')
        //Like this SHOW DATABASES;Extra junk

        if (position < tokens.size()) {
            throw new RuntimeException("⚔️ [FENRIR'S CHAOS] 'SHOW DATABASES' was spoken truly,yet dark runes linger — cleanse the command with purity!");
        }
        return new ShowDatabaseCommand();
    }

    /**
     * Parses GET CURRENT DATABASE - Mimir's knowledge of your current realm.
     * Expected syntax: GET CURRENT DATABASE;
     *
     * @return GetCurrentDatabaseCommand revealing your standing location in Yggdrasil
     * @throws RuntimeException When:
     *                          - Extra tokens ("🧠 [MIMIR'S CONFUSION] The knowledge request is polluted with unnecessary runes!")
     */

    private GetCurrentDatabaseCommand parseGetCurrentDatabase() {
        consume(TokenType.CURRENT);
        if (position >= tokens.size()) {
            throw new RuntimeException("⚡ [BLADE OF CHAOS] The path is broken! You must name what you seek after 'CURRENT'.");
        }
        if (peek().type != TokenType.DATABASE) {
            throw new RuntimeException("⚔️ [BLADE OF THE GODS] " + "You dare speak half-formed incantations?! The sacred word 'DATABASE' must be carved into your command!");
        }
        consume(TokenType.DATABASE);

        if (position >= tokens.size()) {
            throw new RuntimeException("🏺 [GREEK FIRE] The Oracle demands closure! A semicolon (;) must seal your command.");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        consume(TokenType.SEMICOLON);

        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [CHAOS STORM] Kratos frowns upon your carelessness! Junk lingers after the command.");
        }

        return new GetCurrentDatabaseCommand();
    }

    /**
     * Parses a USE DATABASE command.
     * Expected format: USE database_name;
     *
     * @throws RuntimeException if:
     *                          - No database name is given ("⚔️ **ENOUGH!** The Bifrost obeys only those who name their destination!")
     *                          - Missing semicolon ("🌉 [BIFROST UNBOUND] The command lingers in limbo — seal its fate with ';'!")
     */

    private UseDatabaseCommand parseUseDatabase() {
        String dbName = peek().value;
        consume(TokenType.IDENTIFIER);
        if (position >= tokens.size()) {
            throw new RuntimeException("⚔️ [WRATH OF KRATOS] The gods demand a semicolon to seal your fate! Your query remains unfinished like Kratos' vengeance - add ';' to complete the ritual!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        consume(TokenType.SEMICOLON);

        // 2. Check for extra tokens (e.g., 'EXTRA_JUNK')
        if (position < tokens.size()) {
            throw new RuntimeException("🌉 [HEIMDALL'S JUDGMENT] 'USE DATABASE' was declared, yet false markings defile the path — only the pure command may pass!");
        }

        return new UseDatabaseCommand(dbName);
    }

    /**
     * ⚔️ [WRATH OF ODIN] Parses the ALTER DATABASE RENAME command.
     * This method calls upon the wisdom of the Allfather to take an ancient realm’s name
     * and bestow upon it a new one. It ensures the ritual follows the sacred syntax:
     * ALTER DATABASE <old_name> RENAME  <new_name>;
     * Each step of the saga is guarded by divine checks—if an intruder dares break the
     * rules, the parser strikes them down with the fury of the Nine Realms.
     *
     * @return AlterDatabaseNameCommand imbued with the will of Asgard.
     * @throws RuntimeException if the syntax defies the sacred laws of Yggdrasil.
     */

    private AlterDatabaseNameCommand parseAlterDatabase() {
        // 1. Consume DATABASE token (already verified by caller)
        consume(TokenType.DATABASE);

        // 2. Verify old database name exists in command
        if (position >= tokens.size()) {
            throw new RuntimeException("🌌 [VOID WHISPER] The old realm name is missing!\n" + "⚔️ Usage: ALTER DATABASE <old_name> RENAME TO <new_name>;");
        }

        // 3. Validate old name is a proper identifier
        String oldName = peek().value;
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("🔥 [FLAMES OF KRATOS] '" + oldName + "' is not a valid realm name!\n" + "🛡️ Names must be unquoted and free of dark runes (; -- ' etc.)");
        }
        consume(TokenType.IDENTIFIER);

        // 4. Ensure RENAME keyword follows
        if (peek().type != TokenType.RENAME) {
            throw new RuntimeException("⚡ [THOR'S JUDGMENT] Expected 'RENAME' but found '" + peek().value + "'!\n" + "🌠 The Allfather demands: ALTER DATABASE <name> RENAME TO <new_name>;");
        }
        consume(TokenType.RENAME);

        // 5. Validate new name is a proper identifier
        String newName = peek().value;
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("🗡️ [MIST OF NIFLHEIM] '" + newName + "' is an unworthy new name!\n" + "⚒️ Example: ALTER DATABASE Valhalla RENAME TO Asgard;");
        }
        consume(TokenType.IDENTIFIER);

        // 6. Verify command termination
        if (position >= tokens.size()) {
            throw new RuntimeException("🌉 [BIFROST UNSEALED] Your command lacks the sacred semicolon (;)!\n" + "📜 Complete your saga properly: ...TO <name>;");
        }

        // 7. Consume semicolon
        if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("💀 [HELHEIM'S GAZE] Expected ';' but found '" + peek().value + "'!\n" + "🛡️ All commands must end with the mark of closure!");
        }
        consume(TokenType.SEMICOLON);

        // 8. Reject any trailing junk tokens
        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [CHAOS STORM] Junk symbols after command!\n" + "⚔️ Only one sacred incantation per line is permitted.");
        }
        // 9. Return the executable command
        return new AlterDatabaseNameCommand(oldName, newName);
    }

    /**
     * 🛡️ [VALHALLA’S DOOR] Parses the USE NONE command.
     * This method seals the gates to the current realm, returning the warrior
     * back to the realm of no database (NONE). It ensures the ritual ends
     * with the sacred mark `;` and punishes any unworthy symbols that follow.
     * Syntax demanded by the gods:
     * USE NONE;;
     *
     * @return ExitDatabaseCommand blessed by Heimdall to guide the warrior out.
     * @throws RuntimeException if chaos runes remain after the command.
     */

    private ExitDatabaseCommand parseExitDatabase() {
        // Expect a placeholder NONE token before the command
        consume(TokenType.NONE);

        // 🛑 Missing semicolon at the end of the command
        if (position >= tokens.size()) {
            throw new RuntimeException("🪓 [BLADE OF OLYMPUS MISPLACED] The exit ritual lacks its final mark — ';' — " + "without it, the Bifrost cannot close!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        // Consume the mandatory semicolon
        consume(TokenType.SEMICOLON);

        // 🛑 Extra symbols or tokens after the semicolon
        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [CHAOS STORM] Kratos roars: 'Your words spill beyond the sacred end!'\n" + "⚔️ Only one sacred incantation per line is permitted.");
        }

        // Return the parsed command
        return new ExitDatabaseCommand();
    }

    //🗿THE CODEX OF TABLES
    // A SCRIBE'S TOOLS, FORGED IN THE FIRES OF MIDGARD

    /**
     * Parse CREATE TABLE Command - Handles the complete CREATE TABLE statement parsing.
     * Expected format: CREATE TABLE table_name (column_definitions);
     * Features:
     * - Validates table name, parentheses matching, and semicolon termination.
     * - Supports column definitions with data types (INT, VARCHAR(n), etc.).
     * - Recognizes and applies DEFAULT value clauses for columns.
     * - Allows NULL as a valid default value when specified.
     * Error Handling:
     * - Throws @RuntimeException for syntax errors (missing commas, unmatched parentheses, etc.).
     * - Rejects invalid datatype/length combinations (e.g., INT with length).
     */

    private CreateTableCommand parseCreateTable() {
        consume(TokenType.TABLE);
        String tableName = peek().value;
        // Ensure table name is present
        if (position >= tokens.size()) {
            throw new RuntimeException("🏛️ [TEMPLE UNNAMED] 'CREATE TABLE' spoken but no table name follows — what shall be built without identity?");
        }

        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚔️ [NAMELESS ALTAR] After 'CREATE TABLE', a table name was demanded — yet none was offered to the gods.");
        }

        consume(TokenType.IDENTIFIER);

        // Check for opening parenthesis
        if (position >= tokens.size()) {
            throw new RuntimeException("🚪 [GATEWAY SEALED] Table name declared but column definitions gateway '(' is missing — entry to structure is barred!");
        }

        if (peek().type != TokenType.LEFT_PAREN) {
            throw new RuntimeException("⛓️ [UNBOUND PARENTHESIS] Expected '(' to begin column definitions, yet it was never cast.");
        }
        consume(TokenType.LEFT_PAREN);

        // Check for empty column list
        if (position >= tokens.size()) {
            throw new RuntimeException("🌪️ [VANISHING COLUMNS] Opening '(' found but column definitions have vanished into the ether!");
        }

        if (peek().type == TokenType.RIGHT_PAREN) {
            throw new RuntimeException("🔥 [VOID OF CREATION] The table was summoned, yet no columns were inscribed upon its stone — a hollow shell unworthy of existence.");
        }

        List<ColumnDefinition> columns = parseColumnDefinitions();

        // Check for closing parenthesis
        if (position >= tokens.size()) {
            throw new RuntimeException("⚖️ [SCALES UNBALANCED] Column definitions complete but closing ')' has fled — balance demands closure!");
        }

        if (peek().type != TokenType.RIGHT_PAREN) {
            throw new RuntimeException("⛓️ [UNBOUND PARENTHESIS] An opening '(' was cast, yet no ')' arose to close the ritual — balance has been forsaken.");
        }
        consume(TokenType.RIGHT_PAREN);

        // Check for semicolon termination
        if (position >= tokens.size()) {
            throw new RuntimeException("⚡ [ZEUS' INCOMPLETE DECREE] Statement structure complete but missing final ';' — even gods must end their proclamations!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        consume(TokenType.SEMICOLON);
        // Ensure no trailing tokens exist
        if (position < tokens.size()) {
            throw new RuntimeException("⚡ [ZEUS' WRATH] Additional tokens linger after the statement — finish what you began!");
        }
        return new CreateTableCommand(tableName, columns);
    }

    /**
     * Parses a DROP TABLE statement and constructs a DropTableCommand.
     * Expected syntax: DROP TABLE <table_name>;
     *
     * @return DropTableCommand containing the name of the table to be dropped.
     */

    private DropTableCommand parseDropTable() {
        // Consume the 'TABLE' keyword after 'DROP'
        consume(TokenType.TABLE);

        // Extract the table name (identifier)
        String tableName = peek().value;

        // Ensure the table name is actually present
        if (position >= tokens.size()) {
            throw new RuntimeException("🏛️ [DROP TABLE UNNAMED] You invoke deletion yet grant no name — a realm cannot rise from the void without identity!");
        }

        // Consume the identifier token representing the table name
        consume(TokenType.IDENTIFIER);

        // Expect a semicolon to properly terminate the statement
        if (position >= tokens.size()) {
            throw new RuntimeException("⚔️ [SAGA UNFINISHED] The command lingers in limbo — seal its fate with ';'!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        consume(TokenType.SEMICOLON);

        // Ensure there are no extra unexpected tokens after the semicolon
        if (position < tokens.size()) {
            throw new RuntimeException("🔥 [SURTR'S ANNIHILATION] 'DROP TABLE' was invoked, yet chaos runes persist — only absolute destruction is permitted!");
        }

        // Return the parsed DropTableCommand with the table name
        return new DropTableCommand(tableName);
    }

    /**
     * 🧠 [PROPHECY DECODED] Parses the sacred incantation 'SHOW TABLES;'
     * and returns a command that reveals all forged realms (tables).
     * 🔍 The parser ensures that:
     * - 'TABLES' follows the sacred keyword 'SHOW'
     * - A mighty semicolon seals the saga
     * - No chaos runes (extra tokens) remain to disrupt the flow
     * 🛑 If the ritual is incomplete or corrupted, the parser shall unleash divine exceptions.
     *
     * @return ShowTablesCommand — a scroll commanding the executor to unveil all tables.
     * @throws RuntimeException if tokens are missing, malformed, or overabundant.
     */

    private ShowTablesCommand parseShowTables() {
        consume(TokenType.TABLES); // 🪓 [COMMAND CONTINUES] Expecting 'TABLES' to follow 'SHOW'

        // 📜 [SAGA SEALED] A semicolon marks the end of the sacred chant.

        if (position >= tokens.size()) {
            throw new RuntimeException("🌑 [VISION INTERRUPTED] 'SHOW TABLES' spoken, yet the command ends in chaos — the saga demands a closing ';'!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        consume(TokenType.SEMICOLON);// 📜 [SAGA SEALED] A semicolon marks the end of the sacred chant.


        // 2. Check for extra tokens (e.g., 'EXTRA_JUNK')
        // 🕳️ [CHAOS LURKS] If dark runes remain, reject the spell.

        if (position < tokens.size()) {
            throw new RuntimeException("⚔️ [FENRIR'S CHAOS] 'SHOW TABLES' was spoken truly,yet dark runes linger — cleanse the command with purity!");
        }
        return new ShowTablesCommand();
    }

    /**
     * Parse INSERT INTO Statement - Handles the complete INSERT INTO statement parsing
     * Expected format: INSERT INTO table_name (column1, column2, ...) VALUES (value1, value2, ...);
     * Validates table name, column list, VALUES keyword, value list, and semicolon termination
     *
     * @throws RuntimeException for various INSERT INTO syntax errors
     */

    public InsertCommand parseInsertStatement() {
        // Consume the mandatory INTO keyword
        consume(TokenType.INTO);
        String tableName = peek().value;
        if (position >= tokens.size()) {
            throw new RuntimeException("🏺 [DIONYSUS' EMPTY CHALICE] 'INSERT INTO' declared but no table name follows — where shall the wine of data flow?");
        }

        // Validate that the table name is an identifier

        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("🎯 [ARTEMIS' LOST TARGET] After 'INSERT INTO', a table name was expected — the hunter's arrow needs its mark!");
        }
        consume(TokenType.IDENTIFIER);

        // Check for opening parenthesis to begin column list
        if (position >= tokens.size()) {
            throw new RuntimeException("🗝️ [HADES' LOCKED GATE] Table name declared but opening '(' is missing — the underworld of data remains sealed!");
        }

        if (peek().type != TokenType.LEFT_PAREN) {
            throw new RuntimeException("🌀 [CYCLONE OF CONFUSION] Expected '(' to begin column list, yet the winds of syntax blow astray!");
        }
        consume(TokenType.LEFT_PAREN);

        // Validate that the column list is not empty
        if (position >= tokens.size()) {
            throw new RuntimeException("🕳️ [VOID OF VALUES] Opening '(' found but column names have vanished — what fields shall receive the offerings?");
        }

        // Ensure at least one column is specified (not immediately closed)
        if (peek().type == TokenType.RIGHT_PAREN) {
            throw new RuntimeException("🏹 [EMPTY QUIVER] The insertion was summoned, yet no columns were named — a hunter cannot shoot without arrows!");
        }
        // Parse the comma-separated list of column names
        List<String> columns = parseColumnInsertStatements();

        // Check for closing parenthesis after column list
        if (position >= tokens.size()) {
            throw new RuntimeException("🌉 [BIFROST BROKEN] Column list complete but closing ')' has fled — the rainbow bridge remains unfinished!");
        }

        if (peek().type != TokenType.RIGHT_PAREN) {
            throw new RuntimeException("⚖️ [THEMIS' IMBALANCE] An opening '(' was cast, yet no ')' arose to restore justice — the scales tip toward chaos!");
        }
        consume(TokenType.RIGHT_PAREN);

        // Ensure VALUES keyword is present after column list
        if (position >= tokens.size()) {
            throw new RuntimeException("💎 [MIDAS' MISSING TREASURE] Column names given but VALUES keyword is absent — where lies the golden data to insert?");
        }

        // Validate the VALUES keyword
        if (peek().type != TokenType.VALUES) {
            throw new RuntimeException("🔮 [ORACLE'S SILENCE] VALUES keyword is missing — the prophecy of insertion remains unspoken!");
        }

        consume(TokenType.VALUES);
        // Check for opening parenthesis to begin values list
        if (peek().type != TokenType.LEFT_PAREN) {
            throw new RuntimeException("🎭 [THEATER OF ERRORS] Expected '(' to begin values list, yet the performance lacks its opening act!");
        }
        consume(TokenType.LEFT_PAREN);

        // Validate that the values list is not empty
        if (position >= tokens.size()) {
            throw new RuntimeException("🍯 [EMPTY HONEYPOT] Opening '(' found but values have vanished — the sweet nectar of data is nowhere to be found!");
        }

        // Ensure at least one value is specified (not immediately closed)
        if (peek().type == TokenType.RIGHT_PAREN) {
            throw new RuntimeException("⚱️ [EMPTY URN] The values were summoned, yet none were placed within — an offering vessel without tribute!");
        }

        // Parse the comma-separated list of values
        List<ValueDefinition> values = parseValuesInsertStatements();
        // Check for closing parenthesis after values list

        if (position >= tokens.size()) {
            throw new RuntimeException("🎪 [CIRCUS WITHOUT FINALE] Values list complete but closing ')' has escaped — even the greatest show needs its curtain call!");
        }

        if (peek().type != TokenType.RIGHT_PAREN) {
            throw new RuntimeException("🏺 [AMPHORA UNSEALED] An opening '(' was cast for values, yet no ')' arose to seal the vessel — the contents shall spill forth!");
        }
        consume(TokenType.RIGHT_PAREN);
        // Check for mandatory semicolon termination
        if (position >= tokens.size()) {
            throw new RuntimeException("📜 [UNFINISHED SCROLL] Insert statement complete but missing final ';' — even divine commands need their sacred seal!");
        }
        if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [WRATH OF KRATOS] The gods demand a semicolon to seal your fate! Found " + peek().value + " instead of the sacred ';' - your query remains unfinished like Kratos' vengeance!");
        }
        consume(TokenType.SEMICOLON);

        // Ensure no extraneous tokens remain after the complete statement
        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [LINGERING SPIRITS] Additional tokens haunt the completed statement — banish these phantoms to complete the ritual!");
        }

        if (columns.size() != values.size()) {
            throw new RuntimeException("⚔️ The AllFather demands equal measures! Columns (" + columns.size() + ") and values (" + values.size() + ") must stand in perfect balance!");
        }
        return new InsertCommand(tableName, columns, values);
    }

    /**
     * ⚔️ [RITUAL OF ALTER TABLE] Interprets the sacred words of the ALTER TABLE RENAME command
     * and forges an AlterTableNameCommand worthy of execution.
     * Functional Saga:
     * 1. 📜 Expect and consume the TABLE rune following the ALTER invocation.
     * - Without it, the ritual loses its purpose.
     * 2. 🏛️ Read the old table name (IDENTIFIER) — the name to be struck from the annals.
     * - If absent or invalid, the ritual fails before it begins.
     * 3. 🗡️ Expect the RENAME rune — the moment of transformation.
     * - Any other rune here is heresy.
     * 4. 🌟 Read the new table name (IDENTIFIER) — the name to be etched into eternity.
     * 5. 🔒 Expect the SEMICOLON rune to seal the ritual.
     * - Without this seal, the realms remain unstable.
     * 6. 🌪️ If any lingering spirits (extra tokens) remain after the seal,
     * banish them with a stern warning.
     * command ALTER TABLE <TABLE_NAME> RENAME <NEW_TABLE_NAME>
     *
     * @return AlterTableNameCommand carrying the old and new names for execution.
     * @throws RuntimeException if syntax is broken or runes are missing.
     */

    private AlterTableNameCommand parseAlterTable() {
        // 1. 📜 Consume the TABLE rune
        consume(TokenType.TABLE);

        // 2. 🏛️ Read the old table name
        if (position >= tokens.size()) {
            throw new RuntimeException("❌ [NAMELESS TABLE] No old name was provided for the ritual.");
        }
        String oldTableName = peek().value;
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("❌ [INVALID RUNE] Expected a table name but received: " + peek().value);
        }
        consume(TokenType.IDENTIFIER);

        // 3. 🗡️ Expect the RENAME rune
        if (position >= tokens.size()) {
            throw new RuntimeException("❌ [BROKEN INCANTATION] Nothing follows the old name — RENAME rune is missing.");
        }
        if (peek().type != TokenType.RENAME) {
            throw new RuntimeException("❌ [HERESY] Expected the sacred RENAME rune but received: " + peek().value);
        }
        consume(TokenType.RENAME);

        // 4. 🌟 Read the new table name
        if (position >= tokens.size()) {
            throw new RuntimeException("❌ [NAMELESS FUTURE] No new name was provided to replace the old.");
        }
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("❌ [INVALID RUNE] Expected a new table name but received: " + peek().value);
        }
        String newTableName = peek().value;
        consume(TokenType.IDENTIFIER);

        // 5. 🔒 Expect the SEMICOLON rune
        if (position >= tokens.size()) {
            throw new RuntimeException("❌ [UNSEALED RITUAL] No semicolon was provided to seal the statement.");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        consume(TokenType.SEMICOLON);

        // 6. 🌪️ Banish lingering spirits (extra tokens)
        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [LINGERING SPIRITS] Additional tokens haunt the completed statement — banish these phantoms to complete the ritual!");
        }

        // Return the forged command
        return new AlterTableNameCommand(oldTableName, newTableName);
    }

    /**
     * ⚔️ [SUMMON ALTER COMMAND]
     * Interprets the full ALTER TABLE ritual to add new columns.
     * Expects a sequence of runes:
     * COLUMN ( <column definitions> ) TO TABLE <tableName> ;
     * Verifies each rune in the order of the ritual.
     * Returns a command object ready for execution in the Great Hall.
     * ⚔️ Parses an ALTER TABLE ADD COLUMN command in the grand tongue of YggraDB.
     * Expected syntax:
     * ADD COLUMN ( <column_definitions> ) TO TABLE <table_name> DEFAULT (<default_values>);
     * Returns: A mighty AlterAddColumnCommand ready for execution by the DatabaseManager.
     */

    private AlterAddColumnCommand parseAlterColumnsofTable() {
        // Step 1: Begin the COLUMN rune sequence — without it, the forge lies dormant.
        consume(TokenType.COLUMN);

        // Step 2: Expect the left paren rune to open the chamber of column definitions.
        if (position >= tokens.size()) {
            throw new RuntimeException("🕳️ [NAMELESS FORGE] You spoke of columns, yet named none. Begin with COLUMN ( ... )");
        }
        if (peek().type != TokenType.LEFT_PAREN) {
            throw new RuntimeException("🚪 [SEALED GATE] Expected ‘(’ to open the forge, but found: " + peek().value);
        }
        consume(TokenType.LEFT_PAREN);

        // Step 3: Summon the column definitions from the runes.
        List<ColumnDefinition> tobeAddedColumns = parseAlterColumns();

        // Step 4: Expect the right paren rune to close the chamber.
        if (peek().type != TokenType.RIGHT_PAREN) {
            throw new RuntimeException("🔓 [OPEN PORTAL] Missing ‘)’ to seal the forge — chaos may spill forth!");
        }
        consume(TokenType.RIGHT_PAREN);

        // Step 5: Expect the TO rune to direct the magic toward its destination.
        if (position >= tokens.size()) {
            throw new RuntimeException("🌌 [LOST SPELL] The ‘TO’ rune is absent — the magic drifts without aim.");
        }
        if (peek().type != TokenType.TO) {
            throw new RuntimeException("🎯 [MISGUIDED MAGIC] Expected the ‘TO’ rune, but found: " + peek().value);
        }
        consume(TokenType.TO);

        // Step 6: Expect the TABLE rune to anchor the ritual to a vessel.
        if (peek().type != TokenType.TABLE) {
            throw new RuntimeException("⚓ [ANCHOR LOST] Without the TABLE rune, the ritual has no vessel to bind.");
        }
        consume(TokenType.TABLE);

        // Step 7: Expect the table name rune to identify the vessel of change.
        if (position >= tokens.size()) {
            throw new RuntimeException("🏚️ [ORPHANED SPELL] No table name given — the power fades into the void.");
        }
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("🛑 [UNWORTHY NAME] Expected a table name, but received: " + peek().value);
        }
        String tableName = peek().value;
        consume(TokenType.IDENTIFIER);

        // Step 8: If the DEFAULT rune is present, gather the divine essences to gift existing rows.
        List<ValueDefinition> defaultValue = null;
        if (peek().type == TokenType.DEFAULT) {
            consume(TokenType.DEFAULT);

            if (peek().type != TokenType.LEFT_PAREN) {
                throw new RuntimeException("🚪 [SEALED CHEST] DEFAULT values must begin with ‘(’. Found: " + peek().value);
            }
            consume(TokenType.LEFT_PAREN);

            // Summon the default values list from the scroll.
            defaultValue = parseDefaultValues();

            if (peek().type != TokenType.RIGHT_PAREN) {
                throw new RuntimeException("🔓 [UNCLOSED CHEST] DEFAULT values must end with ‘)’. Found: " + peek().value);
            }
            consume(TokenType.RIGHT_PAREN);
        }

        // Step 9: Expect the semicolon rune to seal the scroll.
        if (position >= tokens.size()) {
            throw new RuntimeException("📜 [UNCLOSED SCROLL] Missing the ‘;’ to seal this sacred command.");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        consume(TokenType.SEMICOLON);

        // Step 10: Ensure no lingering spirits (tokens) remain after the command.
        if (position < tokens.size()) {
            throw new RuntimeException("👻 [LINGERING SPIRITS] Extra runes remain after the command — banish them!");
        }

        // Step 11: Return the forged command for execution.
        return new AlterAddColumnCommand(tableName, tobeAddedColumns, defaultValue);
    }

    /**
     * ⚔️ [RAGNARÖK'S CLEANSING] Parses a TRUNCATE TABLE command to purge all mortal data from a realm
     * while preserving its sacred structure. Like Kratos wiping out an entire pantheon but leaving their temples standing.
     * Valid Syntax: TRUNCATE TABLE <table_name>;
     * Example: TRUNCATE TABLE Titans;  // Clears all Titans but keeps their hall intact
     *
     * @return TruncateTableCommand - A weaponized command ready for execution
     * @throws RuntimeException - When syntax violates the Laws of the Nine Realms
     */

    private TruncateTableCommand parseTruncateCommand() {
        // [STEP 1] CONSUME TABLE TOKEN - The first rune in the cleansing ritual
        consume(TokenType.TABLE);

        // [STEP 2] VERIFY TABLE NAME EXISTS - No empty purges allowed
        if (position >= tokens.size()) {
            throw new RuntimeException("""
                    🔥 [FLAMES OF THE FORGE] The TRUNCATE spell lacks a target!
                    ⚔️ Syntax: TRUNCATE TABLE <realm_name>;
                    🌌 Example: TRUNCATE TABLE Fallen_Gods;""");
        }

        // [STEP 3] VALIDATE TABLE NAME - Must be a pure identifier (no chaos runes)
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚡ [ODIN'S WRATH] '" + peek().value + "' is an unworthy name for purification!\n" + "🛡️ Names must be:\n" + "   - Unquoted\n" + "   - Free of ; -- ' \\\n" + "📜 Example: TRUNCATE TABLE Valkyries;");
        }

        // [STEP 4] CLAIM THE TABLE NAME - Worthy name confirmed
        String tableName = peek().value;
        consume(TokenType.IDENTIFIER);

        // [STEP 5] ENFORCE COMMAND TERMINATION - All spells must end with ;
        if (position >= tokens.size()) {
            throw new RuntimeException("💀 [HELL'S JUDGMENT] Your TRUNCATE spell lacks the closing ';'!\n" + "🌪️ Complete the ritual properly: TRUNCATE TABLE " + tableName + ";");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        consume(TokenType.SEMICOLON);

        // [STEP 6] REJECT TRAILING CHAOS - No extra symbols after ;
        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [CHAOS ECHOES] Junk symbols linger after the sacred ';'!\n" + "⚔️ The TRUNCATE spell must stand alone in its purity.");
        }

        // [STEP 7] FORGE THE COMMAND - Ready to execute Kratos-style cleansing
        return new TruncateTableCommand(tableName);
    }

    /**
     * Parses a custom "REMOVE FROM TABLE <tableName> (<columnNames>);" or
     * equivalent drop-columns statement into a DropColumnsCommand object.
     * This parser:
     * 1. Validates the presence of the 'FROM' keyword.
     * 2. Ensures a valid table name is provided.
     * 3. Checks that the command syntax includes parentheses containing one or more column names.
     * 4. Validates the required closing semicolon and rejects any trailing tokens after it.
     * 5. Produces a DropColumnsCommand object with the target table and list of columns to remove.
     * Error messages follow the themed style with descriptive hints for the user.
     *
     * @return DropColumnsCommand containing the table name and columns to drop.
     * @throws RuntimeException if syntax rules are violated or required tokens are missing.
     */

    private DropColumnsCommand parseDropColumnsCommand() {
        // Step 1: Expect and consume 'FROM'
        consume(TokenType.FROM);

        // Step 2: Ensure there is a table name after FROM (no empty target allowed)
        if (position >= tokens.size()) {
            throw new RuntimeException("""
                    🔥 [FLAMES OF THE FORGE] The DROP spell lacks a target!
                    ⚔️ Syntax: REMOVE FROM TABLE <realm_name> (<columns...>);
                    🌌 Example: REMOVE FROM TABLE Fallen_Gods (wings, armor);""");
        }

        // Step 3: Validate the TABLE keyword presence
        if (peek().type != TokenType.TABLE) {
            throw new RuntimeException("⚡ [ODIN'S WRATH] '" + peek().value + "' is an unworthy table reference!\n" + "🛡️ Proper format:\n" + "   REMOVE FROM TABLE <name> (...);\n" + "📜 Example: REMOVE FROM TABLE Valkyries (sword, shield);");
        }
        consume(TokenType.TABLE);

        // Step 4: Capture the table name (must be a valid identifier)
        String tableName = peek().value;
        consume(TokenType.IDENTIFIER);

        // Step 5: Expect '(' before column list
        if (peek().type != TokenType.LEFT_PAREN) {
            throw new RuntimeException("⚠️ Left parenthesis '(' required before column list");
        }
        consume(TokenType.LEFT_PAREN);

        // Step 6: Parse the list of column names to be dropped
        List<String> toBeDeletedColumns = parseDropColumnDefinitions();

        // Step 7: Expect closing ")"

        if (peek().type != TokenType.RIGHT_PAREN) {
            throw new RuntimeException("⚠️ Right parenthesis ')' required after column list");
        }
        consume(TokenType.RIGHT_PAREN);

        // Step 8: Expect terminating semicolon ';'
        if (position >= tokens.size()) {
            throw new RuntimeException("🪓 [BLADE OF OLYMPUS MISPLACED] The exit ritual lacks its final mark — ';' — " + "without it, the Bifrost cannot close!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }

        // Consume the mandatory semicolon
        consume(TokenType.SEMICOLON);

        // Step 9: Reject any extra tokens after the semicolon
        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [CHAOS ECHOES] Extra symbols after the sacred ';'!\n" + "⚔️ The DROP command must end cleanly.");
        }

        // Step 10: Return a forged DropColumnsCommand with parsed table name and columns
        return new DropColumnsCommand(toBeDeletedColumns, tableName);
    }

    /**
     * 🏛️ [TEMPLE OF TRANSFORMATION] - RENAME COLUMN COMMAND PARSER
     * ⚔️ [DIVINE PURPOSE]:
     * Parses the sacred RENAME COLUMN command that transforms a column's identity
     * within a table realm, following the ancient syntax blessed by the gods.
     * 📜 [SACRED SYNTAX]:
     * RENAME COLUMN <old_column_name> TO <new_column_name> IN TABLE <table_name>;
     * 🌟 [GODLY EXAMPLE]:
     * RENAME COLUMN cursed_blade TO blessed_sword IN TABLE Weapons;
     * ⚡ [Kratos's METHODOLOGY]:
     * 1. Consumes the COLUMN token (already handled by caller)
     * 2. Captures the old column name destined for transformation
     * 3. Validates and consumes the sacred TO keyword
     * 4. Captures the new column name that shall rise
     * 5. Validates and consumes the IN keyword for realm specification
     * 6. Validates and consumes the TABLE keyword
     * 7. Captures the table name where transformation occurs
     * 8. Seals the command with the sacred semicolon
     * 9. Rejects any chaotic tokens that dare follow
     * 🔥 [RETURN OF THE GODS]:
     * Returns a RenameColumnCommand forged with the old name, table realm, and new name
     * ⚰️ [WRATH OF HADES]:
     * Throws RuntimeException with epic God of War themed messages if syntax sins are detected
     */

    private RenameColumnCommand parseRenameColumnCommand() {
        // ⚔️ [Kratos's FORGE] Begin the sacred ritual of column renaming
        // The COLUMN token has already been consumed by the caller
        consume(TokenType.COLUMN);

        // 🔥 [BLADE OF OLYMPUS] Ensure we have tokens remaining for the old column name
        if (position >= tokens.size()) {
            throw new RuntimeException("""
                    🔥 [FLAMES OF THE FORGE] The RENAME spell lacks a target column!
                    ⚔️ Syntax: RENAME COLUMN <old_name> TO <new_name> IN TABLE <realm_name>;
                    🌌 Example: RENAME COLUMN cursed_blade TO blessed_sword IN TABLE Weapons;""");
        }

        // ⚡ [SPARTAN RAGE] Capture the name of the column to be reforged
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚡ [ODIN'S WRATH] '" + peek().value + "' is not a worthy column name to rename!\n" + "🛡️ Proper format: RENAME COLUMN <old_name> TO <new_name> IN TABLE <table_name>;\n" + "📜 Example: RENAME COLUMN ancient_power TO divine_strength IN TABLE Gods;");
        }
        String oldColumnName = peek().value;
        consume(TokenType.IDENTIFIER);

        // 🌪️ [WINDS OF CHANGE] Expect the sacred TO keyword for transformation
        if (position >= tokens.size()) {
            throw new RuntimeException("""
                    🔥 [FLAMES OF THE FORGE] The RENAME spell requires the sacred 'TO' for transformation!
                    ⚔️ Syntax: RENAME COLUMN <old_name> TO <new_name> IN TABLE <realm_name>;
                    🌌 Example: RENAME COLUMN mortal_strength TO godly_might IN TABLE Heroes;""");
        }

        if (peek().type != TokenType.TO) {
            throw new RuntimeException("⚡ [ZEUS'S THUNDER] Missing the sacred 'TO' in your renaming ritual!\n" + "🛡️ Expected: RENAME COLUMN " + oldColumnName + " TO <new_name> IN TABLE <table_name>;\n" + "📜 The gods demand proper transformation syntax!");
        }
        consume(TokenType.TO);

        // 🏛️ [TEMPLE OF WISDOM] Capture the new name that shall replace the old
        if (position >= tokens.size()) {
            throw new RuntimeException("""
                    🔥 [FLAMES OF THE FORGE] The RENAME spell lacks the new column name!
                    ⚔️ Syntax: RENAME COLUMN <old_name> TO <new_name> IN TABLE <realm_name>;
                    🌌 Example: RENAME COLUMN weak_shield TO aegis_protection IN TABLE Equipment;""");
        }

        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚡ [ARES'S FURY] '" + peek().value + "' is not a worthy new name for the column!\n" + "🛡️ Expected: RENAME COLUMN " + oldColumnName + " TO <new_name> IN TABLE <table_name>;\n" + "📜 Choose a name fit for the gods!");
        }
        String newColumnName = peek().value;
        consume(TokenType.IDENTIFIER);

        // 🌊 [POSEIDON'S DECREE] Expect the IN keyword to specify the realm (table)
        if (position >= tokens.size()) {
            throw new RuntimeException("""
                    🔥 [FLAMES OF THE FORGE] The RENAME spell requires 'IN TABLE' to specify the realm!
                    ⚔️ Syntax: RENAME COLUMN <old_name> TO <new_name> IN TABLE <realm_name>;
                    🌌 Example: RENAME COLUMN old_power TO new_power IN TABLE Artifacts;""");
        }

        if (peek().type != TokenType.IN) {
            throw new RuntimeException("⚡ [ATHENA'S WISDOM] Missing the sacred 'IN' to specify the table realm!\n" + "🛡️ Expected: RENAME COLUMN " + oldColumnName + " TO " + newColumnName + " IN TABLE <table_name>;\n" + "📜 The gods must know which realm to transform!");
        }
        consume(TokenType.IN);

        // 🏺 [PANDORA'S VESSEL] Expect the TABLE keyword before the realm name
        if (position >= tokens.size()) {
            throw new RuntimeException("""
                    🔥 [FLAMES OF THE FORGE] The RENAME spell requires 'TABLE' after 'IN'!
                    ⚔️ Syntax: RENAME COLUMN <old_name> TO <new_name> IN TABLE <realm_name>;
                    🌌 Example: RENAME COLUMN old_relic TO sacred_relic IN TABLE Treasures;""");
        }

        if (peek().type != TokenType.TABLE) {
            throw new RuntimeException("⚡ [HADES'S JUDGMENT] Missing the sacred 'TABLE' keyword!\n" + "🛡️ Expected: RENAME COLUMN " + oldColumnName + " TO " + newColumnName + " IN TABLE <table_name>;\n" + "📜 The underworld demands proper table reference!");
        }
        consume(TokenType.TABLE);

        // 🏛️ [MOUNT OLYMPUS] Capture the name of the table realm to be transformed
        if (position >= tokens.size()) {
            throw new RuntimeException("""
                    🔥 [FLAMES OF THE FORGE] The RENAME spell lacks the table name!
                    ⚔️ Syntax: RENAME COLUMN <old_name> TO <new_name> IN TABLE <realm_name>;
                    🌌 Example: RENAME COLUMN mortal_name TO heroic_title IN TABLE Champions;""");
        }

        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚡ [OLYMPIAN RAGE] '" + peek().value + "' is not a worthy table name!\n" + "🛡️ Expected: RENAME COLUMN " + oldColumnName + " TO " + newColumnName + " IN TABLE <table_name>;\n" + "📜 Name a realm worthy of the gods!");
        }
        String tableName = peek().value;
        consume(TokenType.IDENTIFIER);

        // ⚰️ [FINAL JUDGMENT] Seal the command with the sacred semicolon
        if (position >= tokens.size()) {
            throw new RuntimeException("🔥 [FLAMES OF THE FORGE] The RENAME ritual requires the sacred ';' to complete!\n" + "⚔️ Your command: RENAME COLUMN " + oldColumnName + " TO " + newColumnName + " IN TABLE " + tableName + ";\n" + "📜 The gods demand proper closure!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚡ [FINAL WRATH] Missing the sacred semicolon ';' to seal the renaming ritual!\n" + "🛡️ Complete command: RENAME COLUMN " + oldColumnName + " TO " + newColumnName + " IN TABLE " + tableName + ";\n" + "📜 Without it, the transformation cannot be bound!");
        }
        consume(TokenType.SEMICOLON);

        // 🌪️ [CHAOS PREVENTION] Reject any forbidden tokens after the sacred seal
        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [CHAOS ECHOES] Extra symbols detected after the sacred ';'!\n" + "⚔️ The RENAME command must end cleanly with the semicolon.\n" + "📜 Remove these chaotic remnants: " + tokens.get(position).value);
        }

        // 🎯 [Kratos's TRIUMPH] Forge the command with the captured elements
        return new RenameColumnCommand(oldColumnName, tableName, newColumnName);
    }

    /**
     * 🤺Parses the MODIFY COLUMN command in the form:
     * MODIFY COLUMN (<COLUMN_NAME> <NEW_DATATYPE>) IN TABLE <TABLE_NAME>;
     * Expected grammar:
     * MODIFY COLUMN (column_name datatype [, column_name datatype ...]) IN TABLE table_name;
     * Purpose:
     * This method validates the syntax for modifying one or more column datatypes
     * in a table, throwing thematic error messages if the format is wrong.
     *
     * @return ModifyDatatypeColumn object containing parsed command details (currently null placeholder)
     */

    private ModifyDatatypeColumn parseModifyDataTypeCommand() {

        // Step 1: Expect and consume the COLUMN keyword after MODIFY
        consume(TokenType.COLUMN);

        // Step 2: Expect and consume the LEFT_PAREN to start the modification list
        if (peek().type != TokenType.LEFT_PAREN) {
            throw new RuntimeException("⚡ [BROKEN RUNE] The Allfather demands a '(' to open the column transformation ritual!\n" + "🛡️ Example: MODIFY COLUMN (name VARCHAR(50)) IN TABLE Valhalla;");
        }
        consume(TokenType.LEFT_PAREN);

        List<ColumnDefinition> parsedColumns = parseColumnDefinitions();

        // Step 4: Expect and consume the RIGHT_PAREN to close the modification list
        if (peek().type != TokenType.RIGHT_PAREN) {
            throw new RuntimeException("⚡ [BROKEN RUNE] The ritual circle remains open!\n" + "🌌 Close it with ')' before invoking the table name.");
        }
        consume(TokenType.RIGHT_PAREN);

        // Step 5: Expect and consume the IN keyword
        if (peek().type != TokenType.IN) {
            throw new RuntimeException("⚡ [MISSING RUNE] The path to the table realm is blocked!\n" + "🛡️ Speak the IN rune before naming the table.");
        }
        consume(TokenType.IN);

        // Step 6: Expect and consume the TABLE keyword
        if (peek().type != TokenType.TABLE) {
            throw new RuntimeException("⚡ [BROKEN RUNE] The prophecy demands the TABLE rune!\n" + "🌌 Without TABLE, the realm cannot be summoned.");
        }
        consume(TokenType.TABLE);

        // Step 7: Expect and consume the table name (IDENTIFIER)
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚡ [NAMELESS REALM] The table has no name!\n" + "🛡️ Speak the table's true name to bind the spell.");
        }
        String tableName = peek().value;
        consume(TokenType.IDENTIFIER);

        // Step 8: Expect and consume the SEMICOLON to mark command end
        if (position >= tokens.size()) {
            throw new RuntimeException("🪓 [BLADE OF OLYMPUS MISPLACED] The exit ritual lacks its final mark — ';' — " + "without it, the Bifrost cannot close!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        // Consume the mandatory semicolon
        consume(TokenType.SEMICOLON);

        // Step 9: Ensure there are no extra tokens after the command
        if (position < tokens.size()) {
            throw new RuntimeException("🌪️ [CHAOS ECHOES] Extra symbols linger beyond the sacred ';'!\n" + "⚔️ The MODIFY COLUMN command must end cleanly.");
        }

        return new ModifyDatatypeColumn(tableName, parsedColumns);
    }

    /**
     * Parses a SET DEFAULT VALUE command of the form:
     * SET DEFAULT COLUMN <columnName> IN TABLE <tableName> DEFAULT <value>;
     * Returns a SetDefaultValueColumn object representing the parsed command.
     * Saga-style errors are thrown if tokens are missing or invalid.
     */

    private SetDefaultValueColumn parseSetDefaultValueCommand() {
        // 🔮 Expect the COLUMN keyword
        consume(TokenType.COLUMN);

        // ⚔️ Verify a column name follows
        if (position >= tokens.size()) {
            throw new RuntimeException("⚡ By Odin's beard! You spoke of a COLUMN, yet no name was offered.");
        }
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("🪓 The Norns whisper: A column name was foretold, but instead I see " + peek().value);
        }
        String columnName = peek().value;
        consume(TokenType.IDENTIFIER);

        // 🛡️ Expect the IN keyword
        if (position >= tokens.size()) {
            throw new RuntimeException("🔥 The path ends in shadow... the IN keyword was promised, yet nothing came.");
        }
        if (peek().type != TokenType.IN) {
            throw new RuntimeException("🐍 Midgard Serpent coils — 'IN' was demanded, but instead you gave " + peek().value);
        }
        consume(TokenType.IN);

        // 🏛️ Expect the TABLE keyword
        if (position >= tokens.size()) {
            throw new RuntimeException("🌑 Silence before Ragnarok — TABLE keyword was expected, but void answered.");
        }
        if (peek().type != TokenType.TABLE) {
            throw new RuntimeException("🩸 The gods rage! A TABLE was sought, but you forged " + peek().value + " instead.");
        }
        consume(TokenType.TABLE);

        // ⚒️ Parse the table name
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("⚔️ A table name should rise, but I see only chaos: " + peek().value);
        }
        String tableName = peek().value;
        consume(TokenType.IDENTIFIER);

        // 🌌 Expect DEFAULT keyword before value
        consume(TokenType.DEFAULT);

        // 🪶 Parse the default value (string or number)
        ValueDefinition defaultValue;
        if (peek().type == TokenType.STRING_LITERAL) {
            defaultValue = new ValueDefinition(TokenType.STRING_LITERAL, peek().value);
            consume(TokenType.STRING_LITERAL);
        } else if (peek().type == TokenType.NUMBER_LITERAL) {
            defaultValue = new ValueDefinition(TokenType.NUMBER_LITERAL, peek().value);
            consume(TokenType.NUMBER_LITERAL);
        } else {
            throw new RuntimeException("🌋 The realm trembles — an unknown value awakens: " + peek().value);
        }

        // 🛡️ Ensure the final SEMICOLON is present
        if (position >= tokens.size()) {
            throw new RuntimeException("🪓 [BLADE OF OLYMPUS MISPLACED] The exit ritual lacks its final mark — ';' — " + "without it, the Bifrost cannot close!");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚔️ [FURY OF THE GODS] The ritual demands a semicolon, not '" + peek().value + "'! Even the Blades of Chaos cannot cut through syntax without proper closure!");
        }
        // Consume the mandatory semicolon
        consume(TokenType.SEMICOLON);

        // 👁️ Ensure no extra tokens lurk after the semicolon
        if (position < tokens.size()) {
            throw new RuntimeException("👁️ The Fates hiss: Something lurks beyond the end... a shadow not meant to be.");
        }

        // 🏹 Return a fully parsed command object
        return new SetDefaultValueColumn(tableName, columnName, defaultValue);
    }

    /**
     * 🎯 Parses a `DROP DEFAULT` SQL command.
     * Expected syntax:
     * DROP DEFAULT FOR COLUMN <columnName> IN TABLE <tableName>;
     * Flow:
     * 1. Validate mandatory keywords in order
     * 2. Extract column and table names
     * 3. Enforce semicolon termination
     * 4. Return AST node (DropDefaultValueColumn)
     * Errors are themed in a God of War–style.
     */

    private DropDefaultValueColumn parseDropDefaultValueColumn() {

        // ⚡ STEP I: Expect the DEFAULT keyword (after DROP)
        if (position >= tokens.size()) {
            throw new RuntimeException("🌑 [VOID CLAIMS ALL] DEFAULT was demanded, but no words remain.");
        }
        if (peek().type != TokenType.DEFAULT) {
            throw new RuntimeException("🌑 [SHADOW OF DECEIT] DEFAULT was foretold, yet you speak: " + peek().value);
        }
        consume(TokenType.DEFAULT);

        // ⚔️ STEP II: Expect the FOR keyword
        if (position >= tokens.size()) {
            throw new RuntimeException("🩸 [SILENCE OF THE NORN] The Fates awaited 'FOR', but the scroll ends.");
        }
        if (peek().type != TokenType.FOR) {
            throw new RuntimeException("🩸 [OATHBROKEN] The word 'FOR' must guide this path, not " + peek().value);
        }
        consume(TokenType.FOR);

        // 🛡️ STEP III: Expect the COLUMN keyword
        if (position >= tokens.size()) {
            throw new RuntimeException("🐍 [SERPENT’S HUNGER] The saga ends before COLUMN could be named.");
        }
        if (peek().type != TokenType.COLUMN) {
            throw new RuntimeException("🐍 [COILS OF JÖRMUNGANDR] COLUMN was demanded, yet you forged " + peek().value);
        }
        consume(TokenType.COLUMN);

        // 🎯 STEP IV: Parse the column name
        if (position >= tokens.size()) {
            throw new RuntimeException("💀 [NAMELESS CURSE] COLUMN was called, but no name follows.");
        }
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("💀 [NAMELESS CURSE] A column name must rise, but instead came " + peek().value);
        }
        String columnName = peek().value;
        consume(TokenType.IDENTIFIER);

        // 🏛️ STEP V: Expect the IN keyword
        if (position >= tokens.size()) {
            throw new RuntimeException("🔥 [BROKEN REALM] The prophecy ends before IN could appear.");
        }
        if (peek().type != TokenType.IN) {
            throw new RuntimeException("🔥 [BROKEN REALM] IN was sought, but " + peek().value + " was found.");
        }
        consume(TokenType.IN);

        // 🏹 STEP VI: Expect the TABLE keyword
        if (position >= tokens.size()) {
            throw new RuntimeException("⚔️ [SILENT PILLARS] The saga ends before TABLE could stand.");
        }
        if (peek().type != TokenType.TABLE) {
            throw new RuntimeException("⚔️ [SILENT PILLARS] TABLE must stand here, not " + peek().value);
        }
        consume(TokenType.TABLE);

        // 🛡️ STEP VII: Parse the table name
        if (position >= tokens.size()) {
            throw new RuntimeException("🌪️ [NAMELESS TEMPLE] The table has no name — void consumes it.");
        }
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("🌪️ [NAMELESS TEMPLE] A table name was foretold, not " + peek().value);
        }
        String tableName = peek().value;
        consume(TokenType.IDENTIFIER);

        // 🔒 STEP VIII: Expect semicolon to end statement
        if (position >= tokens.size()) {
            throw new RuntimeException("⚡ [BIFRÖST SUNDERED] The saga ends without its final seal ';'.");
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException("⚡ [BIFRÖST SUNDERED] A semicolon must close fate, not " + peek().value);
        }
        consume(TokenType.SEMICOLON);

        // 👁️ STEP IX: Ensure no trailing tokens exist
        if (position < tokens.size()) {
            throw new RuntimeException("👁️ [WHISPERS BEYOND] Shadows remain past the end... " + peek().value);
        }

        return new DropDefaultValueColumn(tableName, columnName);
    }


    /**
     * 🌌 [SEER’S DIVINATION] 🌌
     * Parses a `SELECT` SQL command from the token stream.
     * The SELECT command in YggraDB follows this structure:
     * SELECT <columns> FROM <tableName>;
     * Columns can be either:
     * - `*` (represented internally as ["ALL"])
     * - A list of identifiers (e.g., ["id", "name"])
     * This method consumes tokens from the input and constructs a {@link SelectCommand}.
     * Any violation of the expected grammar results in a God of War–style runtime error.
     * ⚡ Responsibilities:
     * - Validate that columns are properly specified (`*` or identifiers).
     * - Ensure the `FROM` keyword appears after the column list.
     * - Validate that a table name follows `FROM`.
     * - Ensure the query is terminated by a semicolon.
     * - Guard against stray tokens beyond the query.
     * 🛡️ Error Handling:
     * - If no `*` or identifiers found → raise: "❌ [FATE TWISTED] A SELECT must choose runes (* or identifiers), not this shadow."
     * - If `FROM` is missing → raise: "⚔️ [REALM UNCHOSEN] The path falters — 'FROM' is demanded by fate, not " + peek().value
     * - If table name missing → raise: "🏛️ [NAMELESS REALM] No table name stands where destiny decrees — found " + peek().value
     * - If semicolon missing → raise: "⚡ [BIFRÖST SUNDERED] A semicolon must close fate, not " + peek().value
     * - If extra tokens linger → raise: "👁️ [WHISPERS BEYOND] Shadows remain past the end... " + peek().value
     */

    private SelectCommand parseSelectCommand() {
        // 🔮 STEP I: Ensure SELECT targets are valid (either * or identifiers)
        if (peek().type != TokenType.ASTERISK && peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException(
                    "❌ [THREADS CUT] The Norns decree — SELECT may summon only * or named runes, not '" + peek().value + "'!"
            );
        }

        List<String> columns = new ArrayList<>(); // for select statement columns
        // 🟊 STEP II: Handle `SELECT *`
        if (peek().type == TokenType.ASTERISK) {
            consume(TokenType.ASTERISK);
            columns.add("ALL");
            // 📜 STEP III: Handle `SELECT col1, col2, ...`
        } else if (peek().type == TokenType.IDENTIFIER) {
            columns = parseColumnInsertStatements();
        } else {
            throw new RuntimeException(
                    "❌ [RUNE SHATTERED] The seer finds no path — SELECT cannot bind '" + peek().value + "'."
            );
        }

        // 🏛️ STEP IV: Expect `FROM`
        if (peek().type != TokenType.FROM) {
            throw new RuntimeException(
                    "⚔️ [REALM UNBOUND] The roots of Yggdrasil demand 'FROM', yet '" + peek().value + "' was found."
            );
        }
        consume(TokenType.FROM);

        // 🏷️ STEP V: Expect table name
        String tableName = peek().value;
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException(
                    "🏛️ [NAMELESS REALM] The World Tree sees no table — only a void where '" + tableName + "' stands."
            );
        }
        consume(TokenType.IDENTIFIER);

        // 🌀 STEP VI: Handle optional WHERE
        List<Condition> conditions = null;
        if (peek().type == TokenType.WHERE) {
            consume(TokenType.WHERE);
            conditions = parseConditions();
        }

        // 🪓 STEP VII: Expect semicolon
        if (position >= tokens.size()) {
            throw new RuntimeException(
                    "🔥 [RAGNARÖK AWAKENS] The saga ends too soon — a semicolon seals the fate of queries!"
            );
        } else if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException(
                    "⚔️ [FATE SUNDERED] The ritual demands ';' — not '" + peek().value +
                            "'. Even Mjölnir cannot strike true without closure!"
            );
        }
        consume(TokenType.SEMICOLON);

        // 👁️ STEP VIII: Ensure no trailing tokens
        if (position < tokens.size()) {
            throw new RuntimeException(
                    "👁️ [HUGINN & MUNINN WARN] Ravens whisper of stray runes beyond the end — '" + peek().value + "'."
            );
        }

        // 🎇 STEP IX: Return parsed command
        if (conditions == null)
            return new SelectCommand(tableName, columns);
        else
            return new SelectCommand(tableName, columns, conditions);
    }

    private DeleteCommand parseDeleteCommand() {
        // Ensure table name exists
        consume(TokenType.FROM);
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException(
                    "❌ [DELETE ERROR] Expected table name, but found '" + peek().value + "'"
            );
        }

        String tableName = peek().value;
        consume(TokenType.IDENTIFIER);

        Condition condition = null;

        // Optional WHERE clause
        if (peek().type == TokenType.WHERE) {
            consume(TokenType.WHERE);

            if (peek().type != TokenType.IDENTIFIER) {
                throw new RuntimeException(
                        "❌ [DELETE ERROR] Expected column name after WHERE, but found '" + peek().value + "'"
                );
            }

            String columnName = peek().value;
            consume(TokenType.IDENTIFIER);

            TokenType operator = peek().type;
            consume(operator);

            ValueDefinition value = new ValueDefinition(peek().type, peek().value);
            consume(peek().type);

            condition = new Condition(columnName, operator, value);
        }

        // ✅ Check for semicolon
        if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException(
                    "❌ [DELETE ERROR] Expected ';' at the end of DELETE statement, but found '" + peek().value + "'"
            );
        }
        consume(TokenType.SEMICOLON);

        // ✅ Ensure nothing remains after the semicolon
        if (position < tokens.size()) {
            throw new RuntimeException(
                    "❌ [DELETE ERROR] Unexpected tokens after ';'. DELETE statement must end here."
            );
        }

        return new DeleteCommand(tableName, condition);
    }

    // ⛓️ STEP VI: Ensure semicolon terminates the query

    /**
     * Parse - Main entry point for parsing SQL commands
     * Determines command type (CREATE or INSERT) and delegates to appropriate parser
     * Handles top-level parsing errors with comprehensive error reporting
     */

    public SQLCommand parse() {
        try {
            // Ensure we have at least one token to examine
            if (tokens.isEmpty()) {
                throw new RuntimeException("🏺 [EMPTY VESSEL] No tokens to parse — the vessel of commands stands empty!");
            }
            Token first = peek();

            if (first.type == TokenType.CREATE) {
                advance();
                // Validate that CREATE is followed by TABLE
                if (position >= tokens.size()) {
                    throw new RuntimeException("🔨 [FORGE OF THE GODS SILENT] 'CREATE' declared, yet the forge stands idle — TABLE or DATABASE expected, but void answered!");
                }
                Token second = peek();
                if (second.type == TokenType.TABLE) {
                    return parseCreateTable();
                } else if (second.type == TokenType.DATABASE) {
                    return parseCreateDatabase();
                } else {
                    throw new RuntimeException("🏛️ [ARCHITECT'S CONFUSION] 'CREATE' invoked, yet '" + second.value + "' follows — only TABLE or DATABASE may rise from the forge of Yggra!");
                }

                // PARSE INSERT INTO COMMAND
            } else if (first.type == TokenType.INSERT) {
                advance();
                // Validate that INSERT is followed by INTO
                if (position >= tokens.size()) {
                    throw new RuntimeException("🎯 [ARTEMIS' MISSING TARGET] 'INSERT' declared but INTO what realm? Target specification missing!");
                }
                Token second = peek();
                if (second.type != TokenType.INTO) {
                    throw new RuntimeException("🌊 [POSEIDON'S MISDIRECTION] 'INSERT' found but '" + second.value + "' follows — the data must flow INTO a table!");
                }
                return parseInsertStatement();
            } else if (first.type == TokenType.DROP) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("🏛️ [DROP UNGUIDED] You call upon destruction, yet name no realm — the void demands a target!");
                }
                Token second = peek();
                // PARSE DROP DATABASE COMMAND;
                if (second.type == TokenType.DATABASE) {
                    return parseDropDatabase();
                    // PARSE DROP TABLE COMMAND;
                } else if (second.type == TokenType.TABLE) {
                    return parseDropTable();
                    // PARSE DROP DEFAULT VALUE COMMAND;
                } else if (second.type == TokenType.DEFAULT) {
                    return parseDropDefaultValueColumn();
                } else {
                    throw new RuntimeException("🌀 [REALM MISALIGNED] 'DROP' spoken, but '" + second.value + "' stands in defiance — only DATABASE AND TABLE may be struck down!");
                }

            } else if (first.type == TokenType.SHOW) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("🌌 [BLIND BIFROST] You invoke 'SHOW', yet the bridge to knowledge lies broken — name what must be unveiled!");
                }
                Token second = peek();
                if (second.type == TokenType.DATABASES) {
                    return parseShowDatabase();
                } else if (second.type == TokenType.CURRENT) {
                    return parseGetCurrentDatabase();
                } else if (second.type == TokenType.TABLES) {
                    return parseShowTables();
                } else {
                    throw new RuntimeException("🌀 [VISION DISTORTED] 'SHOW' spoken, yet '" + second.value + "' clouds the truth — only DATABASES OR CURRENT OR TABLES can be unveiled!");
                }
                // PARSE SHOW COMMAND;

            } else if (first.type == TokenType.USE) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("🌉 [BIFROST UNBOUND] You seek passage, yet name no realm — which world shall your will command?");
                }
                Token second = peek();
                if (second.type == TokenType.IDENTIFIER) {
                    return parseUseDatabase();
                }
                // PARSE SHOW COMMAND;
                else if (second.type == TokenType.NONE) {
                    return parseExitDatabase();
                } else {
                    throw new RuntimeException("🌀 [REALM MISCAST] 'USE' spoken, yet '" + second.value + "' defies the gods — only a valid realm name may follow!");
                }
            } else if (peek().type == TokenType.ALTER) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("""
                            ⚡ [BROKEN RUNE] ALTER command incomplete!
                            🛡️ You must specify: ALTER DATABASE <name> RENAME  <new_name>
                            🌌 Example: ALTER DATABASE Valhalla RENAME  Asgard or ALTER TABLE Valhalla RENAME  Asgard.""");
                }

                Token second = peek();
                if (second.type == TokenType.DATABASE) {
                    if (position >= tokens.size()) {
                        throw new RuntimeException("""
                                🌪️ [CHAOS WHISPER] No target specified for ALTER!
                                ⚔️ Valid forms:
                                   ALTER DATABASE <name> RENAME  <new_name>.""");
                    }
                    return parseAlterDatabase();
                } else if (second.type == TokenType.TABLE) {
                    if (position >= tokens.size()) {
                        throw new RuntimeException("""
                                🌪️ [CHAOS WHISPER] No target specified for ALTER!
                                ⚔️ Valid forms:
                                   ALTER TABLE <name> RENAME  <new_name>.""");
                    }
                    return parseAlterTable();
                } else {
                    throw new RuntimeException("⚔️ [WRATH OF THE ALLFATHER] Kratos bellows: 'Only the realms themselves (databases) " + "and their great halls (tables) may be reshaped by my hand!'\n" + "🪓 All other alterations are but whispers to the wind — unworthy of the forge!");

                }
            } else if (peek().type == TokenType.ADD) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("""
                            ⚡ [BROKEN RUNE] ALTER command incomplete!
                            🛡️ You must specify: ADD COLUMN (<column_name,datatype>)  TO TABLE <table_name>
                            🌌 Example: ADD COLUMN (Valhalla INT) TO TABLE Asgard.""");
                }
                Token second = peek();
                if (second.type != TokenType.COLUMN) {
                    throw new RuntimeException("⚡ By Odin’s beard! The 'COLUMN' rune was foretold, yet you bring me '" + second.value + "' instead!");
                }

                return parseAlterColumnsofTable();
            } else if (peek().type == TokenType.TRUNCATE) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("""
                            ⚡ [BROKEN RUNE] TRUNCATE command incomplete!
                            🛡️ You must specify: TRUNCATE TABLE table_name
                            🌌 Example: TRUNCATE TABLE Valhalla""");
                }
                Token second = peek();
                if (second.type != TokenType.TABLE) {
                    throw new RuntimeException("⚡ [BROKEN RUNE] The prophecy called for the TABLE rune, " + "yet you dare present '" + second.value + "'! " + "Summon the TABLE rune to proceed through the Bifrost.");
                }
                return parseTruncateCommand();
            } else if (peek().type == TokenType.REMOVE) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("""
                            ⚡ [BROKEN RUNE] REMOVE command incomplete!
                            🛡️ You must specify: REMOVE FROM TABLE table_name (columns)
                            🌌 Example: REMOVE FROM TABLE Valhalla (id,name).""");
                }
                Token second = peek();
                if (second.type != TokenType.FROM) {
                    throw new RuntimeException("⚡ [BROKEN RUNE] The prophecy called for the FROM rune, " + "yet you dare present '" + second.value + "'! " + "Summon the FROM rune to proceed through the Bifrost.");
                }
                return parseDropColumnsCommand();

            } else if (peek().type == TokenType.RENAME) {
                advance();

                if (position >= tokens.size()) {
                    throw new RuntimeException("""
                            ⚡ [BROKEN RUNE] RENAME command incomplete!
                            🛡️ You must specify: RENAME COLUMN <OLD_COLUMN_NAME> TO <NEW_COLUMN_NAME> IN TABLE <TABLE_NAME>
                            🌌 Example: RENAME COLUMN age TO years IN TABLE warriors.""");
                }

                Token second = peek();
                if (second.type != TokenType.COLUMN) {
                    throw new RuntimeException("⚡ [BROKEN RUNE] The prophecy spoke of the COLUMN rune, " + "yet you offer '" + second.value + "'! " + "Summon the COLUMN rune to reshape destiny.");
                }

                return parseRenameColumnCommand();
            } else if (peek().type == TokenType.MODIFY) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("""
                            ⚡ [BROKEN RUNE] The MODIFY ritual is incomplete!
                            🛡️ You must speak the full incantation:
                            MODIFY COLUMN <COLUMN_NAME> <NEW_DATATYPE> IN TABLE <TABLE_NAME>;
                            🌌 Example: MODIFY COLUMN age INT IN TABLE Midgardians;
                            """);
                }
                Token second = peek();
                if (second.type != TokenType.COLUMN) {
                    throw new RuntimeException("⚡ [BROKEN RUNE] The prophecy demanded the COLUMN rune, " + "yet you brandish '" + second.value + "'! " + "Summon the COLUMN rune to channel the Allfather's will.");
                }
                return parseModifyDataTypeCommand();
            } else if (peek().type == TokenType.SET) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("""
                            ⚡ [BROKEN RUNE] The MODIFY ritual is incomplete!
                            🛡️ You must speak the full incantation:
                            SET COLUMN <COLUMN_NAME>  IN TABLE <TABLE_NAME> TO <DEFAULT_VALUE>;
                            🌌 Example: MODIFY COLUMN age INT IN TABLE Midgardians;
                            """);
                }
                Token second = peek();
                if (second.type != TokenType.COLUMN) {
                    throw new RuntimeException("⚡ [BROKEN RUNE] The prophecy demanded the COLUMN rune, " + "yet you brandish '" + second.value + "'! " + "Summon the COLUMN rune to channel the Allfather's will.");
                }
                return parseSetDefaultValueCommand();
            } else if (peek().type == TokenType.SELECT) {
                advance();
                return parseSelectCommand();
            } else if (peek().type == TokenType.DELETE) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException("""
                            ⚡ [BROKEN RUNE] The DELETE ritual is incomplete!
                            🛡️ You must speak the full incantation:
                            DELETE FROM <table_name> [WHERE <condition>];
                            🌌 Examples: DELETE FROM users WHERE age > 25;
                            """);
                }
                Token second = peek();
                if (second.type != TokenType.FROM) {
                    throw new RuntimeException("⚡ [BROKEN RUNE] The DELETE prophecy demands the FROM rune, " +
                            "yet you brandish '" + second.value + "'! " +
                            "Summon the FROM rune after DELETE to channel the Allfather's will.\n" +
                            "📜 Correct syntax: DELETE FROM <table_name> [WHERE <condition>];");
                }
                return parseDeleteCommand();
            } else {
                throw new RuntimeException("⛓️ [CHAINS OF FATE] The Oracle rejects your words! \n" + "👉 Expected one of: CREATE, INSERT, DROP, SHOW, USE, ALTER, ADD, TRUNCATE, REMOVE, RENAME, MODIFY, SET ,DEFAULT,SELECT.\n" + "❌ But instead received: " + first.type + " ('" + first.value + "').\n" + "⚔️ Only these divine runes may command the realms of Yggra!");
            }

        } catch (Exception e) {
            // Enhanced error reporting with parser state information
            System.err.println("💀 [PARSING CATASTROPHE] Error at position " + position + (position < tokens.size() ? " near token: '" + tokens.get(position).value + "'" : " (end of input)"));
            System.err.println("🔥 [FLAMES OF ERROR] " + e.getMessage());
            return null;
        }
    }

}


