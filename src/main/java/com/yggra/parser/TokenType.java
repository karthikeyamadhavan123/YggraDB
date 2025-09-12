package com.yggra.parser;

/**
 * 🪶 [RUNIC INSCRIPTION] 🪶
 * `TokenType` enumerates all recognized symbols, keywords, data types,
 * and operators in YggraDB's SQL dialect.
 * Each rune guides the parser from mortal text to divine intent.
 */
public enum TokenType {

    // 📜 [COMMAND RUNES] – Keywords for DDL/DML
    CREATE, INSERT, INTO, VALUES, TABLE, DATABASE, USE, DROP, SHOW,
    DATABASES, CURRENT, TABLES, ALTER, RENAME, ADD, COLUMN, TO,
    DEFAULT, TRUNCATE, REMOVE, FROM, IN, MODIFY, SET, FOR, NULL,
    SELECT, WHERE, GROUP, BY, HAVING, ORDER, LIMIT, DISTINCT, AS,NONE,

    // 📐 [DATA TYPE RUNES] – Column essences
    INT, VARCHAR, BOOLEAN, FLOAT, DOUBLE, CHAR, DATE, TIMESTAMP,

    // 🏷️ [IDENTIFIER RUNES] – Names of tables, columns, etc.
    IDENTIFIER,

    // 🔢 [LITERAL RUNES] – Mortal values
    NUMBER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL, NULL_LITERAL,

    // ⚙️ [SYMBOLIC RUNES] – Structure symbols
    LEFT_PAREN, RIGHT_PAREN, COMMA, SEMICOLON, DOT, STAR,

    // ➕ [ARITHMETIC RUNES]
    PLUS, MINUS, SLASH, ASTERISK, PERCENT,

    // 🔍 [COMPARISON RUNES]
    EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUAL,
    GREATER_THAN, GREATER_THAN_EQUAL,

    // 🧭 [LOGICAL RUNES]
    AND, OR, NOT, IS, LIKE, BETWEEN,

    // 🌀 [MISC RUNES]
    EOF // end of input
}
