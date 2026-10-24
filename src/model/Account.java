package model;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Account implements Transactable, InterestBearing {
    private static long accountCounter = 1;
    @Id
    private final String accountNumber;
    private String ownerName;
    @Positive
    private long balance;
    private boolean active;
    private final ReentrantLock transactionLock = new ReentrantLock();

    protected Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", accountCounter++);
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    protected Account(String ownerName) {
        this(ownerName, 0);
    }

    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
        balance = Math.addExact(balance, amount);
    }

    public boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException("Insufficient funds", Math.max(0, amount - availableBalance()));
        }
        balance -= amount;
        return true;
    }

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("Destination account was not found");
        }
        Account first = accountNumber.compareTo(to.accountNumber) <= 0 ? this : to;
        Account second = first == this ? to : this;
        first.transactionLock.lock();
        second.transactionLock.lock();
        boolean debited = false;
        try {
            withdraw(amount);
            debited = true;
            to.deposit(amount);
        } catch (BankException exception) {
            if (debited) {
                balance += amount;
            }
            throw exception;
        } finally {
            second.transactionLock.unlock();
            first.transactionLock.unlock();
        }
    }

    protected long availableBalance() {
        return balance;
    }

    public ReentrantLock getTransactionLock() {
        return transactionLock;
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
