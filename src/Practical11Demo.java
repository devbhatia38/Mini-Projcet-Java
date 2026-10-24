import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import service.ReportGenerator;
import service.StatePersister;
import service.TransactionLog;
import java.nio.file.Path;

public class Practical11Demo {
    public static void main(String[] args) throws Exception {
        Path data = Path.of("data");
        Path log = data.resolve("logs/day.log");
        Account[] accounts = {
            new SavingsAccount("Riya", 5000, 1000),
            new CurrentAccount("Aarav", 2000, 5000)
        };
        StatePersister.save(accounts, data.resolve("accounts.dat"));
        TransactionLog.append(log, "DEPOSIT AC0001 500");
        TransactionLog.append(log, "WITHDRAW AC0002 200");
        for (Account account : StatePersister.load(data.resolve("accounts.dat"))) {
            System.out.println(account);
        }
        System.out.println(ReportGenerator.generate(data.resolve("logs"), data.resolve("report.txt")));
    }
}
