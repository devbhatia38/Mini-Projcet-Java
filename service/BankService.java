package service;
import model.Account;
import model.SavingsAccount;
import util.StatementFormatter;
import util.Validator;
import util.ReportGenerator;
import util.StatePersister;
import util.TransactionLog;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
public class BankService {
    private final Repository<Account> accounts = new Repository<>();
    private final Path dataDirectory;

    public BankService(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public Account openAccount(String ownerName, long openingBalance, String email, String mobile)
            throws InvalidAmountException {
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Owner name is required");
        }
        if (openingBalance <= 0) {
            throw new InvalidAmountException("Opening balance must be greater than zero");
        }
        if (!Validator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (!Validator.isValidMobile(mobile)) {
            throw new IllegalArgumentException("Invalid mobile number");
        }
        Account account = new SavingsAccount(ownerName, openingBalance, 0);
        accounts.save(account);
        return account;
    }

    public void deposit(String accountNumber, long amount) throws BankException, IOException {
        Account account = findAccount(accountNumber);
        account.deposit(amount);
        TransactionLog.append(dataDirectory.resolve("logs/transactions.log"),
                "DEPOSIT " + accountNumber + " " + amount);
    }

    public void withdraw(String accountNumber, long amount) throws BankException, IOException {
        Account account = findAccount(accountNumber);
        account.withdraw(amount);
        TransactionLog.append(dataDirectory.resolve("logs/transactions.log"),
                "WITHDRAW " + accountNumber + " " + amount);
    }

    public void transfer(String fromAccount, String toAccount, long amount) throws BankException, IOException {
        Account source = findAccount(fromAccount);
        Account destination = findAccount(toAccount);
        source.transfer(destination, amount);
        TransactionLog.append(dataDirectory.resolve("logs/transactions.log"),
                "WITHDRAW " + fromAccount + " " + amount);
        TransactionLog.append(dataDirectory.resolve("logs/transactions.log"),
                "DEPOSIT " + toAccount + " " + amount);
    }

    public String statement(String accountNumber) throws AccountNotFoundException {
        return StatementFormatter.buildStatement(findAccount(accountNumber));
    }

    public ArrayList<Account> listAccounts() {
        return accounts.findAll();
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        return account;
    }

    public void load() throws IOException, ClassNotFoundException {
        Path stateFile = dataDirectory.resolve("accounts.dat");
        if (java.nio.file.Files.exists(stateFile)) {
            for (Account account : StatePersister.load(stateFile)) {
                accounts.save(account);
            }
        }
    }

    public void save() throws IOException {
        StatePersister.save(accounts.findAll().toArray(Account[]::new), dataDirectory.resolve("accounts.dat"));
    }

    public String report() throws IOException {
        return ReportGenerator.generate(dataDirectory.resolve("logs"), dataDirectory.resolve("report.txt"));
    }

    public void processBatch(List<Runnable> tasks) throws InterruptedException {
        TransactionProcessor processor = new TransactionProcessor();
        for (Account account : accounts.findAll()) {
            processor.addAccount(account);
        }
        CountDownLatch finished = new CountDownLatch(tasks.size());
        AtomicReference<RuntimeException> failure = new AtomicReference<>();
        for (Runnable task : tasks) {
            processor.submit(() -> {
                try {
                    task.run();
                } catch (RuntimeException exception) {
                    failure.compareAndSet(null, exception);
                } finally {
                    finished.countDown();
                }
            });
        }
        try {
            finished.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw exception;
        } finally {
            processor.stop();
        }
        if (failure.get() != null) {
            throw new IllegalStateException("A batch transaction failed", failure.get());
        }
    }

    public List<Runnable> depositBatch(String accountNumber, List<Long> amounts) {
        ArrayList<Runnable> tasks = new ArrayList<>();
        for (long amount : amounts) {
            tasks.add(() -> {
                try {
                    deposit(accountNumber, amount);
                } catch (BankException | IOException exception) {
                    throw new IllegalStateException(exception);
                }
            });
        }
        return tasks;
    }

    public void execute(Command command) throws BankException, IOException {
        if (command.type() == TransactionType.DEPOSIT) {
            deposit(command.accountNumber(), command.amount());
        } else if (command.type() == TransactionType.WITHDRAW) {
            withdraw(command.accountNumber(), command.amount());
        } else {
            throw new IllegalArgumentException("Transfer commands require a destination account");
        }
    }
}
