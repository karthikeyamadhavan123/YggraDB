package com.yggra.parser;


import java.util.ArrayList;



public class Token {
    //SELECT name FROM users WHERE age > 18;
    //Token definition
    private final TokenType type;
    private final String value;
    //Constructor initiated whenever a new character comes


    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}
