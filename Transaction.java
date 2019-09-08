package ledger;

public class Transaction {
    String transactionID;
    double amount;
    double fee;
    String payload;
    
    public Transaction(String transactionID, double amount, double fee, String payload) {
        this.transactionID = transactionID;
        this.amount = amount;
        this.fee = fee;
        this.payload = payload;
    }
}