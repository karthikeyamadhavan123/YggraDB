package com.yggra.parser;

/**
 * 🪶 [RUNIC INSCRIPTION] 🪶
 * `TokenType` enumerates all recognized symbols, keywords, and data types
 * in YggraDB's sacred SQL dialect. Each rune holds meaning in the parser’s mind,
 * transforming mortal input into structured intent.
 */

public enum TokenType {
    // 📜 [COMMAND RUNES] – Keywords that begin divine operations
    CREATE,
    INSERT,
    INTO,
    VALUES,
    TABLE,
    DATABASE,
    USE,
    DROP,
    SHOW,
    DATABASES,
    CURRENT,
    TABLES,
    ALTER,
    RENAME,
    NONE,
    ADD,
    COLUMN,
    TO,
    DEFAULT,


    // 📐 [DATA TYPE RUNES] – Define the essence of columns
    INT,
    VARCHAR,

    // 🏷️ [IDENTIFIER RUNES] – Names of tables, columns, and other mortal constructs
    IDENTIFIER,

    // 🔢 [LITERAL RUNES] – Mortal values like numbers or strings
    NUMBER_LITERAL,
    STRING_LITERAL,

    // ⚙️ [SYMBOLIC RUNES] – Structural symbols used in SQL scrolls
    LEFT_PAREN,
    RIGHT_PAREN,
    COMMA,
    SEMICOLON
}
