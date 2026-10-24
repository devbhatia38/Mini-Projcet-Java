package service;
import model.Account;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AccountStore {
    private final HashMap<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    public synchronized void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public synchronized Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public synchronized ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public synchronized ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public synchronized ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sorted = getAll();
        sorted.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return sorted;
    }

    public synchronized List<Account> aboveBalance(long minimum) {
        return accounts.values().stream().filter(account -> account.getBalance() > minimum).toList();
    }

    public synchronized Map<String, Account> asMap() {
        return new HashMap<>(accounts);
    }
}
