import java.util.Scanner;

public class MiniBank {
    private static final BankInfo BANK_INFO = new BankInfo("MiniBank", "Main Branch");

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(BANK_INFO);
            Account[] accounts = {
                new Account("Riya", 5000),
                new Account("Aarav"),
                new Account("Mira", 2500)
            };
            accounts[0].deposit(2000);
            accounts[0].withdraw(3000);
            for (Account account : accounts) {
                System.out.println(account);
            }
            System.out.println("Accounts equal: " + accounts[0].equals(accounts[1]));
            System.out.println("Is account: " + (accounts[0] instanceof Account));
            Customer customer = new Customer("Riya", "riya@example.com", "9876543210",
                    new Customer.Address("1 Bank Street", "Anand", "388001"));
            System.out.println("Customer copy: " + customer.clone().getCustomerId());
            System.out.println("Mobile valid: " + Validator.isValidMobile("9876543210"));
            System.out.println("Mobile valid: " + Validator.isValidMobile("12345"));
            Command command = CommandParser.parse("DEPOSIT AC0001 500");
            System.out.println(command.type() + " " + command.accountNumber() + " " + command.amount());

            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Choose an option (1-5): ");

                if (!scanner.hasNextInt()) {
                    scanner.nextLine();
                    System.out.println("Invalid choice. Enter a number from 1 to 5.");
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice < 1 || choice > 5) {
                    System.out.println("Invalid choice. Enter a number from 1 to 5.");
                    continue;
                }

                MenuOption option = switch (choice) {
                    case 1 -> MenuOption.OPEN_ACCOUNT;
                    case 2 -> MenuOption.DEPOSIT;
                    case 3 -> MenuOption.WITHDRAW;
                    case 4 -> MenuOption.TRANSFER;
                    case 5 -> MenuOption.EXIT;
                    default -> throw new IllegalStateException("Validated choice was out of range");
                };

                String message = switch (option) {
                    case OPEN_ACCOUNT -> "Open Account — to be implemented in a later lab";
                    case DEPOSIT -> "Deposit — to be implemented in a later lab";
                    case WITHDRAW -> "Withdraw — to be implemented in a later lab";
                    case TRANSFER -> "Transfer — to be implemented in a later lab";
                    case EXIT -> "Thank you for using MiniBank. Goodbye!";
                };
                System.out.println(message);

                if (option == MenuOption.EXIT) {
                    running = false;
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. Exit");
    }
}

record BankInfo(String name, String branch) {
}

enum MenuOption {
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    EXIT
}
