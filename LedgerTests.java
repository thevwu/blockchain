package ledger;
import junit.framework.TestCase;
import java.util.*;

public class LedgerTests extends TestCase {
    LedgerService ledgerService;
    
    public void setUp() {
        ledgerService = new Ledger("name", "description", "seed");
    }

    public void testReturnTypes() {
       assertTrue( ledgerService.createAccount("") instanceof Account );
       assertTrue( ledgerService.processTransaction(new Transaction("", 0, 0, "")) instanceof String );
       assertTrue( ledgerService.getAccountBalance("") instanceof Integer );
       assertTrue( ledgerService.getAccountBalances() instanceof HashMap );
       assertTrue( ledgerService.getBlock(0) instanceof Block );
       assertTrue( ledgerService.getTransaction("") instanceof Transaction );
    }
    
    public void testAccountCreation() {
        Account account = new Account("blah");
        
        assertEquals ( account.address, "blah" );
        assertEquals ( account.balance, 0.0 );
    }
    
    public void testTransactionCreation() {
        Transaction transaction = new Transaction("id", 1000.50, 10.50, "payload");
        
        assertEquals ( transaction.transactionID, "id" );
        assertEquals ( transaction.amount, 1000.50 );
        assertEquals ( transaction.fee, 10.50 );
        assertEquals ( transaction.payload, "payload" );
    }
    
    public void testLedgerCreation() {
        Ledger ledger = (Ledger)ledgerService;
        
        assertEquals ( ledger.name, "name" );
        assertEquals ( ledger.description, "description" );
        assertEquals ( ledger.seed, "seed" );
    }
    
    public void testBlockCreation() {
        Transaction transaction = new Transaction("1234", 0.0, 0.0, "");
        Block genesisBlock = new Block(1, "", "1", new Transaction[]{transaction}, 
                                new HashMap<String, Double>(), null);
        Block block = new Block(2, "1", "2", new Transaction[]{transaction}, 
                                new HashMap<String, Double>(), genesisBlock);
        
        assertEquals ( genesisBlock.blockNumber, 1 );
        assertEquals ( genesisBlock.hash, "1" );
        assertEquals ( genesisBlock.previousHash, "" );
        
        assertEquals ( block.blockNumber, 2 );
        assertEquals ( block.previousHash, "1" );
        assertEquals ( block.hash, "2" );
        assertEquals ( block.transactionList[0], transaction );
        assertTrue ( block.accountBalanceMap instanceof HashMap );
        assertEquals ( block.previousBlock, genesisBlock );   
    }
}