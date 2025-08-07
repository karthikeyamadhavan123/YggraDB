package com.yggra.commands;

import com.yggra.parser.TokenType;

/**
 * 💎 [RUNIC INSCRIPTION] 💎
 * Holds a single fragment of data — a value paired with its type — destined for insertion into the sacred tables of Yggra.
 * A `ValueDefinition` represents the raw essence of information: whether INT or VARCHAR,
 * it carries truth through the realms of commands like INSERT, where new records are born.
 */

public class ValueDefinition {

    // 🔮 The type rune — INT, VARCHAR, etc. — revealing the nature of this value
    public TokenType type;

    // 📦 The actual value, as spoken by the mortal issuing the SQL command
    public String value;


    /**
     * ✨ [ESSENCE BINDING] ✨
     * Binds a data value to its corresponding type, ready to be inserted into the world-tree of knowledge.
     * @param type The type rune (INT, VARCHAR, etc.)
     * @param value The mortal-provided value to be inserted
     */

    public ValueDefinition(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 🪞 [MIRROR OF ESSENCE] 🪞
     * Returns the stringifier form of this value — useful for debugging or reflection,
     * showing both type and the literal value it holds.
     * @return String representation of the value and its rune
     */

    @Override
    public String toString(){
        return type.toString() + value;
    }
}
