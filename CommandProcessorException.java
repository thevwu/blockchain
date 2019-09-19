package com.cscie97.ledger;

/**
 * CommandProcessorException and LedgerException are basically the same.
 * I originally fused this into its own Exception class, but I think for future
 * planning this is alright incase the code needs to diverge.
 */

public class CommandProcessorException {
    private String action;
    private String reason;
    
    public CommandProcessorException(String action, String reason) {
        this.action = action;
        this.reason = reason;
    }
    
    public String getAction() {
        return action;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void printException() {
        System.out.println("CommandProcessorException!  Action: " + action + " Reason: " + reason + "\n");   
    }
}