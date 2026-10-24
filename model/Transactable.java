package model;

import service.InsufficientFundsException;
import service.InvalidAmountException;

public interface Transactable {
    void deposit(long amount) throws InvalidAmountException;
    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}
