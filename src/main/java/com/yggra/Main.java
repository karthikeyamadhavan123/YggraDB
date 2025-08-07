package com.yggra;

import com.yggra.cli.YggraREPL;

/**
 * 🔥 [RUNIC INSCRIPTION] 🔥
 * The Genesis class — the divine spark that awakens YggraDB.
 *
 * This is the first breath of the system — the mortal entry point — where realms are loaded,
 * the Oracle (REPL) is summoned, and the voice of the user is finally heard across Yggdrasil.
 */
public class Main {

    /**
     * 🌩️ [THE AWAKENING] 🌩️
     * Invokes the YggraREPL — the Read-Eval-Print Loop — allowing mortals to commune with the database gods.
     *
     * @param args Mortal parameters (currently ignored by the gods)
     */
    public static void main(String[] args) {
        // 📦 Initializes the Oracle and prepares for mortal queries
        YggraREPL repl = new YggraREPL();
        repl.start(); // 🔮 Let the prophecy unfold
    }
}
