package com.yggra.parser;

import java.util.ArrayList;

/**
 * The Lexer class is responsible for converting raw SQL input strings
 * into a stream of tokens that the parser can understand.
 * * This acts as the "Scanner" of our ancient scrolls.
 */
public class Lexer {

    /** Checks if the character is an alphabetic letter (A-Z, a-z) */
    public boolean isAlpha(char ch) {
        return Character.isAlphabetic(ch);
    }

    /** Checks if the character is a digit (0-9) */
    public boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    /**
     * Tokenizes the input SQL string into a list of Token objects.
     * @param input Raw SQL query as string.
     * @return List of Tokens.
     */
    public ArrayList<Token> tokenize(String input) {
        ArrayList<Token> tokens = new ArrayList<>();
        int current = 0;

        try {
            while (current < input.length()) {
                char ch = input.charAt(current);

                // Skip whitespace like a stealthy Spartan
                if (Character.isWhitespace(ch)) {
                    current++;
                    continue;
                }

                // Delegate character processing to a helper method
                current = processCharacter(input, current, tokens, ch);
            }
        } catch (Exception e) {
            throw new RuntimeException("🔥 [FURY OF THE GODS] Something broke within the parser: " + e.getMessage());
        }

        return tokens;
    }

    /**
     * Processes a single character from the input string and adds the corresponding token(s) to the list.
     * @param input The raw SQL query string.
     * @param current The current index in the input string.
     * @param tokens The list of tokens being built.
     * @param ch The character to process.
     * @return The updated current index after processing the character.
     * @throws RuntimeException if an unclosed string literal or forbidden symbol is encountered.
     */
    private int processCharacter(String input, int current, ArrayList<Token> tokens, char ch) {
        switch (ch) {
            // Symbols and punctuation
            case '(':
                tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                current++;
                break;
            case ')':
                tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                current++;
                break;
            case ',':
                tokens.add(new Token(TokenType.COMMA, ","));
                current++;
                break;
            case ';':
                tokens.add(new Token(TokenType.SEMICOLON, ";"));
                current++;
                break;

            // String literals enclosed in single quotes
            case '\'':
                current++; // Skip opening quote
                StringBuilder stringLiteral = new StringBuilder();
                while (current < input.length()) {
                    if(input.charAt(current)=='\''){
                        if(input.charAt(current+1)=='\''){
                            stringLiteral.append(input.charAt(current+1));
                            current+=2;
                        }
                        else{
                            break; //End of string
                        }
                    }
                    else{
                        stringLiteral.append(input.charAt(current));
                        current++;
                    }

                }

                // Check for closing quote
                if (current < input.length() && input.charAt(current) == '\'') {
                    current++; // Skip closing quote
                    tokens.add(new Token(TokenType.STRING_LITERAL, stringLiteral.toString()));
                } else {
                    throw new RuntimeException("⚠️ [ERROR] String literal was never closed. By the gods, finish what you started!");
                }
                break;

            // Default case for identifiers, numbers, keywords, etc.
            default:
                // Number literal (e.g., 123)
                if (isDigit(ch)) {
                    StringBuilder numToken = new StringBuilder();
                    while (current < input.length() && isDigit(input.charAt(current))) {
                        numToken.append(input.charAt(current));
                        current++;
                    }
                    tokens.add(new Token(TokenType.NUMBER_LITERAL, numToken.toString()));
                }

                // Identifier or SQL keyword (e.g., CREATE, users, name_1)
                else if (isAlpha(ch)) {
                    StringBuilder alphaToken = new StringBuilder();
                    while (current < input.length() &&
                            (isAlpha(input.charAt(current)) ||
                                    isDigit(input.charAt(current)) ||
                                    input.charAt(current) == '_')) {
                        alphaToken.append(input.charAt(current));
                        current++;
                    }

                    String rawKeyword = alphaToken.toString();
                    String keyword = rawKeyword.toUpperCase(); // SQL is case-insensitive for keywords

                    // Match against known keywords
                    switch (keyword) {
                        case "CREATE":
                            tokens.add(new Token(TokenType.CREATE, rawKeyword));
                            break;
                        case "INSERT":
                            tokens.add(new Token(TokenType.INSERT, rawKeyword));
                            break;
                        case "INTO":
                            tokens.add(new Token(TokenType.INTO, rawKeyword));
                            break;
                        case "VALUES":
                            tokens.add(new Token(TokenType.VALUES, rawKeyword));
                            break;
                        case "TABLE":
                            tokens.add(new Token(TokenType.TABLE, rawKeyword));
                            break;
                        case "INT":
                            tokens.add(new Token(TokenType.INT, rawKeyword));
                            break;
                        case "VARCHAR":
                            tokens.add(new Token(TokenType.VARCHAR, rawKeyword));
                            break;
                        case "DATABASE":
                            tokens.add(new Token(TokenType.DATABASE,rawKeyword));
                            break;
                        case "USE":
                            tokens.add(new Token(TokenType.USE,rawKeyword));
                            break;
                        case "DROP":
                            tokens.add(new Token(TokenType.DROP,rawKeyword));
                            break;
                        case "SHOW":
                            tokens.add(new Token(TokenType.SHOW,rawKeyword));
                            break;
                        case "DATABASES":
                            tokens.add(new Token(TokenType.DATABASES,rawKeyword));
                            break;
                        case "CURRENT":
                            tokens.add(new Token(TokenType.CURRENT,rawKeyword));
                            break;
                        case "TABLES":
                            tokens.add(new Token(TokenType.TABLES,rawKeyword));
                            break;
                        default:
                            tokens.add(new Token(TokenType.IDENTIFIER, rawKeyword));
                    }
                }
                // Unrecognized symbol — cast it into the underworld
                else {
                    throw new RuntimeException("⚔️ [WRATH] Forbidden symbol '" + ch + "' was struck down — it does not belong to the realm of SQL present in realm world!");
                }
                break;
        }
        return current;
    }
}