package com.cscie97.ledger;

/**
 * Represents an account in the blockchain.
 * An account must be unique.  A master account holds all the funds at the start of the blockchain.
 * The maximum account balance is Integer.MAX_VALUE
 */

public class Account {
    static final int DEFAULT_MASTER_BALANCE = 1000000;
    
    private String address;
    private int balance;  //This is an unsigned int thanks to Java 8.
    
    public Account(String address) {
        this.address = address;
        
        if (address == "master") {
            this.balance = 1000000; 
        } else {
            this.balance = 0;
        }
    }
    
    //Only used to make deep copies.  This is an aspect of Java that absolutely drove me up the wall.
    public Account(String address, int balance) {
        this.address = address;
        this.balance = balance;
    }
    
    //Helper methods
    
    public String getAddress() {
        return address;
    }
    
    public int getBalance() {
        return balance;
    }
    
    //Checking for overly negative or overly positive values is not done here, the logic is in the ledger.
    
    public void addToBalance(int add) {
        balance = balance + add;
    }
    
    public void subtractFromBalance(int subtract) {
        balance = balance - subtract;
    }
}