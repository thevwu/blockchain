package ledger;
import java.util.*;

public interface LedgerService {    
    Account createAccount(String address);
    String processTransaction(Transaction transaction);
    Integer getAccountBalance(String address);
    HashMap<String, Double> getAccountBalances();
    Block getBlock(Integer blockNumber);
    Transaction getTransaction(String transactionID);
    void validate();
}