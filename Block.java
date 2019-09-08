package ledger;
import java.util.*;

public class Block {
    int blockNumber;
    String previousHash;
    String hash;
    Transaction[] transactionList;
    HashMap<String, Double> accountBalanceMap;
    Block previousBlock;
    
    public Block(int blockNumber, String previousHash, String hash, Transaction[] transactionList, 
                 HashMap<String, Double> accountBalanceMap, Block previousBlock) {
        this.blockNumber = blockNumber;
        this.previousHash = previousHash;
        this.hash = hash;
        this.transactionList = transactionList;
        this.accountBalanceMap = accountBalanceMap;
        this.previousBlock = previousBlock;
    }
}