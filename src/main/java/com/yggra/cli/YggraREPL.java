package com.yggra.cli;

import com.yggra.commands.SQLCommand;
import com.yggra.executor.SQLExecutor;
import com.yggra.models.DatabaseManager;
import com.yggra.parser.Parser;
import com.yggra.parser.Token;

import java.util.ArrayList;
import java.util.Scanner;

import com.yggra.parser.Lexer;

/**
 * 🧙 [THE ORACLE] 🧙
 * YggraREPL – Read-Eval-Print Loop.
 * The sacred portal through which the chosen one communes with the gods of YggraDB.
 * It listens, interprets, and channels divine commands into raw execution.
 */


public class YggraREPL {
    // 🔮 Input scribe of the mortal realm
    private final Scanner sc;
    // 📜 Lexer – the runic transcriber
    private final Lexer lexer;
    // ⚔️ Executor – the sword that enacts divine will
    private final SQLExecutor executor;

    public YggraREPL() {
        /*
         * 🪄 Constructor – Initializes the Oracle
         */

        this.sc = new Scanner(System.in);
        this.lexer = new Lexer();
        this.executor = new SQLExecutor();
    }


    /**
     * 🌌 Start the REPL loop – awaits mortal incantations
     */

    public void start() {
        System.out.println("⚡ [YGGRA REPL AWAKENS] Speak your incantations, warrior. Type 'quit' to abandon the saga.");
        while (true) {
            try {
                // 🛡️ Dynamic prompt based on current database
                String prompt = "YggraDB 🛡️";
                if (DatabaseManager.getInstance().hasCurrentDatabase()) {
                    prompt = DatabaseManager.getInstance().getCurrentDatabase() + " 🛡️";
                }
                System.out.print(prompt + " >" + " ");
                String input = sc.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("🌑 [VOID WHISPERS] No words spoken — the saga demands a command!");
                    continue;
                }
                if (input.equalsIgnoreCase("quit")) {
                    System.out.println("⚔️ Exiting YggraDB REPL. Farewell, warrior.");
                    break;
                }

                // 🔍 LEX → 📜 PARSE → ⚡ EXECUTE

                else {
                    ArrayList<Token> tokens = lexer.tokenize(input);
                    Parser parser = new Parser(tokens);
                    SQLCommand command = parser.parse();
                    if (command != null) {
                        long startTime = System.nanoTime();
                        executor.execute(command);
                        long endTime = System.nanoTime();
                        System.out.println("⏱️ Execution time: " + (endTime-startTime)*0.000001 + " ms");
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            }
        }

    }
}
