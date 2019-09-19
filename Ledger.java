package com.cscie97.ledger;
import java.util.*;

/**
 * The ledger is the powerhouse of the blockchain.  
 * This does all the heavy lifting.  It's actually quite sparse in what it has direct reference to.
 * Associations are the blockmap, the master account, and I added in the currentBlock instead of computing it over and over.
 */

public class Ledger implements LedgerService {
    public String name;
    public String description;
    private String seed;
    
    private HashMap<Integer, Block> blockMap = new HashMap<Integer, Block>();
    private Integer currentBlockNumber;  //An addition that I found was necessary instead of calling get on the blockmap.
    
    private HashSet<String> transactionIDs = new HashSet<String>(); //Transaction IDs are unique but they are in array lists, so I keep them here for easy access.
    
    public Ledger(String name, String description, String seed) { 
        this.name = name;
        this.description = description;
        this.seed = seed;
        
        Block genesisBlock = new Block(0, null, new ArrayList<Transaction>(), 
                 new HashMap<String, Account>(), null, seed); //Genesis blocks are made upon ledger creation.
        
        Account masterAccount = new Account("master");
        genesisBlock.addAccount(masterAccount);  //Master account needs to be added to the genesis block.
        
        currentBlockNumber = 0;  //And then we do everything on the genesis block until the creation of the next one.
        blockMap.put(currentBlockNumber, genesisBlock);  

    }
    
    //Getter & Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Some methods return null so that the command processor can handle their logic and an exception.  
     * These methods do not handle any syntax related issues.
     */
    
    public String createAccount(String address) {
        if (address == null || address == "") {
            LedgerException ledgerException = new LedgerException("create-account", "Invalid Name");
            ledgerException.printException();
            
            return null;
        }
        
        ArrayList<Account> accounts = blockMap.get(currentBlockNumber).getAccounts();
        
        for (Account account : accounts) {
            if (account.getAddress() == address) {
                LedgerException ledgerException = new LedgerException("create-account", "Duplicate Name");
                ledgerException.printException();
                
                return null;
            }
        }
        
        Account newAccount = new Account(address);
        blockMap.get(currentBlockNumber).addAccount(newAccount);
        
        return address;
    }
    
    /**
     * Fee limitations and the "unsigned" requirement are in this method.
     */
    
    public String processTransaction(Transaction transaction) {
        if (transactionIDs.contains(transaction.getTransactionID())) {
            LedgerException ledgerException = new LedgerException("process-transaction", "Can't have duplicate transaction IDs!");
            ledgerException.printException();
            
            return null;
        }
            
        int amount = transaction.getAmount();
        int fee = transaction.getFee();
        
        if (amount < 0 || amount > Integer.MAX_VALUE) {
            LedgerException ledgerException = new LedgerException("process-transaction", "Amount can't be negative or too high!");
            ledgerException.printException();
            
            return null;
        } else if (fee < 10 || fee > Integer.MAX_VALUE) {
            LedgerException ledgerException = new LedgerException("process-transaction", "Fee can't be less than 10 or too high!");
            ledgerException.printException();
            
            return null;
        }
        
        Block currentBlock = blockMap.get(currentBlockNumber);
        
        Account payer = currentBlock.getAccount(transaction.getPayer());
        Account receiver = currentBlock.getAccount(transaction.getReceiver());
        Account masterAccount = currentBlock.getAccount("master");
        
        if (payer == null || receiver == null) {
            LedgerException ledgerException = new LedgerException("process-transaction", "Payer or receiver not found!");
            ledgerException.printException();
            
            return null;
        }
        
        if (payer.getBalance() < amount + fee) {
            LedgerException ledgerException = new LedgerException("process-transaction", "Payer doesn't have enough funds!");
            ledgerException.printException();
            
            return null;
        }
        
        if (masterAccount.getBalance() + fee > Integer.MAX_VALUE || receiver.getBalance() + amount > Integer.MAX_VALUE) {
            LedgerException ledgerException = new LedgerException("process-transaction", "Can't exceed 214783647 for balance!");
            ledgerException.printException();
            
            return null;
        }
        
        payer.subtractFromBalance(amount + fee);
        receiver.addToBalance(amount);
        masterAccount.addToBalance(fee);
        currentBlock.addTransaction(transaction);
        transactionIDs.add(transaction.getTransactionID());
            
        nextBlockCheck();
        
        return transaction.getTransactionID();
    }
    
     /*
     * The genesis block doesn't have any accounts or account balances committed, so I made a conscious choice not to include
     * it.  Even though the master account is here.
     */
    
