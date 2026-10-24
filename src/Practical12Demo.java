import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import service.AccountStore;
import service.TransactionProcessor;

public class Practical12Demo {
    public static void main(String[] args) {
        AccountStore store = new AccountStore();
        store.add(new SavingsAccount("Riya", 5000, 500));
        store.add(new CurrentAccount("Aarav", 2000, 5000));
        store.add(new SavingsAccount("Mira", 9000, 1000));
        for (Account account : store.sortedByBalance()) {
            System.out.println(account);
        }
        System.out.println("Lookup: " + store.get("AC0002"));
        store.addTransaction("DEPOSIT AC0001 500");
        TransactionProcessor processor = new TransactionProcessor();
        for (Account account : store.getAll()) {
            processor.addAccount(account);
        }
        System.out.println("Thread-safe accounts: " + processor.getAccounts().size());
    }
}
