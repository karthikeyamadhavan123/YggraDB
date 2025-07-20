package com.yggra.parser;






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

    @Override
    public String toString() {
        return type + ": " + value;
    }

}
