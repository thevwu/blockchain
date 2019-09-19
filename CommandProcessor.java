package com.cscie97.ledger;
import java.util.*;

public class CommandProcessor {
    //There is a copy of the ledger here because I did not have time to include persistence.
    static private Ledger ledger;
    
    static public void processCommand(String command) {
        String lowerCaseCommand = command.toLowerCase();  //Commands are automatically converted to lower-case.  This is a line
        String[] commands = lowerCaseCommand.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); //This splits single line commands by their spaces, while ignoring spaces inside of quotation marks.
        String baseCommand = commands[0]; //The first string of a line, to determine what action we will take.
        commands = Arrays.copyOfRange(commands, 1, commands.length); //The first string of a line is not passed as an argument

        System.out.println(command);
        
        if (ledger == null && !baseCommand.equals("create-ledger")) {
            CommandProcessorException commandException = new CommandProcessorException(baseCommand, "Create a ledger first!");
            commandException.printException();
            
            return;
        }
        
        switch(baseCommand) {
            case "create-ledger": 
                createLedger(commands);
                break;
            case "create-account":
                createAccount(commands);
                break;
            case "process-transaction":
                processTransaction(commands);
                break;
            case "get-account-balance":
                getAccountBalance(commands);
                break;
            case "get-account-balances":
                getAccountBalances(commands);
                break;
            case "get-block":
                getBlock(commands);
                break;
            case "get-transaction":
                getTransaction(commands);
                break;
            case "validate":
                validate(commands);
                break;
            default:
                CommandProcessorException commandException = new CommandProcessorException(baseCommand, "Invalid command!");
                commandException.printException();
                break;
        }
    }
    
    static public void processCommandFile(String commandFile) {
        commandFile = commandFile.replaceAll("(?m)^#.*", "\n"); //This turns all commented # lines into new lines

        String[] commandLines = commandFile.split("([\r\n]+)"); //This removes all new lines from processing
            
        for (String command : commandLines) {
            processCommand(command);
        }
    }
    
    /**
     * Utility methods, these are responsible for creation and getting.
     * If they must print then the printing is offloaded to another command.
     * Exceptions are responsible for their own printing.
     */
    
    static private void createLedger(String[] commands) {
        try {
            String name = commands[0];
            String description = commands[2];
            String seed = commands[4];
            
            ledger = new Ledger(name, description, seed);
            
            if(ledger == null) {
                return;
            }
            
            String output = "Ledger created successfully!  Name: " + name + ", Description: " + description + ", Seed: " + seed;
            
            printOutput(commands, output);
        } catch(IndexOutOfBoundsException e) {
            CommandProcessorException commandException = new CommandProcessorException("create-ledger", "Invalid syntax!");
            commandException.printException();
        }
    }
    
    static private void createAccount(String[] commands) {
        try {
            String address = commands[0];
            
            String addressCreated = ledger.createAccount(address);
            
            if(addressCreated == null) {
                return;
            }
            
            String output = "Account created successfully!  Address: " + address;
            
            printOutput(commands, output);
        } catch(IndexOutOfBoundsException e) {
            CommandProcessorException commandException = new CommandProcessorException("create-account", "Invalid syntax!");
            commandException.printException();
        }
    }
    
    static private void processTransaction(String[] commands) {
        try {
            String transactionID = commands[0];
            int amount = Integer.valueOf​(commands[2]);
            int fee = Integer.valueOf(commands[4]);
            String payload = commands[6];
            String payer = commands[8];
            String receiver = commands[10];
            
            Transaction transaction = new Transaction(transactionID, amount, fee, payload, payer, receiver);
            
            String correctTransactionID = ledger.processTransaction(transaction);
            
            if(correctTransactionID == null) {
                return;
            }
            
            String output = "Transaction processed successfully!  ID: " + transactionID + ", Amount: " + amount + ", Fee: " + fee +
                ", Payload: " + payload + ", Payer: " + payer + ", Receiver: " + receiver;
            
            printOutput(commands, output);
        } catch(IndexOutOfBoundsException e) {
            CommandProcessorException commandException = new CommandProcessorException("process-transaction", "Invalid syntax!");
            commandException.printException();
        } catch(NumberFormatException e) {
            CommandProcessorException commandException = new CommandProcessorException("process-transaction", "Can't go below 0 for amount, 10 for fee.  Can't go higher than 2147483647.");
            commandException.printException();
        }
    }
    
    static private void getAccountBalance(String[] commands) {
        try {
            String address = commands[0];
            Integer balance = ledger.getAccountBalance(address);
            
            if (balance == null) {
                return;
            }
            
            String output = "Account: " + address + ", Balance: " + balance;
            
            printOutput(commands, output);
        } catch(IndexOutOfBoundsException e) {
            CommandProcessorException commandException = new CommandProcessorException("get-account-balance", "Invalid syntax!");
            commandException.printException();
        } 
    }
    
    static private void getAccountBalances(String[] commands) {
        try {
            HashMap<String, Integer> accountBalances = ledger.getAccountBalances();
            
            StringBuilder output = new StringBuilder();
                
            accountBalances.forEach((address,balance) -> output.append("Account: " + address + ", Balance: " + balance + "\n"));  
            
            printOutput(commands, output.toString());
        } catch(NullPointerException e) {
            CommandProcessorException commandException = new CommandProcessorException("get-account-balance", "No accounts!");
            commandException.printException();
        }
    }
    
    //Blocks are outputted according to all their values, so their number, hash, previous hash, previous block's number,
    //transaction list, and accounts with their balances.
    
    static private void getBlock(String[] commands) {
        try {
            int blockNumber = Integer.valueOf(commands[0]);
            Block block = ledger.getBlock(blockNumber);
            
            if (block == null) {
                return;
            }
            
            StringBuilder output = new StringBuilder();
                
            output.append("Block #" + block.getBlockNumber() + "\n");
            output.append("Block Previous Hash: " + block.getPreviousHash() + "\n");
            output.append("Block Hash: " + block.getHash() + "\n");
            
            if (block.getPreviousBlock() == null) {
                output.append("Previous Block #None\n");
            } else {
                output.append("Previous Block #" + block.getPreviousBlock().getBlockNumber() + "\n");
            }
            
            output.append("Transaction List" + "\n\n");
            
            for (Transaction transaction : block.getTransactionList()) {
                output.append("Transaction #: " + transaction.getTransactionID() + ", Amount: " + transaction.getAmount() + ", Fee: "
                                + transaction.getFee() + ", Payload: " + transaction.getPayload() + ", Payer: " + transaction.getPayer()
                                + ", Receiver: " + transaction.getReceiver() + "\n");
            }
            
            output.append("Accounts and Balances" + "\n\n");
            
            for (Account account : block.getAccounts()) {
                output.append("Account: " + account.getAddress() + ", Balance: " + account.getBalance() + "\n");
            }
            
            printOutput(commands, output.toString());
        } catch(IndexOutOfBoundsException e) {
            CommandProcessorException commandException = new CommandProcessorException("get-block", "Invalid syntax!");
            commandException.printException();
        } 
    }
    
    static private void getTransaction(String[] commands) {
        try {
            String transactionID = commands[0];
            Transaction transaction = ledger.getTransaction(transactionID);
            
            if (transaction == null) {
                return;
            }
            
            String output = "Transaction #: " + transaction.getTransactionID() + ", Amount: " + transaction.getAmount() + ", Fee: "
                                + transaction.getFee() + ", Payload: " + transaction.getPayload() + ", Payer: " + transaction.getPayer()
                                + ", Receiver: " + transaction.getReceiver();
            
            printOutput(commands, output);
        } catch(IndexOutOfBoundsException e) {
            CommandProcessorException commandException = new CommandProcessorException("get-transaction", "Invalid syntax!");
            commandException.printException();
        } 
    }
    
    static private void printOutput(String[] commands, String output) {
        System.out.println(output + "\n");
    }
    
    static private void validate(String[] commands) {
        boolean successful = ledger.validate();
        
        if (successful) {
            System.out.println("Validate successful!");
        } else {
            System.out.println("Validate failed!");
        }
    }
}