    public Integer getAccountBalance(String address) {
        Block currentBlock = blockMap.get(currentBlockNumber);
        Block previousBlock = currentBlock.getPreviousBlock();
        
        if (previousBlock == null) {
            LedgerException ledgerException = new LedgerException("get-account-balance", "This is the genesis block!");
            ledgerException.printException();
            
            return null;
        }
        
        Account account = previousBlock.getAccount(address);
        
        if (account == null) {
            LedgerException ledgerException = new LedgerException("get-account-balance", "Account doesn't exist!");
            ledgerException.printException();
            
            return null;
        }
        
        return account.getBalance();
    }
    
    /*
     * The genesis block doesn't have any accounts or account balances committed, so I made a conscious choice not to include
     * it.  Even though the master account is here.
     */
    
    public HashMap<String, Integer> getAccountBalances() {
        Block currentBlock = blockMap.get(currentBlockNumber);
        Block previousBlock = currentBlock.getPreviousBlock();
        
        if (previousBlock == null) {
            LedgerException ledgerException = new LedgerException("get-account-balances", "This is the genesis block!");
            ledgerException.printException();
            
            return null;
        }
        
        HashMap<String, Integer> accountBalanceMap = new HashMap<String, Integer>();
        ArrayList<Account> accounts = previousBlock.getAccounts();
        
        accounts.forEach((account) -> accountBalanceMap.put(account.getAddress(), account.getBalance()));
        
        return accountBalanceMap;
    }
    
    public Block getBlock(Integer blockNumber) {
        return blockMap.get(blockNumber);
    }
    
    /*
     * Transactions are searched from the current block and then backwards.
     */
    
    public Transaction getTransaction(String transactionID) {
        int currentBlockNumberCheck = currentBlockNumber;
        Block searchBlock = blockMap.get(currentBlockNumberCheck);
        
        while (searchBlock.getBlockNumber() >= 0) {
            Transaction transaction = searchBlock.getTransaction(transactionID);
            
            if (transaction == null && searchBlock.getBlockNumber() != 0) {
                searchBlock = blockMap.get(currentBlockNumberCheck--);
            } else {
                return transaction;
            }
        }
        
        return null;
    }    
    
     /*
     * Validation computes the hash of a block, and then compares it with the "previousHash" value of its successor.
     * It also adds up all account balances and compares them with master account (if master account had no fees), and then
     * It checks to make sure all committed blocks have a transaction count of 10.
     * BlockToCheck equates to all committed blocks, and at the end of the loop the nextBlock variable equates to the current
     * processing block.
     */
    
    public boolean validate() {
        Block blockToCheck = getBlock(0);
        Block nextBlock = getBlock(1);
        
        int total = 0;
        int currentNextBlockIndex = 1;
        
        while (nextBlock != null) {
            total = 0;
            
            if (blockToCheck.getTransactionCount() != 10) {
                LedgerException ledgerException = new LedgerException("validate", "Block #" + blockToCheck.getBlockNumber() + " has incorrect transaction count!");
                ledgerException.printException();
            
                return false;
            }
            
            if (nextBlock.getPreviousHash() != blockToCheck.getHash()) {
                LedgerException ledgerException = new LedgerException("validate", "Block #" + blockToCheck.getBlockNumber() + " has been falsified!  " +
                                                                      "Hash: " + blockToCheck.getHash() + " Actual: " + nextBlock.getPreviousHash());

                ledgerException.printException();
            
                return false;
            }
            
            for (Account account : blockToCheck.getAccounts()) {
                total = total + account.getBalance();
            }
            
            if (total != Account.DEFAULT_MASTER_BALANCE) {
                LedgerException ledgerException = new LedgerException("validate", "Block #" + blockToCheck.getBlockNumber() + " balances are incorrect!  Expected:" + Account.DEFAULT_MASTER_BALANCE
                                                                     + "but got :" + total);
                ledgerException.printException();
            
                return false;
            }
            
            blockToCheck = getBlock(currentNextBlockIndex);
            currentNextBlockIndex++;
            nextBlock = getBlock(currentNextBlockIndex);
        }
        
        return true;
    }
    
    /*
     * This is called after processing a transaction.
     * If there are 10 transactions, then we make the next block and put it on the map.
     * We then change the currentBlock variable to equate to the new one.
     */
    
    private void nextBlockCheck() {
        Block currentBlock = blockMap.get(currentBlockNumber);
        
        if (currentBlock.isBlockFull()) {
            Block newBlock = currentBlock.makeNextBlock();
            
            blockMap.put(newBlock.getBlockNumber(), newBlock);
            currentBlockNumber++;
        }
    }
}
