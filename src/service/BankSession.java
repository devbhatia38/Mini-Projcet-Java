package service;

public final class BankSession implements AutoCloseable {
    @Override
    public void close() {
        System.out.println("Bank session closed");
    }
}
