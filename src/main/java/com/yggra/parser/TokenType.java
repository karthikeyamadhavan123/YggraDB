package com.yggra.parser;

public enum TokenType {
    // Keywords
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


    // Data Types
    INT,
    VARCHAR,

    // Identifiers (table names, column names)
    IDENTIFIER,

    // Literals Like 50,'John'
    NUMBER_LITERAL,
    STRING_LITERAL,

    // Symbols
    LEFT_PAREN,
    RIGHT_PAREN,
    COMMA,
    SEMICOLON
}
