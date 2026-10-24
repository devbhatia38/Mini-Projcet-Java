import java.util.Objects;

public class Account {
    private static long accountCounter = 1;
    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", accountCounter++);
        this.ownerName = ownerName;
        this.balance = Math.max(0, openingBalance);
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public void deposit(long amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(long amount) {
        if (amount <= 0 || balance < amount) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + balance + " | " + (active ? "active" : "inactive");
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Account account && accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
