package com.yggra;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Entry point (optional)
        Scanner sc = new Scanner(System.in);
        while(true){
            try{
                    System.out.print("Enter the command :");
                    String command = sc.nextLine();
                    if(command.equals("quit")){
                        break;
                    }
                    else {
                        System.out.println("command is " +command);
                    }
            }
            catch (Exception e){
                System.out.print(e);
            }
        }

    }
}
