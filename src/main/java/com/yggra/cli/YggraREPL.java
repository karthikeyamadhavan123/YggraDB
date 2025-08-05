package com.yggra.cli;

import com.yggra.commands.SQLCommand;
import com.yggra.executor.SQLExecutor;
import com.yggra.parser.Parser;
import com.yggra.parser.Token;

import java.util.ArrayList;
import java.util.Scanner;
import com.yggra.parser.Lexer;

public class YggraREPL {
//REPL (Read-Eval-Print Loop)
private final Scanner sc;
private final Lexer lexer;
private final SQLExecutor executor;

public YggraREPL(){
    this.sc = new Scanner(System.in);
    this.lexer = new Lexer();
    this.executor=new SQLExecutor();
}
public void start(){
    System.out.println("⚡ [YGGRA REPL AWAKENS] Speak your incantations, warrior. Type 'quit' to abandon the saga.");
    while(true){
        try{
            System.out.print("YggraDB 🛡️ > ");
            String input  = sc.nextLine().trim();
            if(input.isEmpty()){
                System.out.println("🌑 [VOID WHISPERS] No words spoken — the saga demands a command!");
                continue;
            }
            if(input.equalsIgnoreCase("quit")){
                System.out.println("⚔️ Exiting YggraDB REPL. Farewell, warrior.");
                break;
            }
            // Tokenize → Parse → Execute
            else{
                ArrayList<Token> tokens = lexer.tokenize(input);
                Parser parser = new Parser(tokens);
                SQLCommand command = parser.parse();
                if (command != null) {
                    executor.execute(command);
                }
            }
        }
        catch (Exception e){
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

}
}
