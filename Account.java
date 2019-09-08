package ledger;

public class Account {
    String address;
    double balance;
    
    public Account(String address) {
        this.address = address;
        this.balance = 0;
    }
}