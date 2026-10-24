package service;

import java.util.ArrayDeque;
import java.util.Queue;

public class TransactionBuffer {
    private final Queue<Runnable> transactions = new ArrayDeque<>();
    private boolean finished;

    public synchronized void add(Runnable transaction) {
        transactions.add(transaction);
        notifyAll();
    }

    public synchronized Runnable take() throws InterruptedException {
        while (transactions.isEmpty() && !finished) {
            wait();
        }
        return transactions.poll();
    }

    public synchronized void finish() {
        finished = true;
        notifyAll();
    }
}
