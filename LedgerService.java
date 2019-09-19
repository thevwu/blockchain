package com.cscie97.ledger;
import java.util.*;

//This is just an interface I found useful when implementing.

public interface LedgerService {    
    String createAccount(String address);
    String processTransaction(Transaction transaction);
    Integer getAccountBalance(String address);
    HashMap<String, Integer> getAccountBalances();
    Block getBlock(Integer blockNumber);
    Transaction getTransaction(String transactionID);
    boolean validate();
}