package model;

import model.annotation.Id;
import model.annotation.Positive;
import java.util.Objects;

public abstract class Account implements Transactable, InterestBearing {
    private static long accountCounter = 1;
    @Id
    private final String accountNumber;
    private String ownerName;
    @Positive
    private long balance;
    private boolean active;

    protected Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", accountCounter++);
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    protected Account(String ownerName) {
        this(ownerName, 0);
    }

    public void deposit(long amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(long amount) {
        if (amount <= 0 || !canWithdraw(amount)) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public double monthlyInterest() {
        return balance * interestRate() / 1200.0;
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
