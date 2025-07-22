package com.yggra.commands;

import com.yggra.parser.TokenType;

public class ColumnDefinition {
    public String value;
    public TokenType type;
    public int length;

    public ColumnDefinition(String value, TokenType type, int length) {
        this.value = value;
        this.type = type;
        this.length = length;
    }
    @Override
    public String toString() {
        if (type == TokenType.INT) {
            return value + " INT";
        } else if (type == TokenType.VARCHAR) {
            return value + " VARCHAR(" + length + ")";
        }
        return value + " UNKNOWN_TYPE";
    }
}
