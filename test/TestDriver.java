package com.cscie97.ledger.test;
import com.cscie97.ledger.*;
import java.io.BufferedReader;
import java.io.FileReader;

/* 
 * TestDriver specifically indicates the file path equaling to the package up at the top.
 * It then reads in a multi-line text script.
 */

public class TestDriver {
    public static void main(String[] args) throws Exception {
        String fileName = args[0];

        try(BufferedReader br = new BufferedReader(new FileReader("com/cscie97/ledger/test/" + args[0]))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            
            String everything = sb.toString();
            
            CommandProcessor.processCommandFile(everything);
        }
    }
}