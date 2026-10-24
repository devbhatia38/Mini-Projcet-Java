package model;

public interface InterestBearing {
    double interestRate();
    long getBalance();

    default double yearlyInterest() {
        return interestRate() * getBalance() / 100.0;
    }
}
