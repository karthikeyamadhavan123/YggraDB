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

    private List<InsertColumnDefinition> parseColumnInsertStatements() {
        // Parse the first column name or value (at least one is required)
        List<InsertColumnDefinition> columns = new ArrayList<>();
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
     * Used for both INSERT column specifications and INSERT value specifications
     *
     * @throws RuntimeException if no valid token is found at current position
     */
    private InsertColumnDefinition parseColumnInsertStatement() {
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
        return new InsertColumnDefinition(columnName);
    }

    private InsertValueDefinition parseValuesInsertStatement() {
        // Ensure we have a token to examine
        if (position >= tokens.size()) {
            throw new RuntimeException("📦 [EMPTY OFFERING] Expected value for insertion but found void — the gods demand tribute!");
        }
        // Accept various token types for INSERT operations:
        if (peek().type == TokenType.NUMBER_LITERAL) {
            // Column names are just consumed here
            String value = peek().value;
            consume(TokenType.NUMBER_LITERAL);
            return new InsertValueDefinition(TokenType.NUMBER_LITERAL, value);
        } else if (peek().type == TokenType.STRING_LITERAL) {
            String value = peek().value;
            consume(TokenType.STRING_LITERAL);
            return new InsertValueDefinition(TokenType.STRING_LITERAL, value);
        } else {
            // For extensibility - other data types may be supported in future
            // Currently just logs the occurrence without throwing an error
            throw new RuntimeException("⚔️ [WRONG TRIBUTE] The value offered does not match the column's essence — the gods reject this false gift!");
        }
    }

    private List<InsertValueDefinition> parseValuesInsertStatements() {
        // Parse the first column name or value (at least one is required)
        List<InsertValueDefinition> columns = new ArrayList<>();
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
            throw new RuntimeException(
                    "⚡ [ZEUS' WRATH] Additional tokens linger after the statement — finish what you began!"
            );
        }
        return new CreateTableCommand(columns, tableName);
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
        List<InsertColumnDefinition> columns = parseColumnInsertStatements();

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
        List<InsertValueDefinition> values = parseValuesInsertStatements();
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
            throw new RuntimeException(
                    "🌪️ [LINGERING SPIRITS] Additional tokens haunt the completed statement — banish these phantoms to complete the ritual!"
            );
        }

        if(columns.size()!=values.size()){
            throw new RuntimeException("⚔️ The AllFather demands equal measures! Columns (" + columns.size() + ") and values (" + values.size() + ") must stand in perfect balance!");
        }
        return new InsertCommand(tableName, columns, values);
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
            throw new RuntimeException(
                    "🌌 [COSMOS' END] Expected " + expectedType +
                            " but reached the void beyond all tokens — the universe of syntax has ended!"
            );
        }

        Token token = peek();
        if (token.type == expectedType) {
            advance();
        } else {
            throw new RuntimeException(
                    "⛓️ [CHAINS OF FATE] Expected " + expectedType +
                            " but found " + token.type + " ('" + token.value + "')"
            );
        }
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
                    throw new RuntimeException("🔨 [HEPHAESTUS' HAMMER SILENT] 'CREATE' declared but what shall be forged? TABLE expected but found void!");
                }

                Token second = peek();
                if (second.type != TokenType.TABLE) {
                    throw new RuntimeException("🏛️ [ARCHITECT'S CONFUSION] 'CREATE' found but '" + second.value + "' follows — only TABLE creation is currently blessed by the gods!");
                }

                // PARSE CREATE TABLE COMMAND
                return parseCreateTable();
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
                // PARSE INSERT COMMAND;
                return parseInsertStatement();
            } else {
                throw new RuntimeException(
                        "⛓️ [CHAINS OF FATE] Expected CREATE or INSERT but found " +
                                first.type + " ('" + first.value + "') — only these commands are known to the oracle!"
                );
            }
        } catch (Exception e) {
            // Enhanced error reporting with parser state information
            System.err.println("💀 [PARSING CATASTROPHE] Error at position " + position +
                    (position < tokens.size() ? " near token: '" + tokens.get(position).value + "'" : " (end of input)"));
            System.err.println("🔥 [FLAMES OF ERROR] " + e.getMessage());
            return null;
        }
    }
}