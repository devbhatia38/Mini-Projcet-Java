import exception.InsufficientFundsException;
import model.Account;
import service.BankService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class BankServiceTest {
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("minibank-tests");
        try {
            verifiesDeposit(directory.resolve("deposit"));
            verifiesInsufficientFunds(directory.resolve("withdraw"));
            verifiesConcurrentDeposits(directory.resolve("concurrent"));
            verifiesPersistence(directory.resolve("persistence"));
            System.out.println("All MiniBank tests passed.");
        } finally {
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static void verifiesDeposit(Path directory) throws Exception {
        BankService bank = new BankService(directory);
        Account account = bank.openAccount("Riya", 1000, "riya@example.com", "9876543210");
        bank.deposit(account.getAccountNumber(), 250);
        check(account.getBalance() == 1250, "Deposit did not update the balance");
    }

    private static void verifiesInsufficientFunds(Path directory) throws Exception {
        BankService bank = new BankService(directory);
        Account account = bank.openAccount("Aarav", 1000, "aarav@example.com", "9876543211");
        try {
            bank.withdraw(account.getAccountNumber(), 2000);
            throw new AssertionError("Expected an insufficient-funds exception");
        } catch (InsufficientFundsException exception) {
            check(exception.getShortfall() == 1000, "Incorrect withdrawal shortfall");
            check(account.getBalance() == 1000, "Failed withdrawal changed the balance");
        }
    }

    private static void verifiesConcurrentDeposits(Path directory) throws Exception {
        BankService bank = new BankService(directory);
        Account account = bank.openAccount("Mira", 1000, "mira@example.com", "9876543212");
        ArrayList<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tasks.add(() -> {
                try {
                    account.deposit(1);
                } catch (Exception exception) {
                    throw new IllegalStateException(exception);
                }
            });
        }
        bank.processBatch(tasks);
        check(account.getBalance() == 2000, "Concurrent deposits lost an update");
    }

    private static void verifiesPersistence(Path directory) throws Exception {
        BankService bank = new BankService(directory);
        Account account = bank.openAccount("Dev", 1500, "dev@example.com", "9876543213");
        bank.save();
        BankService restored = new BankService(directory);
        restored.load();
        check(restored.findAccount(account.getAccountNumber()).getBalance() == 1500,
                "Saved account did not reload correctly");
        Account next = restored.openAccount("Nia", 500, "nia@example.com", "9876543214");
        check(!next.getAccountNumber().equals(account.getAccountNumber()), "Account number was reused");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
