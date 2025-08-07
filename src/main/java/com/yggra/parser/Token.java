package com.yggra.parser;

/**
 * ✨ [RUNIC INSCRIPTION] ✨
 * Each `Token` is a fragment of the sacred text — a unit of syntax, born from the lexer’s forge.
 * Tokens represent keywords, identifiers, symbols, or values, each carrying meaning from the mortal query
 * into the parser’s scroll for execution. They are the syllables of Yggra’s sacred language.
 */
public class Token {

    // 🔤 The type of this token (e.g., IDENTIFIER, KEYWORD, SYMBOL, STRING, INT, etc.)
    public final TokenType type;

    // 🧾 The literal value of this token, as whispered by the mortal (e.g., "CREATE", "users", "42")

    public final String value;

    /**
     * 🛠️ [RUNE FORGING] 🛠️
     * Constructs a token by binding a type rune to its string value — used during lexical analysis.
     * @param type The token's type (e.g., VARCHAR, INT, SYMBOL, etc.)
     * @param value The string value associated with the token
     */


    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 🔍 [RUNE REVEAL] 🔍
     * Returns a readable representation of the token — useful for debugging and parsing rituals.
     * @return String combining token type and its mortal value
     */

    @Override
    public String toString() {
        return type + ": " + value;
    }

}
