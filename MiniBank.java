import model.Account;
import model.BankInfo;
import model.MenuOption;
import service.BankService;
import service.BankException;
import service.InvalidAmountException;
import service.CommandParser;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MiniBank {
    private static final BankInfo BANK_INFO = new BankInfo("MiniBank", "Main Branch");

    public static void main(String[] args) {
        BankService bank = new BankService(Path.of("data"));
        try {
            bank.load();
        } catch (IOException | ClassNotFoundException exception) {
            System.err.println("Could not load saved bank data: " + exception.getMessage());
            return;
        }
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(BANK_INFO);
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Choose an option (1-9): ");
                String input = scanner.nextLine().trim();
                try {
                    MenuOption option = MenuOption.values()[Integer.parseInt(input) - 1];
                    switch (option) {
                        case OPEN_ACCOUNT -> openAccount(scanner, bank);
                        case DEPOSIT -> bank.execute(CommandParser.parse("DEPOSIT "
                                + readLine(scanner, "Account number: ") + " "
                                + readAmount(scanner, "Amount: ")));
                        case WITHDRAW -> bank.execute(CommandParser.parse("WITHDRAW "
                                + readLine(scanner, "Account number: ") + " "
                                + readAmount(scanner, "Amount: ")));
                        case TRANSFER -> bank.transfer(readLine(scanner, "From account: "),
                                readLine(scanner, "To account: "), readAmount(scanner, "Amount: "));
                        case STATEMENT -> System.out.println(
                                bank.statement(readLine(scanner, "Account number: ")));
                        case REPORT -> System.out.println(bank.report());
                        case LIST_ACCOUNTS -> printAccounts(bank.listAccounts());
                        case BATCH_DEPOSIT -> batchDeposit(scanner, bank);
                        case EXIT -> {
                            bank.save();
                            System.out.println("Thank you for using MiniBank. Goodbye!");
                            running = false;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
                    System.out.println("Invalid choice. Enter a number from 1 to 9.");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    System.out.println("The operation was interrupted.");
                } catch (BankException | IOException | IllegalArgumentException
                        | IllegalStateException exception) {
                    System.out.println("Operation failed: " + exception.getMessage());
                }
            }
        }
    }

    private static void openAccount(Scanner scanner, BankService bank) throws InvalidAmountException {
        String name = readLine(scanner, "Owner name: ");
        String email = readLine(scanner, "Email: ");
        String mobile = readLine(scanner, "Mobile: ");
        long openingBalance = readAmount(scanner, "Opening balance: ");
        Account account = bank.openAccount(name, openingBalance, email, mobile);
        System.out.println("Account opened: " + account.getAccountNumber());
    }

    private static void batchDeposit(Scanner scanner, BankService bank) throws InterruptedException {
        String accountNumber = readLine(scanner, "Account number: ");
        String[] values = readLine(scanner, "Deposit amounts separated by commas: ").split(",");
        List<Long> amounts = new ArrayList<>();
        for (String value : values) {
            amounts.add(Long.parseLong(value.trim()));
        }
        bank.processBatch(bank.depositBatch(accountNumber, amounts));
        System.out.println("Batch completed.");
    }

    private static void printAccounts(List<Account> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static long readAmount(Scanner scanner, String prompt) {
        return Long.parseLong(readLine(scanner, prompt));
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. View statement");
        System.out.println("6. Run report");
        System.out.println("7. List accounts");
        System.out.println("8. Batch deposit");
        System.out.println("9. Exit");
    }
}
