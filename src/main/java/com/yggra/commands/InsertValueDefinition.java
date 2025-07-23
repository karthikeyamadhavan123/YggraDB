package com.yggra.commands;

import com.yggra.parser.TokenType;

public class InsertValueDefinition {
    public TokenType type;
    public String value;

    public InsertValueDefinition(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    @Override
    public String toString(){
        return type.toString() + value;
    }
}
