package com.yggra.parser;

import java.util.ArrayList;

public class Lexer {
//
    //Tokenizer
    //The lexer is the program or component responsible for reading the raw input
    //A scanner that reads a page
    //SELECT name FROM users WHERE age > 18;

    public boolean isAlpha(char ch){
        return Character.isAlphabetic(ch);
    }
    public boolean isDigit(char ch){
        return Character.isDigit(ch);
    }

    // Tokenizer

    public ArrayList<Token> tokenize(String input){
        ArrayList<Token> tokens= new ArrayList<>();
        int current=0;
        try {
            while (current < input.length()) {
                char ch = input.charAt(current);
                if (Character.isWhitespace(ch)) {
                    current++;
                    continue;
                }
                switch (ch){
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
                    case '\'':
                        current++; // Skip opening quote
                        StringBuilder stringLiteral=new StringBuilder();
                        while(current<input.length() && input.charAt(current)!='\''){
                            stringLiteral.append(input.charAt(current));
                            current++;}
                        // When we reach the closing quote
                            if(current<input.length() && input.charAt(current)=='\''){
                                current++;
                                tokens.add(new Token(TokenType.STRING_LITERAL, stringLiteral.toString()));
                            } else {
                                System.out.println("⚠️ [ERROR] String literal was never closed. By the gods, finish what you started!");
                                return tokens;
                            }
                        break;
                    default:
                        if(isDigit(ch)){
                            StringBuilder numToken=new StringBuilder();
                            while (current<input.length() && isDigit(input.charAt(current))){
                                numToken.append(input.charAt(current));
                                current++;
                            }
                            tokens.add(new Token(TokenType.NUMBER_LITERAL, numToken.toString()));
                        }
                        else if(isAlpha(ch)){
                            StringBuilder alphaToken=new StringBuilder();
                            while ((current<input.length()) && (isAlpha(input.charAt(current)) || isDigit(input.charAt(current)) || input.charAt(current)=='_')){
                                alphaToken.append(input.charAt(current));
                                current++;
                            }
                            String keyword=alphaToken.toString().toUpperCase();

                            switch (keyword){
                                case "CREATE":
                                    tokens.add(new Token(TokenType.CREATE, keyword));
                                    break;
                                case "INSERT":
                                    tokens.add(new Token(TokenType.INSERT, keyword));
                                    break;
                                case "INTO":
                                    tokens.add(new Token(TokenType.INTO, keyword));
                                    break;
                                case "VALUES":
                                    tokens.add(new Token(TokenType.VALUES, keyword));
                                    break;
                                case "TABLE":
                                    tokens.add(new Token(TokenType.TABLE, keyword));
                                    break;
                                case "INT":
                                    tokens.add(new Token(TokenType.INT, keyword));
                                    break;
                                case "VARCHAR":
                                    tokens.add(new Token(TokenType.VARCHAR, keyword));
                                    break;
                                default:
                                    tokens.add(new Token(TokenType.IDENTIFIER, keyword));
                            }
                        }
                        else{
                            System.out.println("❌ [CURSE] 'This character is not of this realm: " + ch + "'");
                            current++;
                        }
                }
            }
        }
        catch (Exception e){
            System.out.println("🔥 [FURY OF THE GODS] Something broke within the parser: " + e.getMessage());
        }
        return tokens;
    }
}
