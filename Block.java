package com.cscie97.ledger;
import java.util.*;
import java.security.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Represents a block, which contains a list of transactions, the account map, the hashes of the blocks.
 * Blocks are collections of transactions.  A block is committed when the transaction count reaches 10.
 * Until a block is committed, new accounts are not recognized and transactions do not affect account balances.
 * Hashes are unique and computed based on all values, excluding their own hash value.
 */

public class Block {
    private int blockNumber; //never allowed to change
    private String previousHash;  //unique
    private String hash;  //unique
    private String seed; //added because I consider hashing a block to be part of a block's job
    private Block previousBlock;
    
    private ArrayList<Transaction> transactionList = new ArrayList<Transaction>(); 
    private HashMap<String, Account> accountMap = new HashMap<String, Account>();
    
    public Block(int blockNumber, String previousHash, ArrayList<Transaction> transactionList, 
                 HashMap<String, Account> accountMap, Block previousBlock, String seed) {
        this.blockNumber = blockNumber;
        this.previousHash = previousHash;
        this.hash = hash;
        this.transactionList = transactionList;
        this.accountMap = accountMap;
        this.previousBlock = previousBlock;
        this.seed = seed;
    }
    
    //Helper methods
    
    public int getBlockNumber() {
        return blockNumber;
    }
    
    public String getPreviousHash() {
        return previousHash;
    }
    
    public Block getPreviousBlock() {
        return previousBlock;
    }
    
    public int getTransactionCount() {
        return transactionList.size();
    }
    
    public Transaction[] getTransactionList() {
        Transaction[] returnTransactionList = new Transaction[transactionList.size()]; 
         
        return transactionList.toArray(returnTransactionList);
    }
    
    public Transaction getTransaction(String transactionID) {
        for (Transaction transaction : transactionList) {
            if (transaction.getTransactionID().equals(transactionID)) {
            return transaction; 
            }
        }
        
        return null;
    }
    
    public void addTransaction(Transaction transaction) {
        transactionList.add(transaction);
    }
    
    public Account getAccount(String address) {
        return accountMap.get(address);
    }
    
    public void addAccount(Account account) {
        accountMap.put(account.getAddress(), account);
    }
    
    //I did not want to pass the hashMap which would allow it to be changed, so I am giving a list of accounts instead
    
    public ArrayList<Account> getAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();
            
        accountMap.forEach((accountID, account) -> accounts.add(account));

        return accounts;
    }
    
    //Making the next block logic is in here.  The ledger is responsible for telling the block when to make a new one, but the block
    //is responsible for making the next one.
    
    public boolean isBlockFull() {
        if (transactionList.size() >= 10) {
            return true;
        }
        
        return false;
    }
    
    public Block makeNextBlock() {
        setHash();
        
        HashMap<String, Account> copyAccountMap = new HashMap<String, Account>();
        
        accountMap.forEach((accountID, account) -> {
            Account newAccount = new Account(account.getAddress(), account.getBalance());
            copyAccountMap.put(account.getAddress(), newAccount);
        });
        
        Block newBlock = new Block(blockNumber + 1, hash, new ArrayList<Transaction>(), 
                 copyAccountMap, this, this.seed);
        
        return newBlock;
    }
    
    //Hashing functions.
    
    public String getHash() {
        return hash;
    }
    
    private String getComputedHash() {
        try { 
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        StringBuilder encoding = new StringBuilder();
            
        encoding.append(blockNumber);
        
        for(Transaction transaction : transactionList) {
           encoding.append(transaction.getTransactionID());
           encoding.append(transaction.getAmount());
           encoding.append(transaction.getFee());
           encoding.append(transaction.getPayload());
           encoding.append(transaction.getPayer());
           encoding.append(transaction.getReceiver());
        }
        
        accountMap.forEach((accountID, account) -> encoding.append(account.getAddress() + account.getBalance()));
        
        encoding.append(seed);
        
        String baseEncoding = encoding.toString();
        
        byte[] hash = digest.digest(baseEncoding.getBytes("UTF-8"));
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
        } catch(Exception e) {
            System.out.println("Some error with computing hash!");
        }
        
        return "Broken!";
    }
    
    //The block should set its own hash once it decides to commit itself.
    
    private void setHash() {
        hash = getComputedHash();
    }
}