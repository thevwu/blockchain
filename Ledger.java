package ledger;
import java.util.*;

public class Ledger implements LedgerService {
    String name = "";
    String description = "";
    String seed = "";
    
    public Ledger(String name, String description, String seed) { 
        this.name = name;
        this.description = description;
        this.seed = seed;
    }

    public Account createAccount(String address) {
        return new Account(address);
    }
    
    public String processTransaction(Transaction transaction) {
        return "";
    }
    
    public Integer getAccountBalance(String address) {
        return 0;
    }
    
    public HashMap<String, Double> getAccountBalances() {
        return new HashMap<String, Double>();
    }
    
    public Block getBlock(Integer blockNumber) {
        return new Block(2, "1", "2", new Transaction[]{new Transaction("1234", 0.0, 0.0, "")}, new HashMap<String, Double>(), null);
    }
    
    public Transaction getTransaction(String transactionID) {
        return new Transaction(transactionID, 0.0, 0.0, "");
    }    
    
    public void validate() {
        
    }
}
