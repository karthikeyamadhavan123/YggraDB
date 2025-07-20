package com.yggra.cli;

import com.yggra.parser.Token;

import java.util.ArrayList;
import java.util.Scanner;
import com.yggra.parser.Lexer;

public class YggraREPL {
//REPL (Read-Eval-Print Loop)
private final Scanner sc;
private final Lexer lexer;


public YggraREPL(){
    this.sc = new Scanner(System.in);
    this.lexer = new Lexer();
}
public void start(){
    System.out.println("⚡ YggraDB REPL started. Type 'quit' to exit.");
    while(true){
        try{
            System.out.print("YggraDB 🛡️ > ");
            String input  = sc.nextLine().trim();
            if(input.isEmpty()){
                System.out.println("Please Enter a valid command.");
                continue;
            }
            if(input.equalsIgnoreCase("quit")){
                System.out.println("⚔️ Exiting YggraDB REPL. Farewell, warrior.");
                break;
            }
            // Placeholder: Eventually pass to Lexer → Parser → SQLCommand
            else{
                ArrayList<Token> tokens = lexer.tokenize(input);
                for (Token token : tokens) {
                    System.out.println(token);
                }
            }
        }
        catch (Exception e){
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

}
}
