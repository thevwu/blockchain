package com.cscie97.ledger;

/*
 * Amounts and fees are equating to unsigned int thanks to Java 8. 
 * The minimum or maximum amount or fee is not the responsibility of this model, it is handled in the ledger.
 */

public class Transaction {
    private String transactionID;
    private int amount;  //can't be negative
    private int fee;  //can't be negative
    private String payload;  
    
    private String payer;
    private String receiver;
    
    public Transaction(String transactionID, int amount, int fee, String payload, String payer, String receiver) {
        this.transactionID = transactionID;
        this.amount = amount;
        this.fee = fee;
        this.payload = payload;
        this.payer = payer;
        this.receiver = receiver;
    }
    
    public String getTransactionID() {
        return transactionID;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public int getFee() {
        return fee;
    }
    
    public String getPayload() {
        return payload;
    }
    
    public String getPayer() {
        return payer;
    }
    
    public String getReceiver() {
        return receiver;
    }
}