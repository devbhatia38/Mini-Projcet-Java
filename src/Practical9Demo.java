import model.CurrentAccount;
import model.Account;
import service.AccountWorker;

public class Practical9Demo {
    public static void main(String[] args) throws InterruptedException {
        Account account = new CurrentAccount("Threaded", 0, 0);
        Thread[] workers = new Thread[10];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Thread(new AccountWorker(account, 1000, 1), "Worker-" + (i + 1));
            workers[i].start();
        }
        for (Thread worker : workers) {
            worker.join();
        }
        System.out.println("Final balance: " + account.getBalance());
    }
}
