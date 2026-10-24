package model;
import java.io.Serial;
public class CurrentAccount extends Account {
    @Serial
    private static final long serialVersionUID = 1L;
    private final long overdraftLimit;

    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return amount <= getBalance() + overdraftLimit;
    }

    @Override
    protected long availableBalance() {
        return Math.max(0, getBalance() + overdraftLimit);
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }
}
