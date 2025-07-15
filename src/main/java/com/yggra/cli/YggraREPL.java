package com.yggra.cli;

import java.util.Scanner;

public class YggraREPL {
//REPL (Read-Eval-Print Loop)
private final Scanner sc;

public YggraREPL(){
    this.sc = new Scanner(System.in);
}
public void start(){
    System.out.println("⚡ YggraDB REPL started. Type 'quit' to exit.");
    while(true){
        try{
            System.out.print("yggra> ");
            String input  = sc.nextLine().trim();
            if(input.equalsIgnoreCase("quit")){
                System.out.println("⚔️ Exiting YggraDB REPL. Farewell, warrior.");
                break;
            }

            // Placeholder: Eventually pass to Lexer → Parser → SQLCommand
            System.out.println("Received: " + input);
        }
        catch (Exception e){
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

}
}
