package com.yggra.parser;

import com.yggra.commands.*;

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
     * Parse Column Definition - Handles parsing of individual column specifications
     * Expected format: column_name column_type [constraints]
     * Supports INT and VARCHAR(n) data types
     *
     * @throws RuntimeException for various syntax errors in column definitions
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
            return new ColumnDefinition(colName, TokenType.INT, -1);

        } else if (typeToken.type == TokenType.VARCHAR) {
            consume(TokenType.VARCHAR);
            // VARCHAR requires a (NUMBER) specification
            if (position >= tokens.size()) {
                throw new RuntimeException("📏 [BROKEN MEASURE] VARCHAR declared but size specification missing — how vast shall this text domain be?");
            }

            if (peek().type != TokenType.LEFT_PAREN) {
                throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] VARCHAR demands size specification with '(' — the boundaries must be defined!");
            }
            consume(TokenType.LEFT_PAREN);

            if (position >= tokens.size()) {
                throw new RuntimeException("🔢 [APOLLO'S COUNT LOST] Expected size number for VARCHAR but found void — the count has been forgotten!");
            }

            if (peek().type != TokenType.NUMBER_LITERAL) {
                throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] Expected a valid Number literal for VARCHAR size!");
            }
            int size = parseInt(peek().value);
            consume(TokenType.NUMBER_LITERAL);

            if (position >= tokens.size()) {
                throw new RuntimeException("🌉 [BRIDGE UNFINISHED] VARCHAR size given but closing ')' missing — the specification remains incomplete!");
            }

            if (peek().type != TokenType.RIGHT_PAREN) {
                throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] Expected a valid RIGHT_PAREN to close VARCHAR size specification!");
            }
            consume(TokenType.RIGHT_PAREN);
            return new ColumnDefinition(colName, TokenType.VARCHAR, size);
        } else {
            throw new RuntimeException("⚔️ [BLADE OF OLYMPUS] Expected a valid column type (INT or VARCHAR), but found: " + typeToken.value);
        }
    }

    /**
     * Parse Column Insert Statements - Handles parsing of comma-separated column names or values
     * Used for both column lists and value lists in INSERT statements
     * Format: item1, item2, ..., itemN (where items can be column names or values)
     *
     * @throws RuntimeException for malformed lists (trailing commas, double commas, etc.)
     */

    private List<ColumnDefinition> parseColumnInsertStatements() {
        // Parse the first column name or value (at least one is required)
        List<ColumnDefinition> columns = new ArrayList<>();
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
     * Parse Column Insert Statement - Handles parsing of individual column names or values
     * Accepts identifiers (column names), number literals, or string literals
     * Used for both INSERT column specifications.
     *
     * @throws RuntimeException if no valid token is found at current position
     */
    private ColumnDefinition parseColumnInsertStatement() {
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
            throw new RuntimeException("⚔️ [WRONG TRIBUTE] The value offered does not match the column's essence — the gods reject this false gift!");
        }
        return new ColumnDefinition(columnName);
    }

    /**
     * Parse VALUES Insert Statement - Handles parsing of individual values .
     * Accepts values, numbers, or strings
     * Used for INSERT value specifications
     *
     * @throws RuntimeException if no valid token is found at current position
     */

    private ValueDefinition parseValuesInsertStatement() {
        // Ensure we have a token to examine
        if (position >= tokens.size()) {
            throw new RuntimeException("📦 [EMPTY OFFERING] Expected value for insertion but found void — the gods demand tribute!");
        }

        if (peek().value.contains("--") || peek().value.contains(";") || peek().value.matches(".*\\W&&[^_].*")) {
            throw new RuntimeException(
                    "🌪️ [CHAOS STORM] " +
                            "Data values cannot contain ';' or '--'!"
            );
        }
        // Accept various token types for INSERT operations:
        if (peek().type == TokenType.NUMBER_LITERAL) {
            // Column names are just consumed here
            String value = peek().value;
            consume(TokenType.NUMBER_LITERAL);
            return new ValueDefinition(TokenType.NUMBER_LITERAL, value);
        } else if (peek().type == TokenType.STRING_LITERAL) {
            String value = peek().value;
            consume(TokenType.STRING_LITERAL);
            return new ValueDefinition(TokenType.STRING_LITERAL, value);
        } else {
            // For extensibility - other data types may be supported in future
            // Currently just logs the occurrence without throwing an error
            throw new RuntimeException("⚔️ [WRONG TRIBUTE] The value offered does not match the column's essence — the gods reject this false gift!");
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

    private Object parseDefaultValue() {
        Object defaultValue = null;

        // ⚔️ If the token is a numeric literal, take it as the default value
        if (peek().type == TokenType.NUMBER_LITERAL) {
            defaultValue = peek().value;
            consume(TokenType.NUMBER_LITERAL);

            // 🪶 If the token is a string literal, accept it as the default value
        } else if (peek().type == TokenType.STRING_LITERAL) {
            defaultValue = peek().value;
            consume(TokenType.STRING_LITERAL);

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

    private List<Object> parseDefaultValues() {
        List<Object> defaultValues = new ArrayList<>();

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
     * Ensures proper comma placement and validates column definition syntax
     * Format: column_def1, column_def2, ..., column_defN
     *
     * @throws RuntimeException for malformed column lists
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
     * Parse CREATE TABLE Command - Handles the complete CREATE TABLE statement parsing
     * Expected format: CREATE TABLE table_name (column_definitions);
     * Validates table name, parentheses matching, and semicolon termination
     *
     * @throws RuntimeException for various CREATE TABLE syntax errors
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
        }

        consume(TokenType.SEMICOLON);
        // Ensure no trailing tokens exist
        if (position < tokens.size()) {
            throw new RuntimeException("⚡ [ZEUS' WRATH] Additional tokens linger after the statement — finish what you began!");
        }
        return new CreateTableCommand(tableName, columns);
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
            throw new RuntimeException(
                    "⚔️ [BLADE OF THE GODS] " +
                            "You dare speak half-formed incantations?! The sacred word 'DATABASE' must be carved into your command!"
            );
        }
        consume(TokenType.DATABASE);

        if (position >= tokens.size()) {
            throw new RuntimeException("🏺 [GREEK FIRE] The Oracle demands closure! A semicolon (;) must seal your command.");
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
            throw new RuntimeException("⚔️ **ENOUGH!** The Bifrost obeys only those who *name their destination*. (Missing database after `USE`, boy.)");
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
     * ALTER DATABASE <old_name> RENAME TO <new_name>;
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
            throw new RuntimeException(
                    "🌌 [VOID WHISPER] The old realm name is missing!\n" +
                            "⚔️ Usage: ALTER DATABASE <old_name> RENAME TO <new_name>;"
            );
        }

        // 3. Validate old name is a proper identifier
        String oldName = peek().value;
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException(
                    "🔥 [FLAMES OF KRATOS] '" + oldName + "' is not a valid realm name!\n" +
                            "🛡️ Names must be unquoted and free of dark runes (; -- ' etc.)"
            );
        }
        consume(TokenType.IDENTIFIER);

        // 4. Ensure RENAME keyword follows
        if (peek().type != TokenType.RENAME) {
            throw new RuntimeException(
                    "⚡ [THOR'S JUDGMENT] Expected 'RENAME' but found '" + peek().value + "'!\n" +
                            "🌠 The Allfather demands: ALTER DATABASE <name> RENAME TO <new_name>;"
            );
        }
        consume(TokenType.RENAME);

        // 5. Validate new name is a proper identifier
        String newName = peek().value;
        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException(
                    "🗡️ [MIST OF NIFLHEIM] '" + newName + "' is an unworthy new name!\n" +
                            "⚒️ Example: ALTER DATABASE Valhalla RENAME TO Asgard;"
            );
        }
        consume(TokenType.IDENTIFIER);

        // 6. Verify command termination
        if (position >= tokens.size()) {
            throw new RuntimeException(
                    "🌉 [BIFROST UNSEALED] Your command lacks the sacred semicolon (;)!\n" +
                            "📜 Complete your saga properly: ...TO <name>;"
            );
        }

        // 7. Consume semicolon
        if (peek().type != TokenType.SEMICOLON) {
            throw new RuntimeException(
                    "💀 [HELHEIM'S GAZE] Expected ';' but found '" + peek().value + "'!\n" +
                            "🛡️ All commands must end with the mark of closure!"
            );
        }
        consume(TokenType.SEMICOLON);

        // 8. Reject any trailing junk tokens
        if (position < tokens.size()) {
            throw new RuntimeException(
                    "🌪️ [CHAOS STORM] Junk symbols after command!\n" +
                            "⚔️ Only one sacred incantation per line is permitted."
            );
        }
        // 9. Return the executable command
        return new AlterDatabaseNameCommand(oldName, newName);
    }

    /**
     * 🛡️ [VALHALLA’S DOOR] Parses the EXIT DATABASE command.
     * This method seals the gates to the current realm, returning the warrior
     * back to the realm of no database (NONE). It ensures the ritual ends
     * with the sacred mark `;` and punishes any unworthy symbols that follow.
     * Syntax demanded by the gods:
     * EXIT DATABASE;
     *
     * @return ExitDatabaseCommand blessed by Heimdall to guide the warrior out.
     * @throws RuntimeException if chaos runes remain after the command.
     */

    private ExitDatabaseCommand parseExitDatabase() {
        consume(TokenType.NONE);
        if (position >= tokens.size()) {
            System.out.println("expected a semi colon");
        }
        consume(TokenType.SEMICOLON);
        if (position < tokens.size()) {
            throw new RuntimeException(
                    "🌪️ [CHAOS STORM] Junk symbols after command!\n" +
                            "⚔️ Only one sacred incantation per line is permitted."
            );
        }
        return new ExitDatabaseCommand();
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
        List<ColumnDefinition> columns = parseColumnInsertStatements();

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
        List<Object> defaultValue = null;
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
                } else if (second.type == TokenType.TABLE) {
                    return parseDropTable();
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
                    throw new RuntimeException(
                            """
                                    ⚡ [BROKEN RUNE] ALTER command incomplete!
                                    🛡️ You must specify: ALTER DATABASE <name> RENAME  <new_name>
                                    🌌 Example: ALTER DATABASE Valhalla RENAME TO Asgard"""
                    );
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
                    throw new RuntimeException("except database and table alter doesn't work");
                }
            } else if (peek().type == TokenType.ADD) {
                advance();
                if (position >= tokens.size()) {
                    throw new RuntimeException(
                            """
                                    ⚡ [BROKEN RUNE] ALTER command incomplete!
                                    🛡️ You must specify: ADD COLUMN <column_name> RENAME TO <new_name>
                                    🌌 Example: ALTER DATABASE Valhalla RENAME TO Asgard"""
                    );
                }
                Token second = peek();
                if (second.type != TokenType.COLUMN) {
                    throw new RuntimeException("column keyword was expected but provided with " + second.value);
                }
                return parseAlterColumnsofTable();
            } else {
                throw new RuntimeException("⛓️ [CHAINS OF FATE] Expected CREATE or INSERT but found " + first.type + " ('" + first.value + "') — only these commands are known to the oracle!");
            }
        } catch (Exception e) {
            // Enhanced error reporting with parser state information
            System.err.println("💀 [PARSING CATASTROPHE] Error at position " + position + (position < tokens.size() ? " near token: '" + tokens.get(position).value + "'" : " (end of input)"));
            System.err.println("🔥 [FLAMES OF ERROR] " + e.getMessage());
            return null;
        }
    }
}