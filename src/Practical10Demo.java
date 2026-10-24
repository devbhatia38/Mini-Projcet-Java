import exception.BankException;
import model.Account;
import model.CurrentAccount;
import service.TransactionBuffer;
import service.TransactionProcessor;

public class Practical10Demo {
    public static void main(String[] args) throws InterruptedException {
        Account account = new CurrentAccount("Batch", 0, 0);
        TransactionProcessor processor = new TransactionProcessor();
        for (int i = 0; i < 100; i++) {
            processor.submit(() -> {
                try {
                    account.deposit(10);
                } catch (BankException exception) {
                    throw new IllegalStateException(exception);
                }
            });
        }
        processor.stop();

        TransactionBuffer buffer = new TransactionBuffer();
        Thread consumer = new Thread(() -> {
            try {
                Runnable transaction;
                while ((transaction = buffer.take()) != null) {
                    transaction.run();
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        for (int i = 0; i < 10; i++) {
            buffer.add(() -> {
                try {
                    account.deposit(1);
                } catch (BankException exception) {
                    throw new IllegalStateException(exception);
                }
            });
        }
        buffer.finish();
        consumer.join();
        System.out.println("Final balance: " + account.getBalance());
    }
}
