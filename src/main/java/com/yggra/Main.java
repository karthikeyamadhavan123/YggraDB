package com.yggra;

import com.yggra.cli.YggraREPL;


public class Main {
    public static void main(String[] args) {
        //Entry point (optional)
        //packages and modules set up
        YggraREPL repl = new YggraREPL();
        repl.start();
    }
}
